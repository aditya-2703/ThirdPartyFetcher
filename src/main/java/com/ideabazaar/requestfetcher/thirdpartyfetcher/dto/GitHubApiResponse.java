package com.ideabazaar.requestfetcher.thirdpartyfetcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GitHubApiResponse {
    private String name;
    private String description;
    @JsonProperty("stargazers_count")
    private Integer stargazersCount;
    @JsonProperty("forks_count")
    private Integer forksCount;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("topics")
    private List<String> topics;
    @JsonProperty("language")
    private String language;

    public GitHubApiResponse(){}

    public GitHubApiResponse(String name, String description, Integer stargazersCount, Integer forksCount, String htmlUrl, List<String> topics, String language) {
        this.name = name;
        this.description = description;
        this.stargazersCount = stargazersCount;
        this.forksCount = forksCount;
        this.htmlUrl = htmlUrl;
        this.topics = topics;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStargazersCount() {
        return stargazersCount;
    }

    public void setStargazersCount(Integer stargazersCount) {
        this.stargazersCount = stargazersCount;
    }

    public Integer getForksCount() {
        return forksCount;
    }

    public void setForksCount(Integer forksCount) {
        this.forksCount = forksCount;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
