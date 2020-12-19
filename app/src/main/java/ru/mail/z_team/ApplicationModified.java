package ru.mail.z_team;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import ru.mail.z_team.map.MapRepository;
import androidx.room.Room;

import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.network.DatabaseApiRepository;

public class ApplicationModified extends Application {

    public static final String CHANNEL_GO_OUT = "channel-go-out";

    private NotificationManager notificationManager;

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

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        initNotificationChannels();
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

    public void initNotificationChannels(){
        if (Build.VERSION.SDK_INT < 26)
            return;

        NotificationChannel defaultChannel = new NotificationChannel(
                CHANNEL_GO_OUT,
                getString(R.string.channel_go_out_name), NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(defaultChannel);
    }
}
