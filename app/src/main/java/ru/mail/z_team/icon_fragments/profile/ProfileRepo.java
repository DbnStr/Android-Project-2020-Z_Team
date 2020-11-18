package ru.mail.z_team.icon_fragments.profile;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.icon_fragments.profile.network.ApiRepo;
import ru.mail.z_team.icon_fragments.profile.network.UserApi;

public class ProfileRepo {

    private static final long ONE_MEGABYTE = 1024 * 1024;
    private static final String LOG_TAG = "ProfileRepo";
    private final Context mContext;
    private static final MutableLiveData<String> data = new MutableLiveData<>();

    private final UserApi mUserApi;

    public ProfileRepo(Context context) {
        mContext = context;
        mUserApi = ApiRepo.from(mContext).getUserApi();
    }

    public LiveData<String> getUserInfoById(final String id) {
        return data;
    }

    public void update(final String id) {
        mUserApi.getUserById(1).enqueue(new Callback<UserApi.User>() {
            @Override
            public void onResponse(Call<UserApi.User> call,
                                   Response<UserApi.User> response) {
                if (response.code() == 401) {
                    errorLog("Problem with Auth", null);
                }

                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body().name);
                }
            }

            @Override
            public void onFailure(Call<UserApi.User> call, Throwable t) {
                errorLog("Failed to load", t);
            }
        });
    }

    private void errorLog(final String message, Throwable t) {
        Log.e(LOG_TAG, message, t);
    }

    private void log(final String message) {
        Log.d(LOG_TAG, message);
    }
}
