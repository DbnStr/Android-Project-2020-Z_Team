package ru.mail.z_team.map;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

public class MapRepository {

    private static final String LOG_TAG = "MapRepository";
    private final Logger logger;

    private final Context context;

    private final MutableLiveData<Point> startPos = new MutableLiveData<>();
    private final MutableLiveData<Point> destinationPos = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Feature>> walkList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Story>> stories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isClickable = new MutableLiveData<>();

    private final MutableLiveData<String> placeName = new MutableLiveData<>();
    private final MutableLiveData<String> description = new MutableLiveData<>();
    private final MutableLiveData<Point> storyPoint = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Uri>> uris = new MutableLiveData<>();
    private final MutableLiveData<Integer> photoCount = new MutableLiveData<>();

    public MapRepository(Context context){
        logger = new Logger(LOG_TAG, true);
        this.context = context;
    }

    //--------------------- MapFragment

    public LiveData<Point> getStartPos() {
        return startPos;
    }

    public LiveData<Point> getDestinationPos() {
        return destinationPos;
    }

    public LiveData<ArrayList<Feature>> getWalkList() {
        return walkList;
    }

    public LiveData<ArrayList<Story>> getStories() {
        return stories;
    }

    public LiveData<Boolean> getIsClickable() {
        return isClickable;
    }

    public void setStartPos(Point p) {
        startPos.postValue(p);
    }

    public void setDestinationPos(Point p) {
        destinationPos.postValue(p);
    }

    public void setWalkList(ArrayList<Feature> featureCollection) {
        walkList.postValue(featureCollection);
    }

    public void setStories(ArrayList<Story> arrayList) {
        stories.postValue(arrayList);
    }

    public void setIsClickable(boolean b) {
        isClickable.postValue(b);
    }

    //--------------------- SaveStoryFragment

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

    public void setDescription(String text) {
        description.postValue(text);
    }

    public void setPoint(Point p) {
        storyPoint.postValue(p);
    }

    public void setImageUris(ArrayList<Uri> uris) {
        this.uris.postValue(uris);
    }

    public void setPhotoCount(int count) {
        photoCount.postValue(count);
    }

    public LiveData<String> getPlaceName() {
        return placeName;
    }

    public LiveData<String> getDescription() {
        return description;
    }

    public LiveData<Point> getStoryPoint() {
        return storyPoint;
    }

    public LiveData<ArrayList<Uri>> getImageUris() {
        return uris;
    }

    public LiveData<Integer> getPhotoCount() {
        return photoCount;
    }
}
