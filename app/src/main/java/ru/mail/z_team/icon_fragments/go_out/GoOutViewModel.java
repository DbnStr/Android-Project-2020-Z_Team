package ru.mail.z_team.icon_fragments.go_out;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import java.util.ArrayList;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.map.Story;

public class GoOutViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "WalkViewModel";
    private final Logger logger;
    private final GoOutRepository repository;
    MediatorLiveData<String> postWalkStatus = new MediatorLiveData<>();
    MediatorLiveData<String> placeName = new MediatorLiveData<>();

    public GoOutViewModel(@NonNull Application application) {
        super(application);
        logger = new Logger(LOG_TAG, true);
        repository = new GoOutRepository(getApplication());
    }

    public void postWalk(String title, FeatureCollection walk, ArrayList<Story> stories) {
        logger.log("postWalk");
        repository.postWalk(title, walk, stories);
        postWalkStatus.addSource(repository.getPostStatus(), postStatus -> {
            if (postStatus == GoOutRepository.PostStatus.FAILED){
                postWalkStatus.postValue(getApplication().getString(R.string.FAILED));
            }
            else if (postStatus == GoOutRepository.PostStatus.OK){
                postWalkStatus.postValue(getApplication().getString(R.string.SUCCESS));
            }
        });
    }

    public LiveData<String> getPostStatus(){
        return postWalkStatus;
    }

    public void updatePlaceName(Point point) {
        repository.updatePlaceName(point);
        placeName.addSource(repository.getPlaceName(), place -> {
            placeName.postValue(place);
        });
    }

    public LiveData<String> getPlaceName() {
        return placeName;
    }
}
