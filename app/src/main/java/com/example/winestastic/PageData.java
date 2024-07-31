package com.example.winestastic;


public class PageData {
    private String imageUrl;
    private String title;
    private String description;

    public PageData(String imageUrl, String title, String description) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}