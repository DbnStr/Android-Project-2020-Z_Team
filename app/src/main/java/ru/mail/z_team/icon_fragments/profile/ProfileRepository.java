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
import ru.mail.z_team.icon_fragments.Transformer;
import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.local_storage.UserDao;
import ru.mail.z_team.local_storage.UserFriend;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

import static ru.mail.z_team.icon_fragments.Transformer.transformToLocalDBFriend;

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
                    currentUserData.postValue(Transformer.transformToUser(response.body()));

                    localDatabase.databaseWriteExecutor.execute(() -> {
                        userDao.insert(Transformer.transformToLocalDBUser(response.body()));

                        User currentUser = Transformer.transformToUser(response.body());
                        ArrayList<Friend> friends = currentUser.getFriends();
                        for (Friend friend : friends) {
                            userDao.insert(Transformer.transformToLocalDBFriend(friend, currentUserId));
                        }

                        List<UserFriend> fr = userDao.getUserFriends();
                        logger.log(fr.get(0).name);
                    });

                }
            });
        } else {
            localDatabase.databaseWriteExecutor.execute(() -> {
                currentUserData.postValue(Transformer.transformToUser(userDao.getById(currentUserId)));
            });
        }
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

                localDatabase.databaseWriteExecutor.execute(() -> {
                    userDao.insert(Transformer.transformToLocalDBUser(newInformation));
                });
            }
        });
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
