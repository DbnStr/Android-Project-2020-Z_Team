package ru.mail.z_team.databases;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class DatabaseStory {

    public String description;

    public String place;

    public String point;

    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "walkDate")
    public transient String walkDate;

    @Ignore
    public List<String> images;

    public DatabaseStory(String description) {
        this.description = description;
    }

    public DatabaseStory() {

    }
}
