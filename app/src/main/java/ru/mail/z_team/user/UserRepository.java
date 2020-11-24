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
    private final MutableLiveData<User> otherUserData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> userFriends = new MutableLiveData<>();
    private final MutableLiveData<Boolean> userExistence = new MutableLiveData<>();

    private final UserApi userApi;

    public UserRepository(Context context) {
        this.context = context;
        userApi = ApiRepository.from(context).getUserApi();
    }

    public LiveData<User> getUserInfo() {
        return userData;
    }

    public void update(final String id) {
        Log.d(LOG_TAG, "update");
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
        Log.d(LOG_TAG, "updateFriends");
        userApi.getUserFriendsById(id).enqueue(new DatabaseCallback<List<UserApi.Friend>>() {
            @Override
            void onNull(Response<List<UserApi.Friend>> response) {
                userFriends.postValue(new ArrayList<>());
            }

            @Override
            void onSuccess(Response<List<UserApi.Friend>> response) {
                List<User> res = new ArrayList<>();
                for (UserApi.Friend friendApi : response.body()){
                    res.add(friendTransformToUser(friendApi));
                }
                userFriends.postValue(null);
                userFriends.postValue(res);
            }
        });
    }

    public LiveData<List<User>> getUserFriendsById(String id) {
        Log.d(LOG_TAG, "getUserFriends");
        return userFriends;
    }

    public void addFriend(String id, int num) {
        Log.d(LOG_TAG, "addFriend");
        String curUserId = FirebaseAuth.getInstance().getUid();

        userApi.getUserById(id).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Failed to get " + id + " user", null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                User user = transformToUser(response.body());
                UserApi.Friend friend = transformToUserApiFriend(response.body());
                userApi.addFriend(curUserId, Integer.toString(num), friend).enqueue(new DatabaseCallback<UserApi.Friend>() {
                    @Override
                    void onNull(Response<UserApi.Friend> response) {
                        errorLog("Failed to add friend " + id, null);
                    }

                    @Override
                    void onSuccess(Response<UserApi.Friend> response) {
                        List<User> singleFriendList = new ArrayList<>();
                        singleFriendList.add(user);
                        userFriends.postValue(singleFriendList);
                    }
                });
            }
        });
    }

    public void changeUserInformation(final String id, User newInformation) {
        userApi.changeUserInformation(id, transformToUserApiUser(newInformation)).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Failed with change information about " + id, null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                log("Change information about " + id);
            }
        });
    }

    public void checkUserExistence(String id) {
        Log.d(LOG_TAG, "checkUserExistence");
        userApi.getUserById(id).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                Log.d(LOG_TAG, "posted false");
                userExistence.postValue(false);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                userExistence.postValue(true);
            }
        });
    }

    public LiveData<Boolean> userExists() {
        Log.d(LOG_TAG, "userExists");
        return userExistence;
    }

    private void errorLog(final String message, Throwable t) {
        Log.e(LOG_TAG, message, t);
    }

    private void log(final String message) {
        Log.d(LOG_TAG, message);
    }

    private User transformToUser(UserApi.User user) {
        String name = user.name;
        if (name == null){
            name = "";
        }
        return new User(
                name,
                user.age,
                user.id
        );
    }

    private User friendTransformToUser(UserApi.Friend user) {
        String name = user.name;
        if (name == null){
            name = "";
        }
        return new User(
                name,
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

    private UserApi.Friend transformToUserApiFriend(User user) {
        UserApi.Friend result = new UserApi.Friend();
        result.id = user.getId();
        result.name = user.getName();
        result.age = user.getAge();
        return result;
    }

    private UserApi.Friend transformToUserApiFriend(UserApi.User user) {
        UserApi.Friend result = new UserApi.Friend();
        result.id = user.id;
        result.name = user.name;
        result.age = user.age;
        result.email = user.email;
        return result;
    }

    public LiveData<User> getOtherUserInfo() {
        return otherUserData;
    }

    public void updateOtherUser(final String id) {
        Log.d(LOG_TAG, "updateOtherUser");
        userApi.getUserById(id).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Fail with update other user", null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                otherUserData.postValue(transformToUser(response.body()));
            }
        });
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
