package ru.mail.z_team.user;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class UserRepository {

    private static final String LOG_TAG = "UserRepository";
    private static final int FAILED_TO_READ_WRITE_DB_CODE = 401;

    private final Context context;
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> userFriends = new MutableLiveData<>();
    private final MutableLiveData<Boolean> userExistence = new MutableLiveData<>();

    private final UserApi userApi;

    public UserRepository(Context context) {
        this.context = context;
        userApi = ApiRepository.from(context).getUserApi();
    }

    public LiveData<User> getUserInfoById(final String id) {
        return userData;
    }

    public void update(final String id) {
        userApi.getUserById(id).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Fail with update", null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                userData.postValue(transformToUser(response.body()));
            }
        });
    }

    public void updateFriends(final String id) {
        userApi.getUserFriendsById(id).enqueue(new DatabaseCallback<List<String>>() {
            @Override
            void onNull(Response<List<String>> response) {
                userFriends.postValue(new ArrayList<>());
            }

            @Override
            void onSuccess(Response<List<String>> response) {
                userFriends.postValue(response.body());
            }
        });
    }

    public LiveData<List<String>> getUserFriendsById(String id) {
        return userFriends;
    }

    public void addFriend(String id, int num) {
        Log.d(LOG_TAG, "addFriend");
        String curUserId = FirebaseAuth.getInstance().getUid();
        userApi.addFriend(curUserId, Integer.toString(num), id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //updateFriends(curUserId);
                List<String> singleIdList = new ArrayList<>();
                singleIdList.add(id);
                userFriends.postValue(singleIdList);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                errorLog("Failed to add friend", t);
            }
        });
    }

    public void changeUserInformation(final String id, User newInformation) {
        userApi.changeUserInformation(id, transformToUserApiUser(newInformation)).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Failed with change information about" + id, null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                log("Change information about " + id);
            }
        });
    }

    public LiveData<Boolean> userExists(String id) {
        userApi.getUserById(id).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                userExistence.postValue(false);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                userExistence.postValue(true);
            }
        });
        return userExistence;
    }

    private void errorLog(final String message, Throwable t) {
        Log.e(LOG_TAG, message, t);
    }

    private void log(final String message) {
        Log.d(LOG_TAG, message);
    }

    private User transformToUser(UserApi.User user) {
        return new User(
                user.name,
                user.age,
                user.id
        );
    }

    private UserApi.User transformToUserApiUser(User user) {
        UserApi.User result = new UserApi.User();
        result.id = user.getId();
        result.name = user.getName();
        result.age = user.getAge();
        return result;
    }

    private abstract class DatabaseCallback<T> implements Callback<T> {

        abstract void onNull(Response<T> response);

        abstract void onSuccess(Response<T> response);

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.code() == FAILED_TO_READ_WRITE_DB_CODE) {
                errorLog("Problem with Auth", null);
                return;
            }
            if (response.body() == null) {
                errorLog("File not found", null);
                onNull(response);
                return;
            }
            if (response.isSuccessful()) {
                onSuccess(response);
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            errorLog("Failed to load", t);
        }
    }
}
