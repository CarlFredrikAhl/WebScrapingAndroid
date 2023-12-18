package com.example.webscraping;

public class PhoneModel {
    private String name;
    private String imageUrl;
    private String screenSize;
    private String resolution;
    private String screenType;
    private String phoneWidth;

    public PhoneModel() { }

    public PhoneModel(String name, String imageUrl, String screenSize, String resolution, String screenType, String phoneWidth) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.screenSize = screenSize;
        this.resolution = resolution;
        this.screenType = screenType;
        this.phoneWidth = phoneWidth;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public String getScreenType() {
        return screenType;
    }

    public String getResolution() {
        return resolution;
    }

    public String getPhoneWidth() {
        return phoneWidth;
    }
}

