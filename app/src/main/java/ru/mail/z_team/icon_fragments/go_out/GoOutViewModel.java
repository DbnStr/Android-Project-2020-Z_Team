package ru.mail.z_team.icon_fragments.go_out;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.mapbox.geojson.FeatureCollection;

import java.util.ArrayList;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

public class GoOutViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "WalkViewModel";
    private final Logger logger;
    private final GoOutRepository repository;
    MediatorLiveData<String> postWalkStatus = new MediatorLiveData<>();

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
}
