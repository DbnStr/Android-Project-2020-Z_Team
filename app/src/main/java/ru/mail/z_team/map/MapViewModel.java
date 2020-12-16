package ru.mail.z_team.map;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

import java.util.ArrayList;

import ru.mail.z_team.ApplicationModified;

public class MapViewModel extends AndroidViewModel {

    MapRepository repository;

    public MapViewModel(@NonNull Application application) {
        super(application);
        repository = ((ApplicationModified) application).getMapRepository();
    }

    public LiveData<Boolean> getIsInitialized(){
        return repository.getIsInitialized();
    }

    public LiveData<Boolean> getIsClickable(){
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

    public void setIsInitialized(boolean b){
        repository.setIsInitialized(b);
    }

    public void setIsClickable(boolean b){
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
}
