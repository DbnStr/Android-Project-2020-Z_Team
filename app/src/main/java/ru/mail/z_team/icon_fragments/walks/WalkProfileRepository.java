package ru.mail.z_team.icon_fragments.walks;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;

import retrofit2.Response;
import ru.mail.z_team.ApplicationModified;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.icon_fragments.DatabaseNetworkControlExecutor;
import ru.mail.z_team.icon_fragments.Transformer;
import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.local_storage.UserDao;
import ru.mail.z_team.map.Story;
import ru.mail.z_team.network.DatabaseApiRepository;
import ru.mail.z_team.network.UserApi;

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

    public LiveData<Story> getStory() {
        return currentDisplayedStory;
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

    public void setStory(Story story) {
        currentDisplayedStory.postValue(story);
    }
}
