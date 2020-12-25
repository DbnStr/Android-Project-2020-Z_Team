package ru.mail.z_team.databases;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class DatabaseUser {

    @NonNull
    @PrimaryKey
    public String id;

    public String name;

    public int age;

    @Ignore
    public List<DatabaseFriend> friends = new ArrayList<>();

    @Ignore
    public String imageUrl;

    public String email;

    public DatabaseUser(@NonNull String id, String email, String name) {
        this.name = name;
        this.id = id;
        this.email = email;
    }

    public DatabaseUser() {
    }
}
