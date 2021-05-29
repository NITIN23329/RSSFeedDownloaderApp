package com.nitin.rssfeeddownloaderapp;

public class FeedEntry {

    private String name;
    private String artist;
    private String summery;
    private String appURL;
    private String releaseDate;

    public String getName() {
        return name;
    }
    public String getArtist() {
        return artist;
    }
    public String getSummery() {
        return summery;
    }
    public String getAppURL() {
        return appURL;
    }
    public String getReleaseDate() {
        return releaseDate;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setSummery(String summery) {
        this.summery = summery;
    }
    public void setAppURL(String appURL) {
        this.appURL = appURL;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }


    @Override
    public String toString() {
        return "FeedEntry{" + '\n'+
                "\t\tname='" + name + '\n' +
                "\t\tartist='" + artist + '\n' +
                "\t\tappURL='" + appURL + '\n' +
                "\t\treleaseDate='" + releaseDate + '\n' +
                '}';
    }
}
