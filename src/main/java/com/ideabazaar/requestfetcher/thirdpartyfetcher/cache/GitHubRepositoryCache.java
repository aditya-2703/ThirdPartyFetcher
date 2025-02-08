package com.ideabazaar.requestfetcher.thirdpartyfetcher.cache;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

@Data
@RedisHash("github_repository")
public class GitHubRepositoryCache implements Serializable {
    @Id
    private String id;
    
    @Indexed
    private String url;
    private String title;
    private String description;
    private String stars;
    private String forks;
    private List<String> tags;
    private long timestamp;

    public GitHubRepositoryCache(String id, String url, String title, String description, String stars, String forks, List<String> tags, long timestamp) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.description = description;
        this.stars = stars;
        this.forks = forks;
        this.tags = tags;
        this.timestamp = timestamp;
    }

    public GitHubRepositoryCache() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getForks() {
        return forks;
    }

    public void setForks(String forks) {
        this.forks = forks;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}