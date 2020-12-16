package ru.mail.z_team.icon_fragments.walks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public class WalksViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "ProfileViewModel";
    private final WalksRepository repository;

    private final LiveData<ArrayList<WalkAnnotation>> currentUserWalks;

    public WalksViewModel(@NonNull Application application) {
        super(application);
        repository = new WalksRepository(getApplication());
        currentUserWalks= repository.getCurrentUserWalks();
    }

    public void updateCurrentUserWalks() {
        repository.updateCurrentUserWalks();
    }

    public LiveData<ArrayList<WalkAnnotation>> getCurrentUserWalks() {
        return currentUserWalks;
    }
}
