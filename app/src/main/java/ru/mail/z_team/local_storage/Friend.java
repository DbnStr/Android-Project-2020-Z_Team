package ru.mail.z_team.local_storage;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Friend {

    @ColumnInfo(name = "friend_name")
    public String name;

    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "current_user_id")
    public String currentUserId;

    public Friend(String name, String id, String currentUserId) {
        this.name = name;
        this.id = id;
        this.currentUserId = currentUserId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
