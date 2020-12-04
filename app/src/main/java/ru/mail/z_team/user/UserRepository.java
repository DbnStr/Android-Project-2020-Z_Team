package ru.mail.z_team.user;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.ApplicationModified;
import ru.mail.z_team.icon_fragments.walks.Walk;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class UserRepository {

    private static final String LOG_TAG = "UserRepository";
    private static final int FAILED_TO_READ_WRITE_DB_CODE = 401;

    private final Context context;
    private final MutableLiveData<User> currentUserData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Walk>> currentUserWalks = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Walk>> currentUserNews = new MutableLiveData<>();
    private final MutableLiveData<User> otherUserData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> userExistence = new MutableLiveData<>();
    private final MutableLiveData<PostStatus> postStatus = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    private final UserApi userApi;
    private String currentUserName;

    private int count;

    public UserRepository(Context context) {
        this.context = context;
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        userApi = ApiRepository.from(context).getUserApi();
        String currentUserId = FirebaseAuth.getInstance().getUid();
        userApi.getUserById(currentUserId).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Fail with update", null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                currentUserName = response.body().name;
            }
        });
    }

    public LiveData<User> getCurrentUser() {
        return currentUserData;
    }

    public void updateCurrentUser() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        log("update user - " + currentUserId);

        userApi.getUserById(currentUserId).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Fail with update", null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
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

    private Friend transformToFriend(UserApi.Friend friend) {
        return new Friend(friend.name, friend.id);
    }

    public void addFriend(String id, int num) {
        log("addFriend");
        String curUserId = FirebaseAuth.getInstance().getUid();

        userApi.getUserById(id).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Failed to get " + id + " user", null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                UserApi.Friend friend = transformToUserApiFriend(response.body());
                if (response.body().friends == null){
                    count = 0;
                }
                else{
                    count = response.body().friends.size();
                }
                userApi.addFriend(curUserId, num, friend).enqueue(new DatabaseCallback<UserApi.Friend>() {
                    @Override
                    void onNull(Response<UserApi.Friend> response) {
                        errorLog("Failed to add friend " + id, null);
                    }

                    @Override
                    void onSuccess(Response<UserApi.Friend> response) {
                        updateCurrentUser();
                    }
                });
                userApi.addFriendId(curUserId, num, friend.id).enqueue(new DatabaseCallback<String>() {
                    @Override
                    void onNull(Response<String> response) {
                        log("failed to add friend id");
                    }

                    @Override
                    void onSuccess(Response<String> response) {
                        log("successfully add friend id");
                    }
                });
            }
        });
        userApi.getUserById(curUserId).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Failed to get " + curUserId + " user", null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                UserApi.Friend friend = transformToUserApiFriend(response.body());
                userApi.addFriend(id, count, friend).enqueue(new DatabaseCallback<UserApi.Friend>() {
                    @Override
                    void onNull(Response<UserApi.Friend> response) {
                        errorLog("Failed to add friend " + curUserId, null);
                    }

                    @Override
                    void onSuccess(Response<UserApi.Friend> response) {
                        updateCurrentUser();
                    }
                });
                userApi.addFriendId(id, count, friend.id).enqueue(new DatabaseCallback<String>() {
                    @Override
                    void onNull(Response<String> response) {
                        log("failed to add friend id");
                    }

                    @Override
                    void onSuccess(Response<String> response) {
                        log("successfully add friend id");
                    }
                });
            }
        });
    }

    private UserApi.Friend transformToUserApiFriend(UserApi.User user) {
        UserApi.Friend result = new UserApi.Friend();
        result.id = user.id;
        result.name = user.name;
        return result;
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

    private UserApi.User transformToUserApiUser(User user) {
        UserApi.User result = new UserApi.User();
        result.id = user.getId();
        result.name = user.getName();
        result.age = user.getAge();
        return result;
    }

    public void checkUserExistence(String id) {
        log("checkUserExistence");
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
        log("userExists");
        return userExistence;
    }

    public void postWalk(String title) {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        updateCurrentUserName();
        userApi.getUserWalksById(currentUserId).enqueue(new DatabaseCallback<List<UserApi.Walk>>() {
            @Override
            void onNull(Response<List<UserApi.Walk>> response) {
                addWalkInDb(0, title, currentUserId, currentUserName);
            }

            @Override
            void onSuccess(Response<List<UserApi.Walk>> response) {
                int count = response.body().size();
                addWalkInDb(count, title, currentUserId, currentUserName);
            }
        });
    }

    private void updateCurrentUserName() {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        log("update user - " + currentUserId);

        userApi.getUserById(currentUserId).enqueue(new DatabaseCallback<UserApi.User>() {
            @Override
            void onNull(Response<UserApi.User> response) {
                errorLog("Fail with update", null);
            }

            @Override
            void onSuccess(Response<UserApi.User> response) {
                currentUserName = response.body().name;
            }
        });
    }

    private void addWalkInDb(int currentWalkNumber, String title, String id, String name) {
        Log.d(LOG_TAG, "postWalk");
        Date currentTime = new Date();
        Log.d(LOG_TAG, "postWalk named  - " + name);
        userApi.addWalk(id, currentWalkNumber, new UserApi.Walk(title, sdf.format(currentTime), name)).enqueue(new Callback<UserApi.User>() {
            @Override
            public void onResponse(Call<UserApi.User> call, Response<UserApi.User> response) {
                if (response.isSuccessful()) {
                    postStatus.postValue(PostStatus.OK);
                } else {
                    postStatus.postValue(PostStatus.FAILED);
                }
            }

            @Override
            public void onFailure(Call<UserApi.User> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage(), null);
                postStatus.postValue(PostStatus.FAILED);
            }
        });
    }

    public void updateCurrentUserWalks() {
        String id = FirebaseAuth.getInstance().getUid();
        userApi.getUserWalksById(id).enqueue(new DatabaseCallback<List<UserApi.Walk>>() {
            @Override
            void onNull(Response<List<UserApi.Walk>> response) {
                currentUserWalks.postValue(new ArrayList<>());
                Log.d(LOG_TAG, "Walks was empty");
            }

            @Override
            void onSuccess(Response<List<UserApi.Walk>> response) {
                ArrayList<Walk> walks = new ArrayList<>();
                for (UserApi.Walk walk : response.body()) {
                    walks.add(transformToWalk(walk));
                }
                currentUserWalks.postValue(walks);
            }
        });
    }

    public void updateNews() {
        Log.d(LOG_TAG, "updateNews");
        String curId = FirebaseAuth.getInstance().getUid();
        userApi.getUserFriendsIds(curId).enqueue(new DatabaseCallback<ArrayList<String>>() {
            @Override
            void onNull(Response<ArrayList<String>> response) {
                currentUserNews.postValue(new ArrayList<>());
                Log.d(LOG_TAG, curId + " doesn't have friends");
            }

            @Override
            void onSuccess(Response<ArrayList<String>> response) {
                Log.d(LOG_TAG, curId + " have friends " + response.body().size());
                compileNews(response.body());
            }
        });
    }

    private void compileNews(ArrayList<String> ids) {
        ArrayList<Walk> news = new ArrayList<>();

        Log.d(LOG_TAG, "Compile news");
        for (String id : ids){
            Log.d(LOG_TAG, "Compile news... " + ids.indexOf(id));
            userApi.getUserWalksById(id).enqueue(new DatabaseCallback<List<UserApi.Walk>>() {
                @Override
                void onNull(Response<List<UserApi.Walk>> response) {
                    Log.d(LOG_TAG, id + " doesn't have walks");
                }

                @Override
                void onSuccess(Response<List<UserApi.Walk>> response) {
                    Log.d(LOG_TAG, id + " have walks");
                    for (UserApi.Walk walk : response.body()){
                        news.add(transformToWalk(walk));
                        currentUserNews.postValue(news);
                    }
                }
            });
        }
    }

    private Walk transformToWalk(UserApi.Walk walk) {
        Walk transformed = new Walk();
        transformed.setTitle(walk.title);
        transformed.setAuthor(walk.author);
        try {
            transformed.setDate(sdf.parse(walk.date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transformed;
    }

    public LiveData<ArrayList<Walk>> getNews() {
        Log.d(LOG_TAG, "get news " + currentUserNews.toString());
        return currentUserNews;
    }

    public MutableLiveData<PostStatus> getPostStatus() {return postStatus;}

    public LiveData<ArrayList<Walk>> getCurrentUserWalks() {
        return currentUserWalks;
    }

    public enum PostStatus {
        OK,
        FAILED
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

    @NonNull
    public static UserRepository getInstance(Context context) {
        return ApplicationModified.from(context).getUserRepository();
    }

    private void errorLog(final String message, Throwable t) {
        Log.e(LOG_TAG, message, t);
    }

    private void log(final String message) {
        Log.d(LOG_TAG, message);
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
