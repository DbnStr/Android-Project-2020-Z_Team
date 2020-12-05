package ru.mail.z_team.icon_fragments.friends;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ru.mail.z_team.Logger;
import ru.mail.z_team.user.User;

public class FriendsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "FriendsViewModel";
    private final Logger logger;

    private final FriendsRepository repository;

    private final LiveData<User> currentUserData;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        logger = new Logger(LOG_TAG, true);
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
        logger.log("checkUserExistence");
        repository.checkUserExistence(id);
    }

    public LiveData<Boolean> userExists(){
        logger.log("userExists");
        return repository.userExists();
    }

    public void addFriendToCurrentUser(String id) {
        logger.log("addFriend");
        User currentUser = currentUserData.getValue();
        if (! currentUser.isThisFriendAdded(id)) {
            repository.addFriendToCurrentUser(id, currentUser.getFriendsListSize());
        } else {
            logger.errorLog("Friend already added");
        }
    }
}
