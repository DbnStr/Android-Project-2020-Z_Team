package ru.mail.z_team.map;

import android.net.Uri;

import java.util.ArrayList;

public class Story {
    private String description;
    private String place;
    private String rui;
    private final ArrayList<Uri> uriImages = new ArrayList<>();

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setRui(String rui) {
        this.rui = rui;
    }

    public void addImage(Uri uri) {
        uriImages.add(uri);
    }

    public String getDescription() {
        return description;
    }

    public String getPlace() {
        return place;
    }

    public String getRui() {
        return rui;
    }

    public ArrayList<Uri> getUriImages() {
        return uriImages;
    }
}
