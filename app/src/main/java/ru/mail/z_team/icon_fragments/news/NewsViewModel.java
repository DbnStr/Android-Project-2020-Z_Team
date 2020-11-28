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

    private static final String LOG_TAG = "WalkViewModel";
    UserRepository repository = new UserRepository(getApplication());

    public NewsViewModel(@NonNull Application application) {
        super(application);
    }

    public void updateNews() {
        Log.d(LOG_TAG, "updateNews");
        repository.updateNews();
    }

    public LiveData<ArrayList<Walk>> getNews() {
        Log.d(LOG_TAG, "updateNews");
        return repository.getNew();
    }
}
