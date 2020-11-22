package ru.mail.z_team.icon_fragments.friends;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.mail.z_team.icon_fragments.profile.UserRepository;

public class FriendsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "ProfileViewModel";
    private final UserRepository userRepository = new UserRepository(getApplication());

    public FriendsViewModel(@NonNull Application application) {
        super(application);
    }

    public void update(final String id) {
        userRepository.updateFriends(id);
    }

    public boolean userExists(final String id){
        return userRepository.userExists(id);
    }

    public LiveData<List<String>> getUserFriendsById(final String id) {
        return userRepository.getUserFriendsById(id);
    }

    private void errorLog(String message, Error error) {
        Log.e(LOG_TAG, message, error);
    }

    public void addFriend(String id, int num) {
        Log.d(LOG_TAG, "addFriend");
        userRepository.addFriend(id, num);
    }
}
