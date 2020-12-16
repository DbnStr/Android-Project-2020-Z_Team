package ru.mail.z_team.icon_fragments.profile;

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
import ru.mail.z_team.icon_fragments.DatabaseNetworkControlExecutor;
import ru.mail.z_team.icon_fragments.Transformer;
import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.local_storage.UserDao;
import ru.mail.z_team.local_storage.UserFriend;
import ru.mail.z_team.network.DatabaseApiRepository;
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

        userApi = DatabaseApiRepository.from(context).getUserApi();

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
        DatabaseNetworkControlExecutor executor = new DatabaseNetworkControlExecutor(context) {
            @Override
            public void networkRun() {
                getUserFromRemoteDBAndAddHimInLocalDB(currentUserId);
            }

            @Override
            public void noNetworkRun() {
                getUserFromLocalDB(currentUserId);
            }
        };
        executor.run();
    }

    private void getUserFromRemoteDBAndAddHimInLocalDB(final String userId) {
        userApi.getUserById(userId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("Fail with update");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                currentUserData.postValue(Transformer.transformToUser(response.body()));

                addUserInLocalDB(response.body());
            }
        });
    }


    private void addUserInLocalDB(final UserApi.User user) {
        localDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(Transformer.transformToLocalDBUser(user));

            User currentUser = Transformer.transformToUser(user);
            ArrayList<Friend> friends = currentUser.getFriends();
            for (Friend friend : friends) {
                userDao.insert(Transformer.transformToLocalDBFriend(friend, user.id));
            }

            List<UserFriend> fr = userDao.getUserFriends();
            logger.log(fr.get(0).name);
        });
    }

    private void getUserFromLocalDB(final String userID) {
        localDatabase.databaseWriteExecutor.execute(() -> currentUserData.postValue(Transformer.transformToUser(userDao.getById(userID))));
    }

    public void changeCurrentUserInformation(User newInformation) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        userApi.changeUserInformation(currentUserId, Transformer.transformToUserApiUser(newInformation)).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                logger.errorLog("Failed with change information about " + currentUserId);
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                logger.log("Change information about " + currentUserId);

                localDatabase.databaseWriteExecutor.execute(() -> userDao.insert(Transformer.transformToLocalDBUser(newInformation)));
            }
        });
    }
}
