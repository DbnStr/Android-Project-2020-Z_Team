package ru.mail.z_team.icon_fragments.go_out;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class WalkRepository {

    private static final String LOG_TAG = "WalkRepository";
    UserApi userApi;
    FirebaseUser user;
    MutableLiveData<PostStatus> postStatus = new MutableLiveData<>();

    public WalkRepository(Context context){
        userApi = ApiRepository.from(context).getUserApi();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postWalk(String title) {
        Log.d(LOG_TAG, "postWalk");
        Date currentTime = new Date();
        final SimpleDateFormat sdf =
                new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        userApi.addWalk(user.getUid(), sdf.format(currentTime),
                new UserApi.Walk(title)).enqueue(new Callback<UserApi.User>() {
            @Override
            public void onResponse(Call<UserApi.User> call, Response<UserApi.User> response) {
                if (response.isSuccessful()){
                    postStatus.postValue(PostStatus.OK);
                }
                else {
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

    enum PostStatus {
        OK,
        FAILED
    }
}
