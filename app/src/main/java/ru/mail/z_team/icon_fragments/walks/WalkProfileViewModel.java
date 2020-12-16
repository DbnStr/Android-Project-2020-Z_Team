package ru.mail.z_team.icon_fragments.walks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ru.mail.z_team.map.Story;

public class WalkProfileViewModel extends AndroidViewModel {

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

    public void setAnnotation(WalkAnnotation walkAnnotation) {
        repository.setAnnotation(walkAnnotation);
    }

    public LiveData<WalkAnnotation> getAnnotation() {
        return repository.getAnnotation();
    }

    public void setStory(Story story) {
        repository.setStory(story);
    }

    public LiveData<Story> getStory() {
        return repository.getStory();
    }
}
