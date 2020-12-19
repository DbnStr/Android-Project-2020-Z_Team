package ru.mail.z_team.local_storage.story;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Story {

    public String description;

    public String place;

    public String point;

    @NonNull
    @PrimaryKey
    public String id;

    public String walkDate;
}
