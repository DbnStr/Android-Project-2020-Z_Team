package ru.mail.z_team;

import android.app.Application;
import android.content.Context;

import ru.mail.z_team.map.MapRepository;
import ru.mail.z_team.network.DatabaseApiRepository;

public class ApplicationModified extends Application {

    private DatabaseApiRepository databaseApiRepository;
    private AuthRepository authRepository;
    private MapRepository mapRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseApiRepository = new DatabaseApiRepository();
        authRepository = new AuthRepository(databaseApiRepository);
        mapRepository = new MapRepository();
    }

    public DatabaseApiRepository getApis() {
        return databaseApiRepository;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public MapRepository getMapRepository() {
        return mapRepository;
    }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}
