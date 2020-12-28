package ru.mail.z_team.user;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class User {

    public String name;
    public int age;
    public String id;
    public String imageUrl;
    public ArrayList<Friend> friends;
    public Bitmap bitmap = null;

    public User(String name, int age, ArrayList<Friend> friends, String id) {
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

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public User updateUserInfo(HashMap<String, String> newInfo) {
        Field[] fields = User.class.getFields();
        for (Field field : fields) {
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

    public boolean isThisFriendAdded(String id) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).id.equals(id)) {
                return true;
            }
        }
        return false;
    }
}
