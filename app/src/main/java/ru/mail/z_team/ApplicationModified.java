package ru.mail.z_team;

import android.app.Application;
import android.content.Context;

public class ApplicationModified extends Application {

    private AuthRepo mAuthRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthRepo = new AuthRepo();
    }

    public AuthRepo getAuthRepo() {
        return mAuthRepo;
    }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}
