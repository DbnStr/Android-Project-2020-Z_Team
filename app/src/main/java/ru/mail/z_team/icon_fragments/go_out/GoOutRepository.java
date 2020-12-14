package ru.mail.z_team.icon_fragments.go_out;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.map.Story;
import ru.mail.z_team.network.DatabaseApiRepository;
import ru.mail.z_team.network.UserApi;

public class GoOutRepository {

    private static final String LOG_TAG = "GoOutRepository";
    private final Logger logger;

    private final UserApi userApi;
    private final Context context;

    private final MutableLiveData<GoOutRepository.PostStatus> postStatus = new MutableLiveData<>();
    private final MutableLiveData<String> placeName = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    private String currentUserName;

    public GoOutRepository(Context context) {
        userApi = DatabaseApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
        this.context = context;
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
        ArrayList<UserApi.Story> userApiStories = transformToUserApiStoryAll(stories, id, sdf.format(currentTime));
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

    private ArrayList<UserApi.Story> transformToUserApiStoryAll(ArrayList<Story> stories,
                                                                String id,
                                                                String date) {
        ArrayList<UserApi.Story> res = new ArrayList<>();
        for (Story story : stories) {
            res.add(transformToUserApiStory(story, id, date, stories.indexOf(story)));
        }
        return res;
    }

    private UserApi.Story transformToUserApiStory(Story story,
                                                  String id,
                                                  String date,
                                                  int i) {
        UserApi.Story apiStory = new UserApi.Story(story.getDescription());
        apiStory.place = story.getPlace();
        apiStory.images = new ArrayList<>();
        apiStory.point = story.getPoint().toJson();
        for (int j = 0; j < story.getUriImages().size(); j++) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("WalkMaps/" + id + "/" + date + "/stories/" + i + "/images/"
                            + story.getUriImages().get(j).getLastPathSegment());
            storageReference.putFile(story.getUriImages().get(j)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    logger.log("successfully added the image");
                } else {
                    logger.errorLog("error in adding the image");
                }
            });
            apiStory.images.add(storageReference.getPath());
        }
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

    public void updatePlaceName(Point point) {
        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken(context.getString(R.string.mapbox_access_token))
                .query(point)
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build();


        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                if (response.body() != null){
                    placeName.postValue(response.body().features().get(0).placeName());
                }
                else {
                    logger.errorLog("geocode response is empty");
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                logger.errorLog(t.getMessage());
            }
        });
    }

    public LiveData<String> getPlaceName() {
        return placeName;
    }

    public enum PostStatus {
        OK,
        FAILED
    }
}
