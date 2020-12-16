package ru.mail.z_team;

import android.app.Application;
import android.content.Context;

import ru.mail.z_team.network.DatabaseApiRepository;

public class ApplicationModified extends Application {

    private DatabaseApiRepository databaseApiRepository;
    private AuthRepository authRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseApiRepository = new DatabaseApiRepository();
        authRepository = new AuthRepository(databaseApiRepository);
    }

    public DatabaseApiRepository getApis() {
        return databaseApiRepository;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}
