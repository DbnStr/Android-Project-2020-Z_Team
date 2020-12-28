package ru.mail.z_team.icon_fragments.friends;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import ru.mail.z_team.Logger;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class FriendsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "FriendsViewModel";
    private final Logger logger;

    private final FriendsRepository repository;

    private final LiveData<ArrayList<Friend>> currentUserFriends;
    private final LiveData<ArrayList<Friend>> currentUserFriendRequestList;
    private final LiveData<User> currentDisplayedUser;
    private final LiveData<FriendsRepository.RefreshStatus> friendListRefreshStatus;
    private final LiveData<FriendsRepository.RefreshStatus> friendRequestRefreshStatus;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        logger = new Logger(LOG_TAG, true);

        repository = new FriendsRepository(getApplication());
        currentDisplayedUser = repository.getCurrentUserProfileData();
        currentUserFriends = repository.getCurrentUserFriends();
        currentUserFriendRequestList = repository.getCurrentUserFriendsRequestList();
        friendListRefreshStatus = repository.getFriendListRefreshStatus();
        friendRequestRefreshStatus = repository.getFriendRequestListRefreshStatus();
    }

    public LiveData<ArrayList<Friend>> getCurrentUserFriends() {
        return currentUserFriends;
    }

    public LiveData<ArrayList<Friend>> getCurrentUserFriendRequestList() {
        return currentUserFriendRequestList;
    }

    public LiveData<FriendsRepository.RefreshStatus> getFriendListRefreshStatus() {
        return friendListRefreshStatus;
    }

    public  LiveData<FriendsRepository.RefreshStatus> getFriendRequestRefreshStatus() {
        return friendRequestRefreshStatus;
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
            if (friends == null) {
                repository.addFriendToCurrentUserAndAddFriendRequestToFriend(id, 0);
            } else {
                repository.addFriendToCurrentUserAndAddFriendRequestToFriend(id, friends.size());
            }
        } else {
            logger.errorLog("LocalDbFriend already added");
        }
    }

    private boolean isFriendWithIDAdded(ArrayList<Friend> friends, String id) {
        if (friends != null) {
            for (Friend friend : friends) {
                if (friend.id.equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setUser(User user){
        repository.setCurrentUserProfileData(user);
    }

    public LiveData<User> getUserProfileData() {
        return  currentDisplayedUser;
    }

    public void updateCurrentDisplayedUser(String id) {
        repository.updateCurrentUserProfileData(id);
    }
}
