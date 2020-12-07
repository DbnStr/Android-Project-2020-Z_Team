package ru.mail.z_team;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.network.ApiRepository;

public class ApplicationModified extends Application {

    private ApiRepository apiRepository;
    private AuthRepository authRepository;
    private LocalDatabase localDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        apiRepository = new ApiRepository();
        authRepository = new AuthRepository(apiRepository);
        localDatabase = Room.databaseBuilder(this, LocalDatabase.class, "LocalDatabase")
                .build();
    }

    public ApiRepository getApis() {
        return apiRepository;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public LocalDatabase getLocalDatabase() {
        return localDatabase;
    }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}
