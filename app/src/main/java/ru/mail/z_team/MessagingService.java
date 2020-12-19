package ru.mail.z_team;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ru.mail.z_team.map.MapActivity;

public class MessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_ID = 100;

    private static final String LOG_TAG = "MessagingService";
    private final Logger logger = new Logger(LOG_TAG, true);

    public MessagingService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        logger.log("onMessageReceived");

        if (!remoteMessage.getData().isEmpty()
                && remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            showNotification(title, body);
        }

    }

    private void showNotification(String title, String body) {
        logger.log("showNotification");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null)
            return;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ApplicationModified.CHANNEL_GO_OUT);

        Intent resultIntent = new Intent(this, MapActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);


        manager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        logger.log("onNewToken: " + s);
        super.onNewToken(s);
    }
}