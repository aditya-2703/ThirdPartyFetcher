package com.ideabazaar.requestfetcher.thirdpartyfetcher.service;


import com.ideabazaar.requestfetcher.thirdpartyfetcher.cache.GitHubRepositoryCache;
import com.ideabazaar.requestfetcher.thirdpartyfetcher.cache.GitHubRepositoryRedisRepository;
import com.ideabazaar.requestfetcher.thirdpartyfetcher.dto.GitHubApiResponse;
import com.ideabazaar.requestfetcher.thirdpartyfetcher.dto.GitHubRepository;
import com.ideabazaar.requestfetcher.thirdpartyfetcher.model.GitHubRepositoryEntity;
import com.ideabazaar.requestfetcher.thirdpartyfetcher.repositories.GitHubRepositoryJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GitHubService {

    private final RestTemplate restTemplate;
    private final String githubToken;
    private final GitHubRepositoryJpaRepository jpaRepository;
    private final GitHubRepositoryRedisRepository redisRepository;
    private final Duration cacheExpiration;

    public GitHubService(
            @Value("${github.api.token}") String githubToken,
            @Value("${github.cache.expiration-hours:24}") long cacheExpirationHours,
            GitHubRepositoryJpaRepository jpaRepository,
            GitHubRepositoryRedisRepository redisRepository
    ) {
        this.restTemplate = new RestTemplate();
        this.githubToken = githubToken;
        this.jpaRepository = jpaRepository;
        this.redisRepository = redisRepository;
        this.cacheExpiration = Duration.ofHours(cacheExpirationHours);
    }

    @Transactional
    public List<GitHubRepository> fetchRepositoriesFromUrls(List<String> urls) {
        return urls.stream()
                .map(this::fetchAndSaveRepository)
                .filter(repo -> repo != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public GitHubRepository fetchAndSaveRepository(String url) {
        // Check Redis cache first
        Optional<GitHubRepositoryCache> cachedRepo = redisRepository.findByUrl(url);
        if (cachedRepo.isPresent() && !isCacheExpired(cachedRepo.get())) {
            return convertFromCache(cachedRepo.get());
        }

        // Check database
        Optional<GitHubRepositoryEntity> existingRepo = jpaRepository.findByUrl(url);
        if (existingRepo.isPresent()) {
            // Update if data is old
            if (Duration.between(existingRepo.get().getUpdatedAt(), java.time.LocalDateTime.now()).toHours() >= 24) {
                return updateExistingRepository(url, existingRepo.get());
            }
            return convertFromEntity(existingRepo.get());
        }

        // Fetch from GitHub API
        try {
            GitHubApiResponse apiResponse = fetchFromGitHub(url);
            if (apiResponse == null) return null;

            // Save to database
            GitHubRepositoryEntity entity = convertToEntity(apiResponse);
            entity.setUrl(url);
            entity = jpaRepository.save(entity);

            // Save to Redis cache
            saveToCache(entity);

            return convertFromEntity(entity);
        } catch (Exception e) {
            log.error("Error fetching repository from URL: " + url, e);
            return null;
        }
    }

    public List<GitHubRepository> fetchAllRepositoriesFromDatabase() {
        List<GitHubRepositoryEntity> allEntities = jpaRepository.findAll();
        return allEntities.stream()
                .map(this::convertFromEntity)
                .collect(Collectors.toList());
    }


    private GitHubApiResponse fetchFromGitHub(String url) {
        try {
            String[] urlParts = url.split("/");
            String owner = urlParts[urlParts.length - 2];
            String repo = urlParts[urlParts.length - 1];

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String apiUrl = String.format("https://api.github.com/repos/%s/%s", owner, repo);
            ResponseEntity<GitHubApiResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    GitHubApiResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching from GitHub API: " + url, e);
            return null;
        }
    }

    private GitHubRepository updateExistingRepository(String url, GitHubRepositoryEntity existingEntity) {
        GitHubApiResponse apiResponse = fetchFromGitHub(url);
        if (apiResponse == null) return convertFromEntity(existingEntity);

        // Update entity
        updateEntity(existingEntity, apiResponse);
        existingEntity = jpaRepository.save(existingEntity);

        // Update cache
        saveToCache(existingEntity);

        return convertFromEntity(existingEntity);
    }

    private void saveToCache(GitHubRepositoryEntity entity) {
        GitHubRepositoryCache cache = new GitHubRepositoryCache();
        cache.setUrl(entity.getUrl());
        cache.setTitle(entity.getTitle());
        cache.setDescription(entity.getDescription());
        cache.setStars(entity.getStars());
        cache.setForks(entity.getForks());
        cache.setTags(entity.getTags());
        cache.setTimestamp(System.currentTimeMillis());
        redisRepository.save(cache);
    }

    private boolean isCacheExpired(GitHubRepositoryCache cache) {
        return System.currentTimeMillis() - cache.getTimestamp() > cacheExpiration.toMillis();
    }

    // Conversion methods
    private GitHubRepositoryEntity convertToEntity(GitHubApiResponse apiResponse) {
        GitHubRepositoryEntity entity = new GitHubRepositoryEntity();
        updateEntity(entity, apiResponse);
        return entity;
    }

    private void updateEntity(GitHubRepositoryEntity entity, GitHubApiResponse apiResponse) {
        entity.setTitle(apiResponse.getName());
        entity.setDescription(apiResponse.getDescription());
        entity.setStars(formatNumber(apiResponse.getStargazersCount()));
        entity.setForks(formatNumber(apiResponse.getForksCount()));
        entity.setRepositoryUrl(apiResponse.getHtmlUrl());

        List<String> tags = new ArrayList<>();
        if (apiResponse.getLanguage() != null) {
            tags.add(apiResponse.getLanguage());
        }
        if (apiResponse.getTopics() != null) {
            tags.addAll(apiResponse.getTopics());
        }
        entity.setTags(tags);
    }

    private GitHubRepository convertFromEntity(GitHubRepositoryEntity entity) {
        GitHubRepository repo = new GitHubRepository();
        repo.setTitle(entity.getTitle());
        repo.setDescription(entity.getDescription());
        repo.setStars(entity.getStars());
        repo.setForks(entity.getForks());
        repo.setUrl(entity.getUrl());
        repo.setTags(entity.getTags());
        return repo;
    }

    private GitHubRepository convertFromCache(GitHubRepositoryCache cache) {
        GitHubRepository repo = new GitHubRepository();
        repo.setTitle(cache.getTitle());
        repo.setDescription(cache.getDescription());
        repo.setStars(cache.getStars());
        repo.setForks(cache.getForks());
        repo.setUrl(cache.getUrl());
        repo.setTags(cache.getTags());
        return repo;
    }

    private String formatNumber(Integer number) {
        if (number == null) return "0";

        if (number < 1000) return String.valueOf(number);
        if (number < 10000) return String.format("%.1fk", number / 1000.0);
        if (number < 1000000) return String.format("%dk", number / 1000);
        return String.format("%.1fm", number / 1000000.0);
    }
}