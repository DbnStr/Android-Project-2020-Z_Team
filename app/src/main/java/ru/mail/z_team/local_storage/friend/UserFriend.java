package ru.mail.z_team.local_storage.friend;

import androidx.room.ColumnInfo;

public class UserFriend {

    public String id;

    @ColumnInfo(name  = "fr_name")
    public String name;
}
