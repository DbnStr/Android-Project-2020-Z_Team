package ru.mail.z_team.icon_fragments.friends;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import ru.mail.z_team.ApplicationModified;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.local_storage.UserDao;
import ru.mail.z_team.local_storage.UserFriend;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class FriendsRepository {

    private static final String LOG_TAG = "FriendsRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final UserDao userDao;
    private final LocalDatabase localDatabase;

    private final MutableLiveData<ArrayList<Friend>> currentUserFriends = new MutableLiveData<>();
    private final MutableLiveData<Boolean> userExistence = new MutableLiveData<>();

    private int count;

    public FriendsRepository(Context context) {
        userApi = ApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
        userDao = ApplicationModified.from(context).getLocalDatabase().getUserDao();
        localDatabase = ApplicationModified.from(context).getLocalDatabase();
    }

    public LiveData<ArrayList<Friend>> getCurrentUserFriends() {
        return currentUserFriends;
    }

    public void updateCurrentUserFriends() {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        logger.log("update user - " + currentUserId);

        userApi.getUserById(currentUserId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("Fail with update");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                currentUserFriends.postValue(transformToUser(response.body()).getFriends());

                localDatabase.databaseWriteExecutor.execute(() -> {
                    User currentUser = transformToUser(response.body());
                    ArrayList<Friend> friends = currentUser.getFriends();
                    userDao.deleteAllFriendsAndAddNew(transformToLocalDBFriendALl(friends, currentUserId));
                });
            }
        });
    }

    private User transformToUser(UserApi.User user) {
        String name = user.name;
        if (name == null) {
            name = "Anonymous";
        }
        ArrayList<Friend> userFriends = new ArrayList<>();
        if (user.friends != null) {
            for (UserApi.Friend friend : user.friends) {
                userFriends.add(transformToFriend(friend));
            }
        }
        return new User(
                name,
                user.age,
                user.id,
                userFriends
        );
    }

    private List<ru.mail.z_team.local_storage.Friend> transformToLocalDBFriendALl(List<Friend> friends, String currentUserId) {
        List<ru.mail.z_team.local_storage.Friend> result = new ArrayList<>();
        for(Friend friend : friends) {
            result.add(transformToLocalDBFriend(friend, currentUserId));
        }
        return result;
    }

    private ru.mail.z_team.local_storage.Friend transformToLocalDBFriend(Friend friend, String currentUserId) {
        return new ru.mail.z_team.local_storage.Friend(
                friend.name,
                friend.id,
                currentUserId
        );
    }

    private Friend transformToFriend(UserApi.Friend friend) {
        return new Friend(friend.name, friend.id);
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

    public void addFriendToCurrentUser(String id, int num) {
        logger.log("addFriend");
        String curUserId = FirebaseAuth.getInstance().getUid();

        userApi.getUserById(id).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("Failed to get " + id + " user");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                UserApi.Friend friend = transformToUserApiFriend(response.body());
                if (response.body().friends == null) {
                    count = 0;
                } else {
                    count = response.body().friends.size();
                }

                addFriendToUser(curUserId, num, friend);

                addFriendIdToFriendsIdsList(curUserId, num, friend.id);
            }
        });
        userApi.getUserById(curUserId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("Failed to get " + curUserId + " user");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                UserApi.Friend friend = transformToUserApiFriend(response.body());

                addFriendToUser(id, count, friend);

                addFriendIdToFriendsIdsList(id, count, friend.id);
            }
        });
    }

    private UserApi.Friend transformToUserApiFriend(UserApi.User user) {
        UserApi.Friend result = new UserApi.Friend();
        result.id = user.id;
        result.name = user.name;
        return result;
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

    private void addFriendToUser(final String userId, final int number, final UserApi.Friend friend) {
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
}
