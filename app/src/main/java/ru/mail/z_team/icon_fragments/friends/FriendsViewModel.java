package ru.mail.z_team.icon_fragments.friends;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ru.mail.z_team.AuthRepository;
import ru.mail.z_team.Logger;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class FriendsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "FriendsViewModel";
    private final Logger logger;

    private final FriendsRepository repository;

    private final LiveData<ArrayList<Friend>> currentUserFriends;
    private final LiveData<ArrayList<Friend>> currentUserFriendRequestList;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        logger = new Logger(LOG_TAG, true);
        repository = new FriendsRepository(getApplication());
        currentUserFriends = repository.getCurrentUserFriends();
        currentUserFriendRequestList = repository.getCurrentUserFriendsRequestList();
    }

    public LiveData<ArrayList<Friend>> getCurrentUserFriends() {
        return currentUserFriends;
    }

    public LiveData<ArrayList<Friend>> getCurrentUserFriendRequestList() {
        return currentUserFriendRequestList;
    }

    public void updateCurrentUserFriends() {
        repository.updateCurrentUserFriends();
    }

    public void updateCurrentUserFriendRequestList() {
        repository.updateCurrentUserFriendRequestList();
    }

    public void acceptFriendRequest(final int number) {
        repository.acceptFriendRequest(number);
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
        ArrayList<Friend> friends = currentUserFriends.getValue();
        if (! isFriendWithIDAdded(friends, id)) {
            repository.addFriendToCurrentUser(id, friends.size());
        } else {
            logger.errorLog("Friend already added");
        }
    }

    private boolean isFriendWithIDAdded(ArrayList<Friend> friends, String id) {
        for(Friend friend : friends) {
            if (friend.id.equals(id)) {
                return true;
            }
        }
        return false;
    }
}
