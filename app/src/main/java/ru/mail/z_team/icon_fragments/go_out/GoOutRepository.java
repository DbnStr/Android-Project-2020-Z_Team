package ru.mail.z_team.icon_fragments.go_out;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class GoOutRepository {

    private static final String LOG_TAG = "GoOutRepository";

    private final UserApi userApi;

    private final MutableLiveData<GoOutRepository.PostStatus> postStatus = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    private String currentUserName;

    public GoOutRepository(Context context) {
        userApi = ApiRepository.from(context).getUserApi();
    }

    public void postWalk(String title) {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        updateCurrentUserName();
        userApi.getUserWalksById(currentUserId).enqueue(new DatabaseCallback<List<UserApi.Walk>>(LOG_TAG) {
            @Override
            public void onNull(Response<List<UserApi.Walk>> response) {
                addWalkInDb(0, title, currentUserId, currentUserName);
            }

            @Override
            public void onSuccessResponse(Response<List<UserApi.Walk>> response) {
                int count = response.body().size();
                addWalkInDb(count, title, currentUserId, currentUserName);
            }
        });
    }

    private void addWalkInDb(int currentWalkNumber, String title, String id, String name) {
        Log.d(LOG_TAG, "postWalk");
        Date currentTime = new Date();
        Log.d(LOG_TAG, "postWalk named  - " + name);
        userApi.addWalk(id, currentWalkNumber, new UserApi.Walk(title, sdf.format(currentTime), name)).enqueue(new Callback<UserApi.User>() {
            @Override
            public void onResponse(Call<UserApi.User> call, Response<UserApi.User> response) {
                if (response.isSuccessful()) {
                    postStatus.postValue(PostStatus.OK);
                } else {
                    postStatus.postValue(PostStatus.FAILED);
                }
            }

            @Override
            public void onFailure(Call<UserApi.User> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage(), null);
                postStatus.postValue(PostStatus.FAILED);
            }
        });
    }

    private void updateCurrentUserName() {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        log("update user - " + currentUserId);

        userApi.getUserById(currentUserId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNull(Response<UserApi.User> response) {
                errorLog("Fail with update", null);
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                currentUserName = response.body().name;
            }
        });
    }

    public MutableLiveData<PostStatus> getPostStatus() {return postStatus;}

    public enum PostStatus {
        OK,
        FAILED
    }

    private void log(final String message) {
        Log.d(LOG_TAG, message);
    }

    private void errorLog(final String message, Throwable t) {
        Log.e(LOG_TAG, message, t);
    }
}
