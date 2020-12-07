package ru.mail.z_team.local_storage;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @NonNull
    @PrimaryKey
    public String id;

    public String name;

    public int age;
}
