package ru.mail.z_team.icon_fragments.walks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private final Context context;

    private final UserApi userApi;

    private final UserDao userDao;
    private final LocalDatabase localDatabase;

    private final MutableLiveData<ArrayList<Walk>> currentUserWalks = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    public WalksRepository(Context context) {
        this.context = context;

        userApi = ApiRepository.from(context).getUserApi();

        logger = new Logger(LOG_TAG, true);

        localDatabase = ApplicationModified.from(context).getLocalDatabase();
        userDao = localDatabase.getUserDao();
    }

    public LiveData<ArrayList<Walk>> getCurrentUserWalks() {
        return currentUserWalks;
    }

    public void updateCurrentUserWalks() {
        String id = FirebaseAuth.getInstance().getUid();
        if (isOnline(context)) {
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
                        userDao.deleteAllWalksAndAddNew(transformToLocalDBWalkAll(walks));
                    });
                }
            });
        } else {
            localDatabase.databaseWriteExecutor.execute(() -> {
                currentUserWalks.postValue(transformToWalkAll(userDao.getUserWalks()));
            });
        }
    }

    private ArrayList<Walk> transformToWalkAll(List<ru.mail.z_team.local_storage.UserWalk> walks) {
        ArrayList<Walk> result = new ArrayList<>();
        for (ru.mail.z_team.local_storage.UserWalk walk : walks) {
            result.add(transformToWalk(walk));
        }
        return result;
    }

    private Walk transformToWalk(ru.mail.z_team.local_storage.UserWalk walk) {
        Walk result = new Walk();
        result.setAuthor(walk.author);
        result.setTitle(walk.title);
        result.setDate(getDate(walk.date));
        return result;
    }

    private Date getDate(String date) {
        Date result;
        try {
            result = sdf.parse(date);
        } catch (ParseException e) {
            result = new Date();
            e.printStackTrace();
        }
        return result;
    }
    private List<ru.mail.z_team.local_storage.Walk> transformToLocalDBWalkAll(List<Walk> walks) {
        List<ru.mail.z_team.local_storage.Walk> result = new ArrayList<>();
        for(Walk walk : walks) {
            result.add(transformToLocalDBWalk(walk));
        }
        return result;
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
        transformed.setDate(getDate(walk.date));
        return transformed;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
