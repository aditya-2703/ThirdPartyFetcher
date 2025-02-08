// GitHubRepository.java
package com.ideabazaar.requestfetcher.thirdpartyfetcher.dto;

import java.util.List;
import lombok.Data;

@Data
public class GitHubRepository {
    private String title;
    private String description;
    private String stars;
    private String forks;
    private String url;
    private List<String> tags;

    public GitHubRepository(){}

    public GitHubRepository(String title, String description, String stars, String forks, String url, List<String> tags) {
        this.title = title;
        this.description = description;
        this.stars = stars;
        this.forks = forks;
        this.url = url;
        this.tags = tags;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
