package ru.mail.z_team.local_storage;

import androidx.room.ColumnInfo;

public class UserFriend {

    public String id;

    @ColumnInfo(name  = "fr_name")
    public String name;
}
