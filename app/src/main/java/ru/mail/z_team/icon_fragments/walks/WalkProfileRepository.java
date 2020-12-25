package ru.mail.z_team.icon_fragments.walks;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;

import retrofit2.Response;
import ru.mail.z_team.ApplicationModified;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.icon_fragments.DatabaseNetworkControlExecutor;
import ru.mail.z_team.icon_fragments.Transformer;
import ru.mail.z_team.databases.local_storage.LocalDatabase;
import ru.mail.z_team.databases.local_storage.UserDao;
import ru.mail.z_team.map.Story;
import ru.mail.z_team.databases.network.DatabaseApiRepository;
import ru.mail.z_team.databases.network.UserApi;

public class WalkProfileRepository {
    private static final String LOG_TAG = "WalkProfileRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final UserDao userDao;
    private final LocalDatabase localDatabase;

    private final Context context;

    private final MutableLiveData<Walk> currentDisplayedWalk = new MutableLiveData<>();
    private final MutableLiveData<WalkAnnotation> annotation = new MutableLiveData<>();
    private final MutableLiveData<Story> currentDisplayedStory = new MutableLiveData<>();

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    public WalkProfileRepository(Context context) {
        this.context = context;

        logger = new Logger(LOG_TAG, true);

        userApi = DatabaseApiRepository.from(context).getUserApi();

        localDatabase = ApplicationModified.from(context).getLocalDatabase();
        userDao = localDatabase.getUserDao();
    }

    public LiveData<WalkAnnotation> getAnnotation() {
        return annotation;
    }

    public LiveData<Walk> getCurrentDisplayedWalk() {
        return currentDisplayedWalk;
    }

    public LiveData<Story> getCurrentDisplayedStory() {
        return currentDisplayedStory;
    }

    //изменить получение данных
    public void updateCurrentDisplayedStory(final int number, final String dateOfWalk) {
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseNetworkControlExecutor executor = new DatabaseNetworkControlExecutor(context) {
            @Override
            public void networkRun() {
                userApi.getStory(userId, dateOfWalk, number).enqueue(new DatabaseCallback<UserApi.Story>(LOG_TAG) {
                    @Override
                    public void onNullResponse(Response<UserApi.Story> response) {
                        logger.log("EMPTY STORY");
                        currentDisplayedStory.postValue(new Story());
                    }

                    @Override
                    public void onSuccessResponse(Response<UserApi.Story> response) {
                        logger.log("successfully get walk story");
                        currentDisplayedStory.postValue(Transformer.transformToStory(response.body()));
                    }
                });
            }

            @Override
            public void noNetworkRun() {
                //почему-то не получается достать walk из currentDisplayedWalk
                localDatabase.databaseWriteExecutor.execute(() -> currentDisplayedStory.postValue(Transformer.transformToWalk(userDao.getUserWalkWithStories(userId, dateOfWalk)).getStories().get(number)));
            }
        };
        executor.run();
    }

    public void updateCurrentDisplayedWalk(WalkAnnotation walkAnnotation) {
        String userId = walkAnnotation.getAuthorId();
        String date = sdf.format(walkAnnotation.getDate());
        DatabaseNetworkControlExecutor executor = new DatabaseNetworkControlExecutor(context) {
            @Override
            public void networkRun() {
                getUserWalkFromRemoteDB(userId, date);
            }

            @Override
            public void noNetworkRun() {
                getUserWalkFromLocalDB(userId, date);
            }
        };
        executor.run();
    }

    private void getUserWalkFromRemoteDB(final String userId, final String date) {
        userApi.getWalkByDateAndId(userId, date).enqueue(new DatabaseCallback<UserApi.Walk>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.Walk> response) {
                logger.errorLog("This walk doesn't exist");
            }

            @Override
            public void onSuccessResponse(Response<UserApi.Walk> response) {
                currentDisplayedWalk.postValue(Transformer.transformToWalk(response.body()));
            }
        });
    }

    private void getUserWalkFromLocalDB(final String userId, final String date) {
        localDatabase.databaseWriteExecutor.execute(() -> currentDisplayedWalk.postValue(Transformer.transformToWalk(userDao.getUserWalkWithStories(userId, date))));
    }

    public void setAnnotation(WalkAnnotation walkAnnotation) {
        annotation.postValue(walkAnnotation);
    }
}
