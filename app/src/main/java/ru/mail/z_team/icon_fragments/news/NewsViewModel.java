package ru.mail.z_team.icon_fragments.news;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.walks.WalkAnnotation;

public class NewsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "NewsViewModel";
    private final Logger logger;

    private final NewsRepository repository;
    private final LiveData<ArrayList<WalkAnnotation>> currentUserNews;
    private final LiveData<NewsRepository.RefreshStatus> refreshStatus;

    public NewsViewModel(@NonNull Application application) {
        super(application);

        logger = new Logger(LOG_TAG, true);

        repository = new NewsRepository(getApplication());
        currentUserNews = repository.getNews();
        refreshStatus = repository.getRefreshStatus();
    }

    public LiveData<ArrayList<WalkAnnotation>> getCurrentUserNews() {
        logger.log("getNews");
        return currentUserNews;
    }

    public LiveData<NewsRepository.RefreshStatus> getRefreshStatus() {
        return refreshStatus;
    }

    public void updateCurrentUserNews() {
        logger.log("updateNews");
        repository.updateCurrentUserNews();
    }
}
