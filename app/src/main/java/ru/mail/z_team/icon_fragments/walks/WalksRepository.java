package ru.mail.z_team.icon_fragments.walks;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class WalksRepository {

    private static final String LOG_TAG = "WalksRepository";

    private final UserApi userApi;

    private final MutableLiveData<ArrayList<Walk>> currentUserWalks = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    public WalksRepository(Context context) {
        userApi = ApiRepository.from(context).getUserApi();
    }

    public LiveData<ArrayList<Walk>> getCurrentUserWalks() {
        return currentUserWalks;
    }

    public void updateCurrentUserWalks() {
        String id = FirebaseAuth.getInstance().getUid();
        userApi.getUserWalksById(id).enqueue(new DatabaseCallback<List<UserApi.Walk>>(LOG_TAG) {
            @Override
            public void onNull(Response<List<UserApi.Walk>> response) {
                currentUserWalks.postValue(new ArrayList<>());
                Log.d(LOG_TAG, "Walks was empty");
            }

            @Override
            public void onSuccessResponse(Response<List<UserApi.Walk>> response) {
                ArrayList<Walk> walks = new ArrayList<>();
                for (UserApi.Walk walk : response.body()) {
                    walks.add(transformToWalk(walk));
                }
                currentUserWalks.postValue(walks);
            }
        });
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
}
