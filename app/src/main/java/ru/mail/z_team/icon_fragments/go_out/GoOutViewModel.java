package ru.mail.z_team.icon_fragments.go_out;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import ru.mail.z_team.R;

public class GoOutViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "WalkViewModel";
    private final GoOutRepository repository;
    MediatorLiveData<String> postWalkStatus = new MediatorLiveData<>();

    public GoOutViewModel(@NonNull Application application) {
        super(application);
        repository = new GoOutRepository(getApplication());
    }

    public void postWalk(String title) {
        log("postWalk");
        repository.postWalk(title);
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

    private void log(final String message) {
        Log.d(LOG_TAG, message);
    }

    private void errorLog(final String message, Throwable t) {
        Log.e(LOG_TAG, message, t);
    }
}
