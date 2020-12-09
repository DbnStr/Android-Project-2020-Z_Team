package ru.mail.z_team.icon_fragments.walks;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class WalksRepository {

    private static final String LOG_TAG = "WalksRepository";
    private final Logger logger;

    private final UserApi userApi;

    private final MutableLiveData<ArrayList<WalkAnnotation>> currentUserWalks = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    public WalksRepository(Context context) {
        userApi = ApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
    }

    public LiveData<ArrayList<WalkAnnotation>> getCurrentUserWalks() {
        return currentUserWalks;
    }

    public void updateCurrentUserWalks() {
        String id = FirebaseAuth.getInstance().getUid();
        userApi.getUserWalksById(id).enqueue(new DatabaseCallback<ArrayList<UserApi.WalkInfo>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<UserApi.WalkInfo>> response) {
                logger.log("Walks was empty");
                currentUserWalks.postValue(new ArrayList<>());
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<UserApi.WalkInfo>> response) {
                logger.log("Successful update current user walks");
                currentUserWalks.postValue(transformToWalkAnnotationAll(response.body()));
            }
        });
    }

    private ArrayList<WalkAnnotation> transformToWalkAnnotationAll(ArrayList<UserApi.WalkInfo> walks) {
        ArrayList<WalkAnnotation> result = new ArrayList<>();
        for (UserApi.WalkInfo walk : walks) {
            result.add(transformToWalkAnnotation(walk));
        }
        return result;
    }

    private WalkAnnotation transformToWalkAnnotation(UserApi.WalkInfo walk) {
        WalkAnnotation transformed = new WalkAnnotation();
        transformed.setTitle(walk.title);
        transformed.setAuthor(walk.author);
        transformed.setAuthorId(walk.id);
        try {
            transformed.setDate(sdf.parse(walk.date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transformed;
    }
}
