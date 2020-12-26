package ru.mail.z_team.icon_fragments.walks;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;
import ru.mail.z_team.ApplicationModified;
import ru.mail.z_team.Logger;
import ru.mail.z_team.databases.DatabaseWalk;
import ru.mail.z_team.databases.DatabaseWalkAnnotation;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.icon_fragments.DatabaseNetworkControlExecutor;
import ru.mail.z_team.icon_fragments.Transformer;
import ru.mail.z_team.databases.local_storage.LocalDatabase;
import ru.mail.z_team.databases.local_storage.UserDao;
import ru.mail.z_team.databases.network.DatabaseApiRepository;
import ru.mail.z_team.databases.network.UserApi;

public class WalksRepository {

    private static final String LOG_TAG = "WalksRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final UserDao userDao;
    private final LocalDatabase localDatabase;

    private final MutableLiveData<ArrayList<WalkAnnotation>> currentUserWalks = new MutableLiveData<>();

    private final Context context;

    public WalksRepository(Context context) {
        this.context = context;

        userApi = DatabaseApiRepository.from(context).getUserApi();

        logger = new Logger(LOG_TAG, true);

        localDatabase = ApplicationModified.from(context).getLocalDatabase();
        userDao = localDatabase.getUserDao();
    }

    public LiveData<ArrayList<WalkAnnotation>> getCurrentUserWalks() {
        return currentUserWalks;
    }

    public void updateCurrentUserWalksAnnotations() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        DatabaseNetworkControlExecutor executor = new DatabaseNetworkControlExecutor(context) {
            @Override
            public void networkRun() {
                getWalksAnnotationsFromRemoteDBAndInsertTheseInLocalDB(currentUserId);
            }

            @Override
            public void noNetworkRun() {
                getCurrentUserWalksAnnotationsFromLocalD();
            }
        };
        executor.run();
    }

    private void getWalksAnnotationsFromRemoteDBAndInsertTheseInLocalDB(final String userId) {
        userApi.getUserWalksAnnotationsById(userId).enqueue(new DatabaseCallback<ArrayList<DatabaseWalkAnnotation>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<DatabaseWalkAnnotation>> response) {
                logger.log("Walks was empty");
                currentUserWalks.postValue(new ArrayList<>());
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<DatabaseWalkAnnotation>> response) {
                logger.log("Successful update current user walks");
                currentUserWalks.postValue(Transformer.transformToWalkAnnotationAll(response.body()));

                insertWalkAnnotationListInLocalDB(response.body());

                userApi.getAllWalks(userId).enqueue(new DatabaseCallback<Map<String, DatabaseWalk>>(LOG_TAG) {
                    @Override
                    public void onNullResponse(Response<Map<String, DatabaseWalk>> response) {
                        logger.log("NULL");
                    }

                    @Override
                    public void onSuccessResponse(Response<Map<String, DatabaseWalk>> response) {
                        localDatabase.databaseWriteExecutor.execute(() -> {
                            List<DatabaseWalk> walks = new ArrayList<>();
                            response.body().forEach((date, walk) -> {
                                walks.add(walk);
                            });
                            userDao.deleteAllWalkAndAddNew(walks);
                            response.body().forEach((date, walk) -> userDao.deleteAllWalkStoryAndAddNew(Transformer.transformToLocalDBStoryAll(walk.stories, walk.date), walk.date));
                        });
                    }
                });
            }
        });
    }

    private void insertWalkAnnotationListInLocalDB(final List<DatabaseWalkAnnotation> walksAnnotations) {
        localDatabase.databaseWriteExecutor.execute(() ->
                userDao.deleteAllWalkAnnotationAndAddNew(walksAnnotations));
    }

    private void getCurrentUserWalksAnnotationsFromLocalD() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        localDatabase.databaseWriteExecutor.execute(() ->
                currentUserWalks.postValue(Transformer.transformToWalkAnnotationAll(userDao.getUserWalksAnnotations(currentUserId))));
    }
}
