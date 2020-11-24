package ru.mail.z_team.user;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "UserViewModel";
    private final UserRepository repository = new UserRepository(getApplication());

    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<User> getUserInfo() {
        return repository.getUserInfo();
    }

    public void update(final String id) {
        repository.update(id);
    }

    public void updateFriends(final String id) {
        Log.d(LOG_TAG, "updateFriends");
        repository.updateFriends(id);
    }

    public void addFriend(String id, int num) {
        Log.d(LOG_TAG, "addFriend");
        repository.addFriend(id, num);
    }

    public LiveData<Boolean> userExists(){
        Log.d(LOG_TAG, "userExists");
        return repository.userExists();
    }

    public LiveData<List<User>> getUserFriendsById(final String id) {
        return repository.getUserFriendsById(id);
    }

    public void changeUserInformation(final String id, HashMap<String, String> newInformation) {
        User user = getUserInfo().getValue();
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
}
