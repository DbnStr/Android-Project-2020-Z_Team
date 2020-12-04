package ru.mail.z_team.icon_fragments.news;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import ru.mail.z_team.icon_fragments.walks.Walk;

public class NewsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "NewsViewModel";
    private final NewsRepository repository;
    private final LiveData<ArrayList<Walk>> currentUserNews;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        repository = new NewsRepository(getApplication());
        currentUserNews = repository.getNews();
    }

    public void updateCurrentUserNews() {
        log("updateNews");
        repository.updateCurrentUserNews();
    }

    public LiveData<ArrayList<Walk>> getCurrentUserNews() {
        log("getNews");
        return currentUserNews;
    }

    private void log(final String message) {
        Log.d(LOG_TAG, message);
    }
}
