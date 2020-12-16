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
import ru.mail.z_team.icon_fragments.Transformer;
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
                    ArrayList<Walk> walks = Transformer.transformToWalkAll(response.body());
                    currentUserWalks.postValue(walks);

                    localDatabase.databaseWriteExecutor.execute(() -> {
                        userDao.deleteAllWalksAndAddNew(Transformer.transformToLocalDBWalkAll(walks));
                    });
                }
            });
        } else {
            localDatabase.databaseWriteExecutor.execute(() -> {
                currentUserWalks.postValue(Transformer.transformToWalkAll(userDao.getUserWalks()));
            });
        }
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
