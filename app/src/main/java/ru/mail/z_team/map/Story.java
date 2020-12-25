package ru.mail.z_team.map;

import android.net.Uri;

import com.mapbox.geojson.Feature;

import java.util.ArrayList;
import java.util.List;

public class Story {
    private String description;
    private String place;
    private Feature point;
    private String id;
    private List<String> urlImages = new ArrayList<>();
    private final ArrayList<Uri> uriImages = new ArrayList<>();

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setPoint(Feature point) {
        this.point = point;
    }

    public void setUriImages(ArrayList<Uri> uris) {
        uriImages.clear();
        this.uriImages.addAll(uris);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrlImages(List<String> urlImages) {
        this.urlImages = urlImages;
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

    public String getId() {
        return id;
    }

    public Feature getPoint() {
        return point;
    }

    public ArrayList<Uri> getUriImages() {
        return uriImages;
    }

    public List<String> getUrlImages() {
        return urlImages;
    }
}
