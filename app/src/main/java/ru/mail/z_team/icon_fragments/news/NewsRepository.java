package ru.mail.z_team.icon_fragments.news;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.databases.DatabaseWalkAnnotation;
import ru.mail.z_team.databases.network.DatabaseApiRepository;
import ru.mail.z_team.databases.network.UserApi;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.icon_fragments.Transformer;
import ru.mail.z_team.icon_fragments.walks.WalkAnnotation;

public class NewsRepository {

    private static final String LOG_TAG = "NewsRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final MutableLiveData<ArrayList<WalkAnnotation>> currentUserNews = new MutableLiveData<>();
    private final MutableLiveData<RefreshStatus> refreshStatus = new MutableLiveData<>();

    public NewsRepository(Context context) {
        userApi = DatabaseApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
    }

    public LiveData<ArrayList<WalkAnnotation>> getNews() {
        logger.log("get news " + currentUserNews.toString());
        return currentUserNews;
    }

    public LiveData<RefreshStatus> getRefreshStatus() {
        return refreshStatus;
    }

    public void updateCurrentUserNews() {
        logger.log("updateNews");
        String curId = FirebaseAuth.getInstance().getUid();
        userApi.getUserFriendsIds(curId).enqueue(new DatabaseCallback<ArrayList<String>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<String>> response) {
                currentUserNews.postValue(new ArrayList<>());
                refreshStatus.postValue(RefreshStatus.OK);
                logger.log(curId + " doesn't have friends");
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<String>> response) {
                logger.log(curId + " have friends " + response.body().size());
                compileNewsAndPostInCurrentNews(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                super.onFailure(call, t);
                refreshStatus.postValue(RefreshStatus.FAILED);
            }
        });
    }

    private void compileNewsAndPostInCurrentNews(ArrayList<String> ids) {
        ArrayList<WalkAnnotation> news = new ArrayList<>();

        logger.log("Compile news");
        for (String id : ids) {
            logger.log("Compile news... " + ids.indexOf(id));
            userApi.getUserWalksAnnotationsById(id).enqueue(new DatabaseCallback<ArrayList<DatabaseWalkAnnotation>>(LOG_TAG) {
                @Override
                public void onNullResponse(Response<ArrayList<DatabaseWalkAnnotation>> response) {
                    logger.log(id + " doesn't have walks");
                }

                @Override
                public void onSuccessResponse(Response<ArrayList<DatabaseWalkAnnotation>> response) {
                    logger.log(id + " have walks");
                    news.addAll(Transformer.transformToWalkAnnotationAll(response.body()));
                    Collections.sort(news);
                    currentUserNews.postValue(news);
                    refreshStatus.postValue(RefreshStatus.OK);
                }
            });
        }
    }

    public enum RefreshStatus {
        OK,
        FAILED
    }
}
