package ru.mail.z_team;

import android.util.Log;

public class Logger {

    private final String tag;
    private final boolean isActive;

    public Logger(String tag, boolean isActive){
        this.tag = tag;
        this.isActive = isActive;
    }

    public void log(String message){
        if (isActive){
            Log.d(tag, message);
        }
    }

    public void errorLog(String message){
        if (isActive){
            Log.e(tag, message);
        }
    }
}
