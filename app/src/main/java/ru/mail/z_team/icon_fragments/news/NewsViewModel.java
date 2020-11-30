package ru.mail.z_team.icon_fragments.news;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import ru.mail.z_team.icon_fragments.walks.Walk;
import ru.mail.z_team.user.UserRepository;

public class NewsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "NewsViewModel";
    private final UserRepository repository;
    private final LiveData<ArrayList<Walk>> currentUserNews;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        repository = UserRepository.getInstance(getApplication());
        currentUserNews = repository.getNews();
    }

    public void updateNews() {
        Log.d(LOG_TAG, "updateNews");
        repository.updateNews();
    }

    public LiveData<ArrayList<Walk>> getNews() {
        Log.d(LOG_TAG, "getNews");
        return currentUserNews;
    }
}
