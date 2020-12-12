package ru.mail.z_team.icon_fragments.go_out;

import android.net.Uri;

public class Story {
    private String description;
    private String place;
    private String rui;
    private Uri uri = null;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setRui(String rui) {
        this.rui = rui;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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

    public Uri getUri() {
        return uri;
    }
}
