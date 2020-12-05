package ru.mail.z_team;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

@SuppressWarnings("WeakerAccess")
public class AuthViewModel extends AndroidViewModel{

    private static final String LOG_TAG = "AuthViewModel";
    private final Logger logger;

    private final String none = getApplication().getResources().getString(R.string.NONE);
    private final String onProgress = getApplication().getResources().getString(R.string.ON_PROGRESS);
    private final String error = getApplication().getResources().getString(R.string.ERROR);
    private final String success = getApplication().getResources().getString(R.string.SUCCESS);
    private final String failed = getApplication().getResources().getString(R.string.FAILED);
    private final MediatorLiveData<Pair<String, String>> mAuthState = new MediatorLiveData<>();
    private final MediatorLiveData<String> restoreState = new MediatorLiveData<>();
    private final AuthRepository authRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mAuthState.setValue(new Pair<>(none, ""));
        authRepository = AuthRepository.getInstance(getApplication());
        logger = new Logger(LOG_TAG, true);
    }

    public LiveData<Pair<String, String>> getProgress() {
        return mAuthState;
    }

    public LiveData<String> getRestoreProgress() {
        return restoreState;
    }

    public void loginUser(String email, String password) {
        logger.log("loginUser");
        mAuthState.postValue(new Pair<>(onProgress, ""));

        final LiveData<Pair<AuthRepository.AuthProgress, String>> progressLiveData = authRepository.login(email, password);
        mAuthState.addSource(progressLiveData, authProgressStringPair -> {
            AuthRepository.AuthProgress authProgress = authProgressStringPair.first;
            if (authProgress == AuthRepository.AuthProgress.SUCCESS) {
                changeAuthState(progressLiveData, authProgressStringPair, success);
            } else if (authProgress == AuthRepository.AuthProgress.FAILED) {
                changeAuthState(progressLiveData, authProgressStringPair, failed);
            } else if (authProgress == AuthRepository.AuthProgress.ERROR) {
                changeAuthState(progressLiveData, authProgressStringPair, error);
            }
        });
    }

    public void registerUser(String email, String password) {
        logger.log("registerUser");
        mAuthState.postValue(new Pair<>(onProgress, ""));
        final LiveData<Pair<AuthRepository.AuthProgress, String>> progressLiveData = authRepository.register(email, password);
        mAuthState.addSource(progressLiveData, authProgressStringPair -> {
            AuthRepository.AuthProgress authProgress = authProgressStringPair.first;
            if (authProgress == AuthRepository.AuthProgress.SUCCESS) {
                authRepository.addNewUser();
                changeAuthState(progressLiveData, authProgressStringPair, success);
            } else if (authProgress == AuthRepository.AuthProgress.FAILED) {
                changeAuthState(progressLiveData, authProgressStringPair, failed);
            } else if (authProgress == AuthRepository.AuthProgress.ERROR) {
                changeAuthState(progressLiveData, authProgressStringPair, error);
            }
        });
    }

    public void recoverPassword(String email) {
        final LiveData<AuthRepository.RestoreProgress> progressLiveData = authRepository.restorePassword(email);
        restoreState.addSource(progressLiveData, restoreProgress -> {
            if (restoreProgress == AuthRepository.RestoreProgress.OK){
                restoreState.postValue(success);
            } else if (restoreProgress == AuthRepository.RestoreProgress.FAILED) {
                restoreState.postValue(failed);
            } else if (restoreProgress == AuthRepository.RestoreProgress.ERROR) {
                restoreState.postValue(error);
            }
        });
    }

    public void changeAuthState(LiveData<Pair<AuthRepository.AuthProgress, String>> progressLiveData,
                                Pair<AuthRepository.AuthProgress, String> authProgressStringPair,
                                String status) {
        mAuthState.postValue(new Pair<>(status, authProgressStringPair.second));
        mAuthState.removeSource(progressLiveData);
    }
}
