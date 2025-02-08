package com.ideabazaar.requestfetcher.thirdpartyfetcher.controller;

import com.ideabazaar.requestfetcher.thirdpartyfetcher.dto.GitHubRepository;
import com.ideabazaar.requestfetcher.thirdpartyfetcher.service.GitHubService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    // Constructor-based dependency injection
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    /**
     * Endpoint to fetch multiple GitHub repositories from a list of URLs.
     * The fetched repositories will be stored in the database and returned as DTOs.
     *
     * @param urls List of GitHub repository URLs
     * @return List of GitHubRepository DTOs
     */
    @PostMapping("/repositories")
    public List<GitHubRepository> fetchRepositories(@RequestBody(required = false) List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return gitHubService.fetchAllRepositoriesFromDatabase();
        }
        return gitHubService.fetchRepositoriesFromUrls(urls);
    }




    /**
     * Endpoint to fetch a single GitHub repository from a given URL.
     * If the repository exists in the cache or database, it will be returned from there.
     * Otherwise, it will fetch from GitHub API, store it, and return the response.
     *
     * @param url GitHub repository URL
     * @return GitHubRepository DTO
     */
    @GetMapping("/repository")
    public GitHubRepository fetchRepository(@RequestParam String url) {
        return gitHubService.fetchAndSaveRepository(url);
    }

    @GetMapping("/repositories")
    public List<GitHubRepository> fetchRepositories() {
        return gitHubService.fetchAllRepositoriesFromDatabase();
    }
}