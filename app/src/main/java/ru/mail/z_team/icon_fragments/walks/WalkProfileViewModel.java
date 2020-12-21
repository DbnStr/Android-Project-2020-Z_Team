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
    private final LiveData<Story> currentDisplayedStory;

    public WalkProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new WalkProfileRepository(getApplication());

        currentDisplayedWalk = repository.getCurrentDisplayedWalk();
        currentDisplayedStory = repository.getCurrentDisplayedStory();
    }

    public void updateCurrentDisplayedWalk(WalkAnnotation walkAnnotation) {
        repository.updateCurrentDisplayedWalk(walkAnnotation);
    }


    public void updateCurrentDisplayedStory(final int number, final String dateOfWalk) {
        repository.updateCurrentDisplayedStory(number, dateOfWalk);
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

    public LiveData<Story> getCurrentDisplayedStory() {
        return currentDisplayedStory;
    }
}
