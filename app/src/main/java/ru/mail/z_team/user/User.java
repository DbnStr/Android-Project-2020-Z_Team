package ru.mail.z_team.user;

import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class User {

    public String name;
    public int age;
    public String id;
    public ArrayList<Friend> friends;

    public User(String name, int age, String id, ArrayList<Friend> friends) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.friends = friends;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public User updateUserInfo(HashMap<String, String> newInfo) {
        Field[] fields = User.class.getFields();
        for(Field field : fields) {
            if (newInfo.containsKey(field.getName())) {
                setFieldValue(field, newInfo.get(field.getName()));
            }
        }
        return this;
    }

    private void setFieldValue(@NonNull final Field field, @NonNull String value) {
        try {
            if (field.getType().equals(int.class)) {
                field.set(this, Integer.valueOf(value));
            } else {
                field.set(this, value);
            }
        } catch (IllegalAccessException exception) {
            Log.e("User", exception.getMessage());
        }
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public boolean isThisFriendAdded(String id) {
        for(int i = 0; i < friends.size(); i++) {
            if (friends.get(i).id.equals(id)) {
                return true;
            }
        }
        return false;
    }
}
