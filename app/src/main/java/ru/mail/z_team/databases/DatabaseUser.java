package ru.mail.z_team.databases;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

import ru.mail.z_team.databases.network.UserApi;

@Entity
public class DatabaseUser {

    @NonNull
    @PrimaryKey
    public String id;

    public String name;

    public int age;

    @Ignore
    public ArrayList<UserApi.Friend> friends = new ArrayList<>();

    @Ignore
    public String imageUrl;

    @Ignore
    public String email;

    public DatabaseUser(String id, String email, String name) {
        this.name = name;
        this.id = id;
        this.email = email;
    }

    public DatabaseUser() {
    }
}
