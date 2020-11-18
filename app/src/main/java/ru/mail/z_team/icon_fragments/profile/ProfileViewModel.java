package ru.mail.z_team.icon_fragments.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ProfileViewModel extends AndroidViewModel {

    private final ProfileRepo mRepo = new ProfileRepo(getApplication());

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<User> getUserInfoById(final String id) {
        return mRepo.getUserInfoById(id);
    }

    public void update(final String id) {
        mRepo.update(id);
    }
}
