package ru.mail.z_team.databases.local_storage;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LocalDbFriend {

    @ColumnInfo(name = "friend_name")
    public String name;

    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "current_user_id")
    public String currentUserId;

    public LocalDbFriend(String name, @NonNull String id, String currentUserId) {
        this.name = name;
        this.id = id;
        this.currentUserId = currentUserId;
    }

    public String getName() {
        return name;
    }

    @NonNull
    public String getId() {
        return id;
    }
}
