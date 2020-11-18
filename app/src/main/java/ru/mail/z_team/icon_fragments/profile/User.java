package ru.mail.z_team.icon_fragments.profile;

public class User {

    private String mName;
    private int mAge;

    User(String mName, int mAge) {
        this.mName = mName;
        this.mAge = mAge;
    }

    public String getName() {
        return mName;
    }

    public int getAge() {
        return mAge;
    }
}
