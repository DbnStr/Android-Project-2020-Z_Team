package ru.mail.z_team.databases;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class DatabaseWalk {

    @NonNull
    @PrimaryKey
    public String title;

    public String authorName;

    public String authorId;

    public String date;

    public String walkInfo;

    @Ignore
    public List<DatabaseStory> stories;

    public DatabaseWalk(@NonNull String title, String date, String authorName, String authorId, String walkInfo, ArrayList<DatabaseStory> stories) {
        this.title = title;
        this.date = date;
        this.authorName = authorName;
        this.authorId = authorId;
        this.walkInfo = walkInfo;
        this.stories = stories;
    }

    public DatabaseWalk() {}
}
