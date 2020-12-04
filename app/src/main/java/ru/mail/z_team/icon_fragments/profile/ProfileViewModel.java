package ru.mail.z_team.icon_fragments.profile;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.HashMap;

import ru.mail.z_team.user.User;

public class ProfileViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "ProfileViewModel";
    private final ProfileRepository repository;

    private final LiveData<User> currentUserInfo;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new ProfileRepository(getApplication());
        currentUserInfo = repository.getCurrentUser();
    }

    public LiveData<User> getCurrentUser() {
        return currentUserInfo;
    }

    public void updateCurrentUser() {
        repository.updateCurrentUser();
    }

    public void changeCurrentUserInformation(HashMap<String, String> newInformation) {
        User user = getCurrentUser().getValue();
        if (user == null) {
            errorLog("LiveData is empty", null);
        } else {
            repository.changeCurrentUserInformation(user.updateUserInfo(newInformation));
        }
    }

    private void errorLog(final String message, Throwable t) {
        Log.e(LOG_TAG, message, t);
    }
}
