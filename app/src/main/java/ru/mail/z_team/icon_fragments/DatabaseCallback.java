package ru.mail.z_team.icon_fragments;

import android.util.Log;

import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class DatabaseCallback<T> implements Callback<T> {

    private static final int FAILED_TO_READ_WRITE_DB_CODE = 401;
    private final String logTag;

    public abstract void onNullResponse(Response<T> response);

    public abstract void onSuccessResponse(Response<T> response) throws ExecutionException, InterruptedException;

    protected DatabaseCallback(final String logTag) {
        this.logTag = logTag;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.code() == FAILED_TO_READ_WRITE_DB_CODE) {
            Log.e(logTag, "Problem with Auth", null);
            return;
        }
        if (response.body() == null) {
            Log.e(logTag, "File not found", null);
            onNullResponse(response);
            return;
        }
        if (response.isSuccessful()) {
            try {
                onSuccessResponse(response);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e(logTag, "Failed to load", t);
    }
}
