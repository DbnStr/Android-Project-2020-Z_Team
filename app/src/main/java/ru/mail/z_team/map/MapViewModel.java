package ru.mail.z_team.map;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

import java.util.ArrayList;

import ru.mail.z_team.ApplicationModified;

public class MapViewModel extends AndroidViewModel {

    MapRepository repository;
    MediatorLiveData<String> placeName = new MediatorLiveData<>();

    public MapViewModel(@NonNull Application application) {
        super(application);
        repository = ((ApplicationModified) application).getMapRepository();
    }

    //--------------------- MapFragment

    public LiveData<Boolean> getIsClickable() {
        return repository.getIsClickable();
    }

    public LiveData<Point> getStartPos() {
        return repository.getStartPos();
    }

    public LiveData<Point> getDestinationPos() {
        return repository.getDestinationPos();
    }

    public LiveData<ArrayList<Feature>> getWalkList() {
        return repository.getWalkList();
    }

    public LiveData<ArrayList<Story>> getStories() {
        return repository.getStories();
    }

    public void setIsClickable(boolean b) {
        repository.setIsClickable(b);
    }

    public void setStartPos(Point p) {
        repository.setStartPos(p);
    }

    public void setDestinationPos(Point p) {
        repository.setDestinationPos(p);
    }

    public void setWalkList(ArrayList<Feature> walkList) {
        repository.setWalkList(walkList);
    }

    public void setStories(ArrayList<Story> stories) {
        repository.setStories(stories);
    }

    //--------------------- SaveStoryFragment

    public void updatePlaceName(Point point) {
        repository.updatePlaceName(point);
        placeName.addSource(repository.getPlaceName(), place -> {
            placeName.postValue(place);
        });
    }

    public LiveData<String> getPlaceName() {
        return placeName;
    }

    public void setStoryPoint(Point p) {
        repository.setPoint(p);
    }

    public LiveData<Point> getStoryPoint() {
        return repository.getStoryPoint();
    }

    public void setDescription(String text) {
        repository.setDescription(text);
    }

    public LiveData<String> getDescription() {
        return repository.getDescription();
    }


    public void setImageUris(ArrayList<Uri> uris) {
        repository.setImageUris(uris);
    }

    public LiveData<ArrayList<Uri>> getImageUris() {
        return repository.getImageUris();
    }

    public void setImageCount(int photoCount) {
        repository.setPhotoCount(photoCount);
    }

    public LiveData<Integer> getImageCount() {
        return repository.getPhotoCount();
    }
}
