package ru.mail.z_team.icon_fragments.walks;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.icon_fragments.Transformer;
import ru.mail.z_team.map.Story;
import ru.mail.z_team.network.DatabaseApiRepository;
import ru.mail.z_team.network.UserApi;

public class WalkProfileRepository {
    private static final String LOG_TAG = "WalkProfileRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final Context context;

    private final MutableLiveData<Walk> currentDisplayedWalk = new MutableLiveData<>();
    private final MutableLiveData<WalkAnnotation> annotation = new MutableLiveData<>();
    private final MutableLiveData<Story> currentDisplayedStory = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    public WalkProfileRepository(Context context) {
        this.context = context;
        userApi = DatabaseApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
    }

    public LiveData<Walk> getCurrentDisplayedWalk() {
        return currentDisplayedWalk;
    }

    public void updateCurrentDisplayedWalk(WalkAnnotation walkAnnotation) {
        String userId = walkAnnotation.getAuthorId();
        String date = sdf.format(walkAnnotation.getDate());
        userApi.getWalkByDateAndId(userId, date).enqueue(new DatabaseCallback<UserApi.Walk>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.Walk> response) {
                logger.errorLog("This walk doesn't exist");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.Walk> response) {
                currentDisplayedWalk.postValue(Transformer.transformToWalk(response.body()));
            }
        });
    }

    public void setAnnotation(WalkAnnotation walkAnnotation) {
        annotation.postValue(walkAnnotation);
    }

    public LiveData<WalkAnnotation> getAnnotation() {
        return annotation;
    }

    public void setStory(Story story) {
        currentDisplayedStory.postValue(story);
    }

    public LiveData<Story> getStory() {
        return currentDisplayedStory;
    }
}
