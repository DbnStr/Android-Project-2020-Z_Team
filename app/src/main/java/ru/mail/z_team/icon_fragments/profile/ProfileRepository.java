package ru.mail.z_team.icon_fragments.profile;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Response;
import ru.mail.z_team.ApplicationModified;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.local_storage.UserDao;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class ProfileRepository {

    private static final String LOG_TAG = "ProfileRepository";
    private final Logger logger;

    private final UserApi userApi;
    private final UserDao userDao;
    private final LocalDatabase localDatabase;

    private final MutableLiveData<User> currentUserData = new MutableLiveData<>();

    public ProfileRepository(Context context) {
        userApi = ApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
        userDao = ApplicationModified.from(context).getLocalDatabase().getUserDao();
        localDatabase = ApplicationModified.from(context).getLocalDatabase();
    }

    public LiveData<User> getCurrentUser() {
        return currentUserData;
    }

    public void updateCurrentUser() {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        logger.log("update user - " + currentUserId);

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
                });
            }
        });
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
}
