package ru.mail.z_team.local_storage.walk;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Walk {

    @NonNull
    @PrimaryKey
    public String title;

    public String authorName;

    public String authorId;

    public String date;

    public String walk;

    public Walk(@NonNull String title, String authorName, String authorId, String date, String walk) {
        this.title = title;
        this.authorName = authorName;
        this.authorId = authorId;
        this.date = date;
        this.walk = walk;
    }
}
