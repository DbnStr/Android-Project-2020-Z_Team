package ru.mail.z_team.databases.local_storage.walk_annotation;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WalkAnnotation {

    @NonNull
    @PrimaryKey
    public String title;

    public String authorName;

    public String authorId;

    public String date;

    public WalkAnnotation(@NonNull String title, String authorName, String authorId, String date) {
        this.title = title;
        this.authorName = authorName;
        this.authorId = authorId;
        this.date = date;
    }
}
