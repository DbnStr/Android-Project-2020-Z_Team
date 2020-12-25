package ru.mail.z_team.databases;

import androidx.room.ColumnInfo;

public class DatabaseFriend {

    public String id;

    @ColumnInfo(name  = "fr_name")
    public String name;
}
