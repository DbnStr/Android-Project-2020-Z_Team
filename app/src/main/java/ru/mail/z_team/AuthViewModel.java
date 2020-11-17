package ru.mail.z_team;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

@SuppressWarnings("WeakerAccess")
public class AuthViewModel extends AndroidViewModel{

    private static final String TAG = "AuthViewModel";
    private final String none = getApplication().getResources().getString(R.string.NONE);
    private final String onProgress = getApplication().getResources().getString(R.string.ON_PROGRESS);
    private final String error = getApplication().getResources().getString(R.string.ERROR);
    private final String success = getApplication().getResources().getString(R.string.SUCCESS);
    private final String failed = getApplication().getResources().getString(R.string.FAILED);
    private final MediatorLiveData<Pair<String, String>> mAuthState = new MediatorLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mAuthState.setValue(new Pair<>(none, ""));
    }

    public LiveData<Pair<String, String>> getProgress() {
        return mAuthState;
    }

    public void loginUser(String email, String password) {
        Log.d(TAG, "loginUser");
        mAuthState.postValue(new Pair<>(onProgress, ""));

        final LiveData<Pair<AuthRepo.AuthProgress, String>> progressLiveData = AuthRepo.getInstance(getApplication())
                .login(email, password);
        mAuthState.addSource(progressLiveData, new Observer<Pair<AuthRepo.AuthProgress, String>>() {
            @Override
            public void onChanged(Pair<AuthRepo.AuthProgress, String> authProgressStringPair) {
                AuthRepo.AuthProgress authProgress = authProgressStringPair.first;
                if (authProgress == AuthRepo.AuthProgress.SUCCESS) {
                    mAuthState.postValue(new Pair<>(success, authProgressStringPair.second));
                    mAuthState.removeSource(progressLiveData);
                } else if (authProgress == AuthRepo.AuthProgress.FAILED) {
                    mAuthState.postValue(new Pair<>(failed, authProgressStringPair.second));
                    mAuthState.removeSource(progressLiveData);
                }
                else if (authProgress == AuthRepo.AuthProgress.ERROR) {
                    mAuthState.postValue(new Pair<>(error, authProgressStringPair.second));
                    mAuthState.removeSource(progressLiveData);
                }
            }
        });
    }

    public void registerUser(String email, String password) {
        Log.d(TAG, "registerUser");
        mAuthState.postValue(new Pair<>(onProgress, ""));

        final LiveData<Pair<AuthRepo.AuthProgress, String>> progressLiveData = AuthRepo.getInstance(getApplication())
                .register(email, password);
        mAuthState.addSource(progressLiveData, new Observer<Pair<AuthRepo.AuthProgress, String>>() {
            @Override
            public void onChanged(Pair<AuthRepo.AuthProgress, String> authProgressStringPair) {
                AuthRepo.AuthProgress authProgress = authProgressStringPair.first;
                if (authProgress == AuthRepo.AuthProgress.SUCCESS) {
                    mAuthState.postValue(new Pair<>(success, authProgressStringPair.second));
                    mAuthState.removeSource(progressLiveData);
                } else if (authProgress == AuthRepo.AuthProgress.FAILED) {
                    mAuthState.postValue(new Pair<>(failed, authProgressStringPair.second));
                    mAuthState.removeSource(progressLiveData);
                }
                else if (authProgress == AuthRepo.AuthProgress.ERROR) {
                    mAuthState.postValue(new Pair<>(error, authProgressStringPair.second));
                    mAuthState.removeSource(progressLiveData);
                }
            }
        });
    }
}
