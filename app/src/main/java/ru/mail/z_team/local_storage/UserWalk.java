package ru.mail.z_team.local_storage;

import androidx.room.Ignore;
import androidx.room.Insert;

import java.util.List;

public class UserWalk {
    public String title;

    public String authorName;

    public String authorId;

    public String date;

    public String walk;

    @Ignore
    public List<UserStory> stories;
}
