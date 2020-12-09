package ru.mail.z_team.icon_fragments.walks;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.geojson.FeatureCollection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class WalksRepository {

    private static final String LOG_TAG = "WalksRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final MutableLiveData<ArrayList<Walk>> currentUserWalks = new MutableLiveData<>();
    private final MutableLiveData<Walk> currentDisplayedWalk = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    public WalksRepository(Context context) {
        userApi = ApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
    }

    public LiveData<ArrayList<Walk>> getCurrentUserWalks() {
        return currentUserWalks;
    }

    public LiveData<Walk> getCurrentDisplayedWalk() {
        return currentDisplayedWalk;
    }

    public void updateCurrentDisplayedWalk(String userId, String date){
        userApi.getWalkByDateAndId(userId, date).enqueue(new DatabaseCallback<UserApi.Walk>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.Walk> response) {
                logger.errorLog("This walk doesn't exist");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.Walk> response) {
                currentDisplayedWalk.postValue(transformToWalk(response.body()));
            }
        });
    }

    public void updateCurrentUserWalks() {
        String id = FirebaseAuth.getInstance().getUid();
        userApi.getUserWalksById(id).enqueue(new DatabaseCallback<ArrayList<UserApi.Walk>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<UserApi.Walk>> response) {
                logger.log("Walks was empty");
                currentUserWalks.postValue(new ArrayList<>());
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<UserApi.Walk>> response) {
                logger.log("Successful update current user walks");
                currentUserWalks.postValue(transformToWalkAll(response.body()));
            }
        });
    }

    private ArrayList<Walk> transformToWalkAll(ArrayList<UserApi.Walk> walks) {
        ArrayList<Walk> result = new ArrayList<>();
        for (UserApi.Walk walk : walks) {
            result.add(transformToWalk(walk));
        }
        return result;
    }

    private Walk transformToWalk(UserApi.Walk walk) {
        Walk transformed = new Walk();
        transformed.setTitle(walk.title);
        transformed.setAuthor(walk.author);
        FeatureCollection map = FeatureCollection.fromJson(walk.walk);
        transformed.setMap(map);
        try {
            transformed.setDate(sdf.parse(walk.date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transformed;
    }
}
