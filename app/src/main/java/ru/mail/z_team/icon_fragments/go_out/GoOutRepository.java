package ru.mail.z_team.icon_fragments.go_out;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.geojson.Feature;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class GoOutRepository {

    private static final String LOG_TAG = "GoOutRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final MutableLiveData<GoOutRepository.PostStatus> postStatus = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    private String currentUserName;

    public GoOutRepository(Context context) {
        userApi = ApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
    }

    public void postWalk(String title, Feature walk) {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        updateCurrentUserName();
        userApi.getUserWalksById(currentUserId).enqueue(new DatabaseCallback<ArrayList<UserApi.Walk>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<UserApi.Walk>> response) {
                addWalkInDb(0, title, currentUserId, currentUserName, walk);
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<UserApi.Walk>> response) {
                int count = response.body().size();
                addWalkInDb(count, title, currentUserId, currentUserName, walk);
            }
        });
    }

    private void addWalkInDb(int currentWalkNumber,
                             String title,
                             String id,
                             String name,
                             Feature walk) {
        logger.log("Post a walk");
        Date currentTime = new Date();
        userApi.addWalk(id, currentWalkNumber, new UserApi.Walk(title, sdf.format(currentTime), name, walk)).enqueue(new Callback<UserApi.User>() {
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
                logger.errorLog(t.getMessage());
                postStatus.postValue(PostStatus.FAILED);
            }
        });
    }

    private void updateCurrentUserName() {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        logger.log("update user - " + currentUserId);

        userApi.getUserById(currentUserId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("Fail with update");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                currentUserName = response.body().name;
            }
        });
    }

    public MutableLiveData<PostStatus> getPostStatus() {
        return postStatus;
    }

    public enum PostStatus {
        OK,
        FAILED
    }
}
