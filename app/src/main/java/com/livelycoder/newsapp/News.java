package com.livelycoder.newsapp;

public class News {
    private String publishedDate;
    private String sectionName;
    private String title;
    private String authorName;
    private String webUrl;

    News(String publishedDate, String sectionName, String title, String authorName, String webUrl) {
        this.publishedDate = publishedDate;
        this.sectionName = sectionName;
        this.title = title;
        this.authorName = authorName;
        this.webUrl = webUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getWebUrl() {
        return webUrl;
    }
}
