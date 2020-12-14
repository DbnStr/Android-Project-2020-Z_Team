package ru.mail.z_team.icon_fragments.profile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

public class ProfileRepository {

    private static final String LOG_TAG = "ProfileRepository";
    private final Logger logger;

    private final Context context;

    private final UserApi userApi;
    private final UserDao userDao;
    private final LocalDatabase localDatabase;

    private final MutableLiveData<User> currentUserData = new MutableLiveData<>();

    public ProfileRepository(Context context) {
        this.context = context;

        userApi = ApiRepository.from(context).getUserApi();

        logger = new Logger(LOG_TAG, true);

        localDatabase = ApplicationModified.from(context).getLocalDatabase();
        userDao = localDatabase.getUserDao();
    }

    public LiveData<User> getCurrentUser() {
        return currentUserData;
    }

    public void updateCurrentUser() {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        logger.log("update user - " + currentUserId);

        if (isOnline(context)) {
            userApi.getUserById(currentUserId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
                @Override
                public void onNullResponse(Response<UserApi.User> response) {
                    logger.errorLog("Fail with update");
                }

                @Override
                public void onSuccessResponse(Response<UserApi.User> response) {
                    currentUserData.postValue(transformToUser(response.body()));

                    localDatabase.databaseWriteExecutor.execute(() -> {
                        userDao.insert(transformToLocalDBUser(response.body()));

                        User currentUser = transformToUser(response.body());
                        ArrayList<Friend> friends = currentUser.getFriends();
                        for (Friend friend : friends) {
                            userDao.insert(transformToLocalDBFriend(friend, currentUserId));
                        }

                        List<UserFriend> fr = userDao.getUserFriends();
                        logger.log(fr.get(0).name);
                    });

                }
            });
        } else {
            localDatabase.databaseWriteExecutor.execute(() -> {
                currentUserData.postValue(transformToUser(userDao.getById(currentUserId)));
            });
        }
    }

    private User transformToUser(ru.mail.z_team.local_storage.User user) {
        return new User(
                user.name,
                user.age,
                user.id,
                new ArrayList<>()
        );
    }

    private ru.mail.z_team.local_storage.Friend transformToLocalDBFriend(Friend friend, String currentUserId) {
        return new ru.mail.z_team.local_storage.Friend(
                friend.name,
                friend.id,
                currentUserId
        );
    }

    private ru.mail.z_team.local_storage.User transformToLocalDBUser(UserApi.User user) {
        ru.mail.z_team.local_storage.User result = new ru.mail.z_team.local_storage.User();
        String name = user.name;
        if (name == null) {
            name = "Anonymous";
        }
        result.name = name;
        result.id = user.id;
        result.age = user.age;

        return result;
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

    public void changeCurrentUserInformation(User newInformation) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        userApi.changeUserInformation(currentUserId, transformToUserApiUser(newInformation)).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("Failed with change information about " + currentUserId);
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                logger.log("Change information about " + currentUserId);

                localDatabase.databaseWriteExecutor.execute(() -> {
                    userDao.insert(transformToLocalDBUser(newInformation));
                });
            }
        });
    }

    private UserApi.User transformToUserApiUser(User user) {
        UserApi.User result = new UserApi.User();
        result.id = user.getId();
        result.name = user.getName();
        result.age = user.getAge();
        return result;
    }

    private ru.mail.z_team.local_storage.User transformToLocalDBUser(User user) {
        ru.mail.z_team.local_storage.User result = new ru.mail.z_team.local_storage.User();
        result.name = user.name;
        result.id = user.id;
        result.age = user.age;

        return result;
    }

    private Friend transformToFriend(UserApi.Friend friend) {
        return new Friend(friend.name, friend.id);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
