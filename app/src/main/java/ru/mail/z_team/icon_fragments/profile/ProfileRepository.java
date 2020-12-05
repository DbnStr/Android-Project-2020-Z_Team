package ru.mail.z_team.icon_fragments.profile;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Response;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class ProfileRepository {

    private static final String LOG_TAG = "ProfileRepository";

    private final UserApi userApi;

    private final MutableLiveData<User> currentUserData = new MutableLiveData<>();

    public ProfileRepository(Context context) {
        userApi = ApiRepository.from(context).getUserApi();
    }

    public LiveData<User> getCurrentUser() {
        return currentUserData;
    }

    public void updateCurrentUser() {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        log("update user - " + currentUserId);

        userApi.getUserById(currentUserId).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                errorLog("Fail with update", null);
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                currentUserData.postValue(transformToUser(response.body()));
            }
        });
    }

    private User transformToUser(UserApi.User user) {
        String name = user.name;
        if (name == null) {
            name = "Anonymous";
        }
        ArrayList<Friend> userFriends = new ArrayList<>();
        if (user.friends != null) {
            for (UserApi.Friend friend : user.friends) {
                userFriends.add(transformToFriend(friend));
            }
        }
        return new User(
                name,
                user.age,
                user.id,
                userFriends
        );
    }

    public void changeCurrentUserInformation(User newInformation) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        userApi.changeUserInformation(currentUserId, transformToUserApiUser(newInformation)).enqueue(new DatabaseCallback<UserApi.User>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<UserApi.User> response) {
                errorLog("Failed with change information about " + currentUserId, null);
            }

            @Override
            public void onSuccessResponse(Response<UserApi.User> response) {
                log("Change information about " + currentUserId);
            }
        });
    }

    private UserApi.User transformToUserApiUser(User user) {
        UserApi.User result = new UserApi.User();
        result.id = user.getId();
        result.name = user.getName();
        result.age = user.getAge();
        return result;
    }

    private Friend transformToFriend(UserApi.Friend friend) {
        return new Friend(friend.name, friend.id);
    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }

    private void errorLog(String message, Throwable t) {
        Log.e(LOG_TAG, message, t);
    }

}
