package ru.mail.z_team.databases;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DatabaseFriend {

    @ColumnInfo(name = "friend_name")
    public String name;

    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "current_user_id")
    public transient String currentUserId;

    public DatabaseFriend(String name, @NonNull String id, String currentUserId) {
        this.name = name;
        this.id = id;
        this.currentUserId = currentUserId;
    }

    public DatabaseFriend() {

    }

    public String getName() {
        return name;
    }

    @NonNull
    public String getId() {
        return id;
    }
}
