package ru.mail.z_team.databases;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DatabaseWalkAnnotation {

    @NonNull
    @PrimaryKey
    public String title;

    public String authorName;

    public String authorId;

    public String date;

    public DatabaseWalkAnnotation(@NonNull String title, String date, String authorName, String authorId) {
        this.title = title;
        this.date = date;
        this.authorName = authorName;
        this.authorId = authorId;
    }
}
