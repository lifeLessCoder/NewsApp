package com.livelycoder.newsapp.models;

public class News {
    private String publishedDate;
    private String sectionName;
    private String title;
    private String authorName;
    private String webUrl;
    private String thumbnail;

    public News(String publishedDate, String sectionName, String title, String authorName, String webUrl, String thumbnail) {
        this.publishedDate = publishedDate;
        this.sectionName = sectionName;
        this.title = title;
        this.authorName = authorName;
        this.webUrl = webUrl;
        this.thumbnail = thumbnail;
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

    public String getThumbnail() {
        return thumbnail;
    }
}
