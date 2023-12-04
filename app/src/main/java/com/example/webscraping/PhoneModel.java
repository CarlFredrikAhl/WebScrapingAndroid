package com.example.webscraping;

public class PhoneModel {
    private String name;
    private String imageUrl;

    public PhoneModel() { }

    public PhoneModel(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

