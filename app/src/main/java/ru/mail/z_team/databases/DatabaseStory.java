package ru.mail.z_team.databases;

import androidx.room.Ignore;

import java.util.List;

public class DatabaseStory {

    public String description;

    public String place;

    public String point;

    public String id;

    @Ignore
    public List<String> images;

    public DatabaseStory(String description) {
        this.description = description;
    }
}
