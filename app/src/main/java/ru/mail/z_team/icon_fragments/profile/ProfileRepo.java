package ru.mail.z_team.icon_fragments.profile;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileRepo {

    private static final long ONE_MEGABYTE = 1024 * 1024;
    private final Context mContext;
    private static final MutableLiveData<String> data = new MutableLiveData<>();

    public ProfileRepo(Context context) {
        mContext = context;
    }

    public LiveData<String> getUserInfoById(final String id) {
        return data;
    }

    public void update(final String id) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference path = storageReference.child("Users/" + id + ".txt");
        path.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                data.setValue(new String(bytes));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("ProfileRepo", "FAIL");
            }
        });
    }
}
