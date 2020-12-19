package ru.mail.z_team;

import android.app.Application;
import android.content.Context;

import ru.mail.z_team.map.MapRepository;
import androidx.room.Room;

import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.network.DatabaseApiRepository;

public class ApplicationModified extends Application {

    private DatabaseApiRepository databaseApiRepository;
    private AuthRepository authRepository;
    private MapRepository mapRepository;
    private LocalDatabase localDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseApiRepository = new DatabaseApiRepository();
        authRepository = new AuthRepository(databaseApiRepository);
        mapRepository = new MapRepository(getApplicationContext());
        localDatabase = Room.databaseBuilder(this, LocalDatabase.class, "LocalDatabase")
                .build();
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

    public LocalDatabase getLocalDatabase() {
        return localDatabase;
    }

    public static ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }
}
