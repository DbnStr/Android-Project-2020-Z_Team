package ru.mail.z_team;

import android.app.Application;
import android.content.Context;

import ru.mail.z_team.network.ApiRepository;

public class ApplicationModified extends Application {

    private ApiRepository apiRepository;
    private AuthRepository authRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        apiRepository = new ApiRepository();
        authRepository = new AuthRepository(apiRepository);
    }

    public ApiRepository getApis() {
        return apiRepository;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}
