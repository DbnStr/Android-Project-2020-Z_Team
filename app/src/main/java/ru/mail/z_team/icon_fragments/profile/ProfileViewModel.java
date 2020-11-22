package ru.mail.z_team.icon_fragments.profile;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.HashMap;

public class ProfileViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "ProfileViewModel";
    private final UserRepository repository = new UserRepository(getApplication());

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<User> getUserInfoById(final String id) {
        return repository.getUserInfoById(id);
    }

    public void update(final String id) {
        repository.update(id);
    }

    public void changeUserInformation(final String id, HashMap<String, String> newInformation) {
        User user = getUserInfoById(id).getValue();
        if (user == null) {
            errorLog("LiveData is empty", null);
        } else {
            repository.changeUserInformation(id, user.updateUserInfo(newInformation));
        }
    }

    private void errorLog(String message, Error error) {
        Log.e(LOG_TAG, message, error);
    }
}
