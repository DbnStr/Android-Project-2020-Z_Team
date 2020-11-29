package ru.mail.z_team;

import android.app.Application;
import android.content.Context;

import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.user.UserRepository;

public class ApplicationModified extends Application {

    private ApiRepository apiRepository;
    private AuthRepo authRepo;
    private UserRepository userRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        apiRepository = new ApiRepository();
        authRepo = new AuthRepo(apiRepository);
        userRepository = new UserRepository(getApplicationContext());
    }

    public ApiRepository getApis() {
        return apiRepository;
    }

    public AuthRepo getAuthRepo() {
        return authRepo;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}
