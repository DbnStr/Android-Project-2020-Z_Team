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

    private static final String LOG_TAG = "ProfileRepo";
    private final Context mContext;
    private static final MutableLiveData<User> data = new MutableLiveData<>();

    private final UserApi mUserApi;

    public ProfileRepo(Context context) {
        mContext = context;
        mUserApi = ApiRepo.from(mContext).getUserApi();
    }

    public LiveData<User> getUserInfoById(final String id) {
        return data;
    }

    public void update(final String id) {
        mUserApi.getUserById(id).enqueue(new Callback<UserApi.User>() {
            @Override
            public void onResponse(Call<UserApi.User> call, Response<UserApi.User> response) {
                if (response.code() == 401) {
                    errorLog("Problem with Auth", null);
                    return;
                }
                if (response.body() == null) {
                    errorLog("File not found", null);
                    return;
                }
                if (response.isSuccessful()) {
                    data.postValue(transformToUser(response.body()));
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

    private User transformToUser(UserApi.User user) {
        return new User(
                user.name,
                user.age
        );
    }
}
