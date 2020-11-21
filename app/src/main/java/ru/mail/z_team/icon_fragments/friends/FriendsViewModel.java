package ru.mail.z_team.icon_fragments.friends;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FriendsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "ProfileViewModel";
    private final FriendsRepository repository = new FriendsRepository(getApplication());

    public FriendsViewModel(@NonNull Application application) {
        super(application);
    }

    public void update(final String id) {
        repository.update(id);
    }

    public LiveData<List<String>> getUserFriendsById(final String id) {
        return repository.getUserFriendsById(id);
    }

    private void errorLog(String message, Error error) {
        Log.e(LOG_TAG, message, error);
    }
}
