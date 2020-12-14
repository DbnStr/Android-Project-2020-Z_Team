package ru.mail.z_team.icon_fragments.walks;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Response;
import ru.mail.z_team.ApplicationModified;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.local_storage.LocalDatabase;
import ru.mail.z_team.local_storage.UserDao;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class WalksRepository {

    private static final String LOG_TAG = "WalksRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final UserDao userDao;
    private final LocalDatabase localDatabase;

    private final MutableLiveData<ArrayList<Walk>> currentUserWalks = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    public WalksRepository(Context context) {
        userApi = ApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
        userDao = ApplicationModified.from(context).getLocalDatabase().getUserDao();
        localDatabase = ApplicationModified.from(context).getLocalDatabase();
    }

    public LiveData<ArrayList<Walk>> getCurrentUserWalks() {
        return currentUserWalks;
    }

    public void updateCurrentUserWalks() {
        String id = FirebaseAuth.getInstance().getUid();
        userApi.getUserWalksById(id).enqueue(new DatabaseCallback<ArrayList<UserApi.Walk>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<UserApi.Walk>> response) {
                logger.log("Walks was empty");
                currentUserWalks.postValue(new ArrayList<>());
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<UserApi.Walk>> response) {
                logger.log("Successful update current user walks");
                ArrayList<Walk> walks = transformToWalkAll(response.body());
                currentUserWalks.postValue(walks);

                localDatabase.databaseWriteExecutor.execute(() -> {
                    for(Walk walk : walks) {
                        userDao.insert(transformToLocalDBWalk(walk));
                    }
                });
            }
        });
    }

    private ru.mail.z_team.local_storage.Walk transformToLocalDBWalk(Walk walk) {
        return new ru.mail.z_team.local_storage.Walk(
                walk.title,
                walk.author,
                walk.date.toString()
        );
    }

    private ArrayList<Walk> transformToWalkAll(ArrayList<UserApi.Walk> walks) {
        ArrayList<Walk> result = new ArrayList<>();
        for (UserApi.Walk walk : walks) {
            result.add(transformToWalk(walk));
        }
        return result;
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
