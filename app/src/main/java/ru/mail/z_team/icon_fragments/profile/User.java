package ru.mail.z_team.icon_fragments.profile;

import ru.mail.z_team.network.UserApi;

public class User {

    public String name;
    public int age;
    public String id;

    User(String name, int age, String id) {
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

    public UserApi.User getUserApiUser() {
        UserApi.User result = new UserApi.User();
        result.name = this.name;
        result.age = this.age;
        result.id = this.id;
        return result;
    }
}
