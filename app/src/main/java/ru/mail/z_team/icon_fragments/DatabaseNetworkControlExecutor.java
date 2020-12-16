package ru.mail.z_team.icon_fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public abstract class DatabaseNetworkControlExecutor {

    private final Context context;

    public DatabaseNetworkControlExecutor(final Context context) {
        this.context = context;
    }

    public void run() {
        if (isOnline()) {
            networkRun();
        } else {
            noNetworkRun();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public abstract void networkRun();

    public abstract void noNetworkRun();
}
