package ru.mail.z_team;

import android.app.Application;
import android.content.Context;

import ru.mail.z_team.icon_fragments.profile.network.ApiRepository;

public class ApplicationModified extends Application {

    private ApiRepository apiRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        apiRepository = new ApiRepository();
    }

    public ApiRepository getApis() {
        return apiRepository;
    }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}
