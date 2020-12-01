package ru.mail.z_team.user;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.HashMap;

public class UserViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "UserViewModel";
    private final UserRepository repository;
    private final LiveData<User> currentUserInfo;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = UserRepository.getInstance(getApplication());
        currentUserInfo = repository.getCurrentUser();
    }

    public LiveData<User> getCurrentUser() {
        log("getCurrentUser");
        return currentUserInfo;
    }

    public void updateCurrentUser() {
        repository.updateCurrentUser();
    }

    public void addFriend(String id, int num) {
        Log.d(LOG_TAG, "addFriend");
        User currentUser = repository.getCurrentUser().getValue();
        if (! currentUser.isThisFriendAdded(id)) {
            repository.addFriend(id, num);
        } else {
            errorLog("Friend already added", null);
        }
    }

    public LiveData<Boolean> userExists(){
        Log.d(LOG_TAG, "userExists");
        return repository.userExists();
    }

    public void changeUserInformation(final String id, HashMap<String, String> newInformation) {
        User user = getCurrentUser().getValue();
        if (user == null) {
            errorLog("LiveData is empty", null);
        } else {
            repository.changeUserInformation(id, user.updateUserInfo(newInformation));
        }
    }

    private void errorLog(String message, Error error) {
        Log.e(LOG_TAG, message, error);
    }

    public void checkUserExistence(final String id) {
        Log.d(LOG_TAG, "checkUserExistence");
        repository.checkUserExistence(id);
    }

    public LiveData<User> getOtherUserInfo() {
        return repository.getOtherUserInfo();
    }

    public void updateOtherUser(String id) {
        repository.updateOtherUser(id);
    }

    private void log(final String message) {
        Log.d(LOG_TAG, message);
    }
}
