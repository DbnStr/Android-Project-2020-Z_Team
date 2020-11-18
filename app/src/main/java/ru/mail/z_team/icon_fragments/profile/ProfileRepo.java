package ru.mail.z_team.icon_fragments.profile;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.icon_fragments.profile.network.ApiRepo;
import ru.mail.z_team.icon_fragments.profile.network.UserApi;

public class ProfileRepo {

    private static final long ONE_MEGABYTE = 1024 * 1024;
    private final Context mContext;
    private static final MutableLiveData<String> data = new MutableLiveData<>();

    private final UserApi mUserApi;

    public ProfileRepo(Context context) {
        mContext = context;
        mUserApi = ApiRepo.from(mContext).getUserApi();
    }

    public LiveData<String> getUserInfoById(final String id) {
        return data;
    }

    public void update(final String id) {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageReference = storage.getReference();
//        StorageReference path = storageReference.child("Users/" + id + ".txt");
//        path.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                data.setValue(new String(bytes));
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.d("ProfileRepo", "FAIL");
//            }
//        });

        mUserApi.getUserById(1).enqueue(new Callback<UserApi.User>() {
            @Override
            public void onResponse(Call<UserApi.User> call,
                                   Response<UserApi.User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ProfileRepo", response.body().name);
                    data.setValue(response.body().name);
                }
            }

            @Override
            public void onFailure(Call<UserApi.User> call, Throwable t) {
                Log.d("ProfileRepo", "Failed to load", t);
            }
        });
    }
}
