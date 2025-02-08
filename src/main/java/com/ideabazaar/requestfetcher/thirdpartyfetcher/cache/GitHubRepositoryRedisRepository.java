package com.ideabazaar.requestfetcher.thirdpartyfetcher.cache;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GitHubRepositoryRedisRepository extends CrudRepository<GitHubRepositoryCache, String> {
    Optional<GitHubRepositoryCache> findByUrl(String url);
}