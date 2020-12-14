package ru.mail.z_team.local_storage;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Walk {

    @NonNull
    @PrimaryKey
    public String title;

    public String author;

    public String date;

    public Walk(@NonNull String title, String author, String date) {
        this.title = title;
        this.author = author;
        this.date = date;
    }
}
