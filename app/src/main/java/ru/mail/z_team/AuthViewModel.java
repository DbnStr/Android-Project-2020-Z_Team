package ru.mail.z_team;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

@SuppressWarnings("WeakerAccess")
public class AuthViewModel extends AndroidViewModel{

    private static final String TAG = "AuthViewModel";
    private final String none = getApplication().getResources().getString(R.string.NONE);
    private final String onProgress = getApplication().getResources().getString(R.string.ON_PROGRESS);
    private final String error = getApplication().getResources().getString(R.string.ERROR);
    private final String success = getApplication().getResources().getString(R.string.SUCCESS);
    private final String failed = getApplication().getResources().getString(R.string.FAILED);
    private final MediatorLiveData<Pair<String, String>> mAuthState = new MediatorLiveData<>();
    private final MediatorLiveData<String> restoreState = new MediatorLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mAuthState.setValue(new Pair<>(none, ""));
    }

    public LiveData<Pair<String, String>> getProgress() {
        return mAuthState;
    }

    public LiveData<String> getRestoreProgress() {
        return restoreState;
    }

    public void loginUser(String email, String password) {
        Log.d(TAG, "loginUser");
        mAuthState.postValue(new Pair<>(onProgress, ""));

        final LiveData<Pair<AuthRepo.AuthProgress, String>> progressLiveData = AuthRepo.getInstance(getApplication())
                .login(email, password);
        mAuthState.addSource(progressLiveData, authProgressStringPair -> {
            AuthRepo.AuthProgress authProgress = authProgressStringPair.first;
            if (authProgress == AuthRepo.AuthProgress.SUCCESS) {
                changeAuthState(progressLiveData, authProgressStringPair, success);
            } else if (authProgress == AuthRepo.AuthProgress.FAILED) {
                changeAuthState(progressLiveData, authProgressStringPair, failed);
            } else if (authProgress == AuthRepo.AuthProgress.ERROR) {
                changeAuthState(progressLiveData, authProgressStringPair, error);
            }
        });
    }

    public void registerUser(String email, String password) {
        Log.d(TAG, "registerUser");
        mAuthState.postValue(new Pair<>(onProgress, ""));
        final LiveData<Pair<AuthRepo.AuthProgress, String>> progressLiveData = AuthRepo.getInstance(getApplication())
                .register(email, password);
        mAuthState.addSource(progressLiveData, authProgressStringPair -> {
            AuthRepo.AuthProgress authProgress = authProgressStringPair.first;
            if (authProgress == AuthRepo.AuthProgress.SUCCESS) {
                changeAuthState(progressLiveData, authProgressStringPair, success);
            } else if (authProgress == AuthRepo.AuthProgress.FAILED) {
                changeAuthState(progressLiveData, authProgressStringPair, failed);
            } else if (authProgress == AuthRepo.AuthProgress.ERROR) {
                changeAuthState(progressLiveData, authProgressStringPair, error);
            }
        });
    }

    public void recoverPassword(String email) {
        final LiveData<AuthRepo.RestoreProgress> progressLiveData = AuthRepo.getInstance(getApplication())
                .restorePassword(email);
        restoreState.addSource(progressLiveData, restoreProgress -> {
            if (restoreProgress == AuthRepo.RestoreProgress.OK){
                restoreState.postValue(success);
            } else if (restoreProgress == AuthRepo.RestoreProgress.FAILED) {
                restoreState.postValue(failed);
            } else if (restoreProgress == AuthRepo.RestoreProgress.ERROR) {
                restoreState.postValue(error);
            }
        });
    }

    public void changeAuthState(LiveData<Pair<AuthRepo.AuthProgress, String>> progressLiveData,
                                Pair<AuthRepo.AuthProgress, String> authProgressStringPair,
                                String status) {
        mAuthState.postValue(new Pair<>(status, authProgressStringPair.second));
        mAuthState.removeSource(progressLiveData);
    }
}
