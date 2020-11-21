package ru.mail.z_team.icon_fragments.friends;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class FriendsRepository {
    private static final String LOG_TAG = "FriendsRepository";
    private static final int FAILED_TO_READ_WRITE_DB_CODE = 401;

    private final Context context;
    private final MutableLiveData<List<String>> userFriends = new MutableLiveData<>();

    private final UserApi userApi;

    public FriendsRepository(Context context) {
        this.context = context;
        userApi = ApiRepository.from(context).getUserApi();
    }

    public void update(final String id) {
        userApi.getUserFriendsById(id).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.code() == FAILED_TO_READ_WRITE_DB_CODE) {
                    errorLog("Problem with Auth", null);
                    return;
                }
                if (response.body() == null) {
                    errorLog("File not found", null);
                    userFriends.postValue(new ArrayList<>());
                    return;
                }
                if (response.isSuccessful()) {
                    userFriends.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                errorLog("Failed to load", t);
            }
        });
    }

    private void errorLog(final String message, Throwable t) {
        Log.e(LOG_TAG, message, t);
    }


    public LiveData<List<String>> getUserFriendsById(String id) {
        return userFriends;
    }

    public void addFriend(String id, int num) {
        Log.d(LOG_TAG, "addFriend");
        userApi.addFriend(FirebaseAuth.getInstance().getUid(), Integer.toString(num), id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                update(FirebaseAuth.getInstance().getUid());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                errorLog("Failed to add friend", t);
            }
        });
    }
}
