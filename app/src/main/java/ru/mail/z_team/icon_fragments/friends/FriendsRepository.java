package ru.mail.z_team.icon_fragments.friends;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Response;
import ru.mail.z_team.ApplicationModified;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.icon_fragments.DatabaseNetworkControlExecutor;
import ru.mail.z_team.icon_fragments.Transformer;
import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.local_storage.UserDao;
import ru.mail.z_team.network.DatabaseApiRepository;
import ru.mail.z_team.network.UserApi;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class FriendsRepository {

    private static final String LOG_TAG = "FriendsRepository";
    private final Logger logger;

    private final Context context;

    private final UserApi userApi;

    private final UserDao userDao;
    private final LocalDatabase localDatabase;

    private final MutableLiveData<ArrayList<Friend>> currentUserFriends = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Friend>> currentUserFriendRequestList = new MutableLiveData<>();
    private final MutableLiveData<User> currentUserProfileData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> userExistence = new MutableLiveData<>();

    public FriendsRepository(Context context) {
        this.context = context;

        userApi = DatabaseApiRepository.from(context).getUserApi();

        logger = new Logger(LOG_TAG, true);

        localDatabase = ApplicationModified.from(context).getLocalDatabase();
        userDao = localDatabase.getUserDao();
    }

    public void setCurrentUserProfileData(User user) {
        currentUserProfileData.postValue(user);
    }

    public LiveData<User> getCurrentUserProfileData() {
        return currentUserProfileData;
    }


    public LiveData<ArrayList<Friend>> getCurrentUserFriends() {
        return currentUserFriends;
    }

    public LiveData<ArrayList<Friend>> getCurrentUserFriendsRequestList() {
        return currentUserFriendRequestList;
    }

    public void updateCurrentUserFriends() {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        logger.log("update user - " + currentUserId);
        DatabaseNetworkControlExecutor executor = new DatabaseNetworkControlExecutor(context) {
            @Override
            public void networkRun() {
                getUserFriendsFromRemoteDBAndAddTheseInLocalDB(currentUserId);
            }

            @Override
            public void noNetworkRun() {
                getCurrentUserFriendsFromLocalDB();
            }
        };
        executor.run();
    }

    private void getUserFriendsFromRemoteDBAndAddTheseInLocalDB(final String userId) {
        userApi.getUserById(userId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("Fail with update");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                currentUserFriends.postValue(Transformer.transformToUser(response.body()).getFriends());

                replaceOldFriendsListInDbWithNew(Transformer.transformToUser(response.body()).getFriends(), userId);
            }
        });
    }

    private void replaceOldFriendsListInDbWithNew(final ArrayList<Friend> newFriends, final String userId) {
        localDatabase.databaseWriteExecutor.execute(() -> userDao.deleteAllFriendsAndAddNew(Transformer.transformToLocalDBFriendALl(newFriends, userId)));
    }

    private void getCurrentUserFriendsFromLocalDB() {
        localDatabase.databaseWriteExecutor.execute(() ->
                currentUserFriends.postValue(Transformer.transformToFriendAll(userDao.getUserFriends())));
    }

    public void acceptFriendRequest(int number) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        userApi.getFriendsRequest(currentUserId, number).enqueue(new DatabaseCallback<UserApi.Friend>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.Friend> response) {
                logger.log("Friend request no longer exists");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.Friend> response) {
                UserApi.Friend newFriend = response.body();
                addFriendToUserWithoutNumber(currentUserId, newFriend);

                deleteFriendRequest(currentUserId, number);
            }
        });
    }

    private void addFriendToUserWithoutNumber(final String userId, final UserApi.Friend newFriend) {
        userApi.getUserFriendsById(userId).enqueue(new DatabaseCallback<ArrayList<UserApi.Friend>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<UserApi.Friend>> response) {
                addFriendToUserByNumberOnFriendList(userId, 0, newFriend);
                updateCurrentUserFriends();
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<UserApi.Friend>> response) {
                addFriendToUserByNumberOnFriendList(userId, response.body().size(), newFriend);
                updateCurrentUserFriends();
            }
        });
    }

    private void deleteFriendRequest(final String userId, final int numberOnList) {
        userApi.deleteFriendRequest(userId, numberOnList).enqueue(new DatabaseCallback<UserApi.Friend>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.Friend> response) {
                logger.log("Successfully delete friend request");
                updateCurrentUserFriendRequestList();
            }

            @Override
            public void onSuccessResponse(Response<UserApi.Friend> response) {

            }
        });
    }

    public void updateCurrentUserFriendRequestList() {
        final String currentUserId = FirebaseAuth.getInstance().getUid();

        userApi.getFriendRequestList(currentUserId).enqueue(new DatabaseCallback<ArrayList<UserApi.Friend>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<UserApi.Friend>> response) {
                logger.log("the current user has no new friend requests");

                currentUserFriendRequestList.postValue(new ArrayList<>());
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<UserApi.Friend>> response) {
                logger.log("the current user has " + response.body().size() + " friend requests");

                currentUserFriendRequestList.postValue(Transformer.transformToFriendAll(response.body()));
            }
        });
    }

    public void checkUserExistence(String id) {
        logger.log("checkUserExistence");
        userApi.getUserById(id).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.log("posted false");
                userExistence.postValue(false);
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                userExistence.postValue(true);
            }
        });
    }

    public LiveData<Boolean> userExists() {
        logger.log("userExists");
        return userExistence;
    }

    public void addFriendToCurrentUserAndAddFriendRequestToFriend(String newFriendId, int num) {
        logger.log("addFriend");
        String curUserId = FirebaseAuth.getInstance().getUid();

        userApi.getUserById(newFriendId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("Failed to get " + newFriendId + " user");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                UserApi.Friend friend = Transformer.transformToUserApiFriend(response.body());

                userApi.getUserById(curUserId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
                    @Override
                    public void onNullResponse(Response<UserApi.User> response) {
                        logger.errorLog("failed to load " + curUserId + " user");
                    }

                    @Override
                    public void onSuccessResponse(Response<UserApi.User> response) {
                        UserApi.Friend friendsReq = Transformer.transformToUserApiFriend(response.body());
                        addFriendToFriendRequest(newFriendId, friendsReq);
                    }
                });

                addFriendToUserByNumberOnFriendList(curUserId, num, friend);

                addFriendIdToFriendsIdsList(curUserId, num, friend.id);
            }
        });
    }

    private void addFriendToFriendRequest(final String userId, final UserApi.Friend friend) {
        userApi.getFriendRequestList(userId).enqueue(new DatabaseCallback<ArrayList<UserApi.Friend>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<UserApi.Friend>> response) {
                addFriendToFriendRequestListAtNumber(userId, 0, friend);
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<UserApi.Friend>> response) {
                final int number = response.body().size();
                addFriendToFriendRequestListAtNumber(userId, number, friend);
            }
        });
    }

    private void addFriendToFriendRequestListAtNumber(final String userId, final int numberOnList, final UserApi.Friend friend) {
        userApi.addFriendToFriendsRequest(userId, numberOnList, friend).enqueue(new DatabaseCallback<UserApi.Friend>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.Friend> response) {
                logger.errorLog("failed to add new friendRequest " + friend.id);
            }

            @Override
            public void onSuccessResponse(Response<UserApi.Friend> response) {
                logger.log("successfully add friend " + friend.id + " to friendRequest");
            }
        });
    }

    private void addFriendIdToFriendsIdsList(final String user, final int number, final String friendId) {
        userApi.addFriendId(user, number, friendId).enqueue(new DatabaseCallback<String>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<String> response) {
                logger.log("failed to add friend id");
            }

            @Override
            public void onSuccessResponse(Response<String> response) {
                logger.log("successfully add friend id");
            }
        });
    }

    private void addFriendToUserByNumberOnFriendList(final String userId, final int number, final UserApi.Friend friend) {
        userApi.addFriend(userId, number, friend).enqueue(new DatabaseCallback<UserApi.Friend>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.Friend> response) {
                logger.errorLog("Failed to add friend " + friend.id);
            }

            @Override
            public void onSuccessResponse(Response<UserApi.Friend> response) {
                updateCurrentUserFriends();
            }
        });
    }

    public void updateCurrentUserProfileData(String id) {
        userApi.getUserById(id).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("failed to load user data");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                currentUserProfileData.postValue(Transformer.transformToUser(response.body()));
            }
        });
    }
}
