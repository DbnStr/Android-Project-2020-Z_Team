package ru.mail.z_team.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

import java.util.ArrayList;

public class MapRepository {

    private final MutableLiveData<Point> startPos = new MutableLiveData<>();
    private final MutableLiveData<Point> destinationPos = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Feature>> walkList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Story>> stories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isClickable = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isInitialized = new MutableLiveData<>();


    public LiveData<Point> getStartPos() {
        return startPos;
    }

    public LiveData<Point> getDestinationPos() {
        return destinationPos;
    }

    public LiveData<ArrayList<Feature>> getWalkList() {
        return walkList;
    }

    public MutableLiveData<ArrayList<Story>> getStories() {
        return stories;
    }

    public MutableLiveData<Boolean> getIsClickable() {
        return isClickable;
    }

    public MutableLiveData<Boolean> getIsInitialized() {
        return isInitialized;
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

    public void setIsInitialized(boolean b) {
        isClickable.postValue(b);
    }
}
