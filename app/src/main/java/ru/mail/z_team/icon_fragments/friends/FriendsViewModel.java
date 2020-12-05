package ru.mail.z_team.icon_fragments.friends;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ru.mail.z_team.user.User;

public class FriendsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "FriendsViewModel";

    private final FriendsRepository repository;

    private final LiveData<User> currentUserData;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        repository = new FriendsRepository(getApplication());
        currentUserData = repository.getCurrentUser();
    }

    public LiveData<User> getCurrentUser() {
        return currentUserData;
    }

    public void updateCurrentUser() {
        repository.updateCurrentUser();
    }

    public void checkUserExistence(final String id) {
        log("checkUserExistence");
        repository.checkUserExistence(id);
    }

    public LiveData<Boolean> userExists(){
        log("userExists");
        return repository.userExists();
    }

    public void addFriendToCurrentUser(String id) {
        log("addFriend");
        User currentUser = currentUserData.getValue();
        if (! currentUser.isThisFriendAdded(id)) {
            repository.addFriendToCurrentUser(id, currentUser.getFriendsListSize());
        } else {
            errorLog("Friend already added", null);
        }
    }

    private void log(final String message) {
        Log.d(LOG_TAG, message);
    }

    private void errorLog(String message, Error error) {
        Log.e(LOG_TAG, message, error);
    }
}
