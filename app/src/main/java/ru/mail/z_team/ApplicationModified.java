package ru.mail.z_team;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import ru.mail.z_team.map.MapRepository;
import ru.mail.z_team.network.DatabaseApiRepository;

public class ApplicationModified extends Application {

    public static final String CHANNEL_GO_OUT = "channel-go-out";

    private NotificationManager notificationManager;

    private DatabaseApiRepository databaseApiRepository;
    private AuthRepository authRepository;
    private MapRepository mapRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseApiRepository = new DatabaseApiRepository();
        authRepository = new AuthRepository(databaseApiRepository);
        mapRepository = new MapRepository(getApplicationContext());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        initNotificationChannels();
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

    public void initNotificationChannels(){
        if (Build.VERSION.SDK_INT < 26)
            return;

        NotificationChannel defaultChannel = new NotificationChannel(
                CHANNEL_GO_OUT,
                getString(R.string.channel_go_out_name), NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(defaultChannel);
    }
}
