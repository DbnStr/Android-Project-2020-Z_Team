package ru.mail.z_team.icon_fragments.walks;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.map.Story;
import ru.mail.z_team.network.DatabaseApiRepository;
import ru.mail.z_team.network.UserApi;

public class WalkProfileRepository {
    private static final String LOG_TAG = "WalkProfileRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final Context context;

    private final MutableLiveData<Walk> currentDisplayedWalk = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCloseEnough = new MutableLiveData<>();

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
                currentDisplayedWalk.postValue(transformToWalk(response.body()));
            }
        });
    }

    private Walk transformToWalk(UserApi.Walk walk) {
        Walk transformed = new Walk();
        transformed.setTitle(walk.title);
        transformed.setAuthor(walk.author);
        FeatureCollection map = FeatureCollection.fromJson(walk.walk);
        transformed.setMap(map);
        transformed.setStories(transformToStoryAll(walk.stories));
        try {
            transformed.setDate(sdf.parse(walk.date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transformed;
    }

    private ArrayList<Story> transformToStoryAll(ArrayList<UserApi.Story> stories) {
        ArrayList<Story> transformed = new ArrayList<>();
        if (stories != null) {
            for (UserApi.Story apiStory : stories) {
                transformed.add(transformToStory(apiStory));
            }
        }
        return transformed;
    }

    private Story transformToStory(UserApi.Story apiStory) {
        Story story = new Story();
        story.setDescription(apiStory.description);
        story.setPlace(apiStory.place);
        story.setUrlImages(apiStory.images);
        story.setPoint(Feature.fromJson(apiStory.point));
        return story;
    }

    public void closeEnough(Point a, Point b) {
        MapboxDirections client = MapboxDirections.builder()
                .origin(a)
                .destination(b)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(context.getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    logger.log("No routes found, make sure you set the right user and access token.");
                } else if (response.body().routes().size() == 0) {
                    logger.log("No routes found");
                } else {
                    List<DirectionsRoute> routes = response.body().routes();
                    logger.log("routes size = " + routes.size());
                    if (routes.size() > 0) {
                        logger.log("time = " + TimeUnit.SECONDS.toMinutes(routes.get(0).duration().longValue()));
                    }
                    isCloseEnough.postValue(routes.size() != 0 && TimeUnit.SECONDS.toMinutes(routes.get(0).duration().longValue()) <= 1);
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                logger.errorLog(t.getMessage());
            }
        });
    }

    public LiveData<Boolean> getAnswer() {
        return isCloseEnough;
    }
}
