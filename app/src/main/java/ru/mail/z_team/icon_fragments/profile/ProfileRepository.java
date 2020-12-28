package ru.mail.z_team.icon_fragments.profile;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Response;
import ru.mail.z_team.ApplicationModified;
import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.databases.DatabaseFriend;
import ru.mail.z_team.databases.DatabaseUser;
import ru.mail.z_team.databases.local_storage.LocalDatabase;
import ru.mail.z_team.databases.local_storage.UserDao;
import ru.mail.z_team.databases.network.DatabaseApiRepository;
import ru.mail.z_team.databases.network.UserApi;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.icon_fragments.DatabaseNetworkControlExecutor;
import ru.mail.z_team.icon_fragments.Transformer;
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
        userApi.getUserById(userId).enqueue(new DatabaseCallback<DatabaseUser>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<DatabaseUser> response) {
                logger.errorLog("Fail with update");
            }

            @Override
            public void onSuccessResponse(Response<DatabaseUser> response) {
                User user = Transformer.transformToUser(response.body());
                if (user.getImageUrl() != null
                        && !user.getImageUrl().isEmpty()
                        && !user.getImageUrl().equals("no Image")) {
                    StorageReference reference = FirebaseStorage.getInstance()
                            .getReferenceFromUrl(context.getString(R.string.base_storage_url) + user.getImageUrl());
                    localDatabase.databaseWriteExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Bitmap bitmap =
                                        Glide.with(context.getApplicationContext())
                                                .asBitmap()
                                                .load(reference)
                                                .submit(800, 800)
                                                .get();
                                user.bitmap = bitmap;
                                currentUserData.postValue(user);
                            } catch (Error | ExecutionException
                                    | InterruptedException error) {
                                logger.log(error.getMessage());
                            }
                        }
                    });
                } else {
                    currentUserData.postValue(user);
                }
                addUserInLocalDB(response.body());
            }
        });
    }


    private void addUserInLocalDB(final DatabaseUser user) {
        localDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);

            User currentUser = Transformer.transformToUser(user);
            ArrayList<Friend> friends = currentUser.getFriends();
            for (Friend friend : friends) {
                userDao.insert(Transformer.transformToDatabaseFriend(friend, user.id));
            }

            List<DatabaseFriend> fr = userDao.getUserFriends(currentUser.id);
        });
    }

    private void getUserFromLocalDB(final String userID) {
        localDatabase.databaseWriteExecutor.execute(() -> currentUserData.postValue(Transformer.transformToUser(userDao.getUserById(userID))));
    }

    public void changeCurrentUserInformation(User newInformation) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        userApi.changeUserInformation(currentUserId, Transformer.transformToDatabaseUser(newInformation)).enqueue(new DatabaseCallback<DatabaseUser>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<DatabaseUser> response) {
                logger.errorLog("Failed with change information about " + currentUserId);
            }

            @Override
            public void onSuccessResponse(Response<DatabaseUser> response) {
                logger.log("Change information about " + currentUserId);

                localDatabase.databaseWriteExecutor.execute(() -> userDao.insert(Transformer.transformToDatabaseUser(newInformation)));
            }
        });
    }
}
