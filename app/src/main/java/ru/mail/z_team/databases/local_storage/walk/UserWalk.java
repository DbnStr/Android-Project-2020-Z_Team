package ru.mail.z_team.databases.local_storage.walk;

import androidx.room.Ignore;

import java.util.List;

import ru.mail.z_team.databases.local_storage.story.UserStory;

public class UserWalk {
    public String title;

    public String authorName;

    public String authorId;

    public String date;

    public String walk;

    @Ignore
    public List<UserStory> stories;
}
