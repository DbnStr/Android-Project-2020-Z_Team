package ru.mail.z_team.local_storage;

import androidx.room.PrimaryKey;

public class Image {

    @PrimaryKey
    public String storyId;

    public String image;
}
