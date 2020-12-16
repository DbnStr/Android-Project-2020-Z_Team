package ru.mail.z_team.icon_fragments.walks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class WalkProfileViewModel extends AndroidViewModel{

    private static final String LOG_TAG = "WalkProfileViewModel";
    private final WalkProfileRepository repository;

    private final LiveData<Walk> currentDisplayedWalk;

    public WalkProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new WalkProfileRepository(getApplication());
        currentDisplayedWalk = repository.getCurrentDisplayedWalk();
    }

    public void updateCurrentDisplayedWalk(WalkAnnotation walkAnnotation) {
        repository.updateCurrentDisplayedWalk(walkAnnotation);
    }

    public LiveData<Walk> getCurrentDisplayedWalk() {
        return currentDisplayedWalk;
    }
}
