package ru.mail.z_team.user;

import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.HashMap;

public class User {

    public String name;
    public int age;
    public String id;

    public User(String name, int age, String id) {
        this.name = name;
        this.age = age;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
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
}
