package ru.mail.z_team.icon_fragments.go_out;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.geojson.FeatureCollection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.map.Story;
import ru.mail.z_team.network.DatabaseApiRepository;
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
        userApi = DatabaseApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
    }

    public void postWalk(String title, FeatureCollection walk, ArrayList<Story> stories) {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        updateCurrentUserName();
        userApi.getUserWalksById(currentUserId).enqueue(new DatabaseCallback<ArrayList<UserApi.WalkInfo>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<UserApi.WalkInfo>> response) {
                addWalkInDb(0, title, currentUserId, currentUserName, walk, stories);
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<UserApi.WalkInfo>> response) {
                int count = response.body().size();
                addWalkInDb(count, title, currentUserId, currentUserName, walk, stories);
            }
        });
    }

    private void addWalkInDb(int currentWalkNumber,
                             String title,
                             String id,
                             String name,
                             FeatureCollection walk,
                             ArrayList<Story> stories) {
        logger.log("Post a walk");
        Date currentTime = new Date();
        String map = walk.toJson();
        ArrayList<UserApi.Story> userApiStories = transformToUserApiStoryAll(stories);
        userApi.addWalk(id, sdf.format(currentTime), new UserApi.Walk(title, sdf.format(currentTime), name, map, userApiStories)).enqueue(new Callback<UserApi.Walk>() {
            @Override
            public void onResponse(Call<UserApi.Walk> call, Response<UserApi.Walk> response) {
                if (response.isSuccessful()) {
                    postStatus.postValue(PostStatus.OK);
                } else {
                    postStatus.postValue(PostStatus.FAILED);
                }
            }

            @Override
            public void onFailure(Call<UserApi.Walk> call, Throwable t) {
                logger.errorLog(t.getMessage());
                postStatus.postValue(PostStatus.FAILED);
            }
        });
        userApi.addWalkInfo(id, currentWalkNumber, new UserApi.WalkInfo(title, sdf.format(currentTime), name, id)).enqueue(new Callback<UserApi.WalkInfo>() {
            @Override
            public void onResponse(Call<UserApi.WalkInfo> call, Response<UserApi.WalkInfo> response) {
                if (response.isSuccessful()) {
                    postStatus.postValue(PostStatus.OK);
                } else {
                    postStatus.postValue(PostStatus.FAILED);
                }
            }

            @Override
            public void onFailure(Call<UserApi.WalkInfo> call, Throwable t) {
                logger.errorLog(t.getMessage());
                postStatus.postValue(PostStatus.FAILED);
            }
        });
    }

    private ArrayList<UserApi.Story> transformToUserApiStoryAll(ArrayList<Story> stories) {
        ArrayList<UserApi.Story> res = new ArrayList<>();
        for (Story story : stories){
            res.add(transformToUserApiStory(story));
        }
        return res;
    }

    private UserApi.Story transformToUserApiStory(Story story) {
        UserApi.Story apiStory = new UserApi.Story(story.getDescription());
        return apiStory;
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
