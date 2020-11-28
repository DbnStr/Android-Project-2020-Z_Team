package ru.mail.z_team.icon_fragments.go_out;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import ru.mail.z_team.R;

public class WalkViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "WalkViewModel";
    WalkRepository repository = new WalkRepository(getApplication());
    MediatorLiveData<String> postWalkStatus = new MediatorLiveData<>();

    public WalkViewModel(@NonNull Application application) {
        super(application);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postWalk(String title) {
        Log.d(LOG_TAG, "postWalk");
        repository.postWalk(title);
        postWalkStatus.addSource(repository.postStatus, postStatus -> {
            if (postStatus == WalkRepository.PostStatus.FAILED){
                postWalkStatus.postValue(getApplication().getString(R.string.FAILED));
            }
            else if (postStatus == WalkRepository.PostStatus.OK){
                postWalkStatus.postValue(getApplication().getString(R.string.SUCCESS));
            }
        });
    }

    public LiveData<String> getPostStatus(){
        return postWalkStatus;
    }
}
