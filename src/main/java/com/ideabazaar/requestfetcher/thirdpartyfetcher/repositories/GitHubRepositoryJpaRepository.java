package com.ideabazaar.requestfetcher.thirdpartyfetcher.repositories;

import com.ideabazaar.requestfetcher.thirdpartyfetcher.model.GitHubRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubRepositoryJpaRepository extends JpaRepository<GitHubRepositoryEntity, Long> {
    Optional<GitHubRepositoryEntity> findByUrl(String url);
    boolean existsByUrl(String url);
    List<GitHubRepositoryEntity> findAll();

}
