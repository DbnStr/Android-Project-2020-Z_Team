package ru.mail.z_team;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.network.ApiRepository;
import ru.mail.z_team.network.UserApi;

public class AuthRepo implements Executor{

    private static final String LOG_TAG = "AuthRepo";
    private static final int FAILED_WRITE_DB_CODE = 401;
    private static final int SUCCESS_CODE = 200;

    FirebaseAuth mAuth;

    private MutableLiveData<Pair<AuthProgress, String>> mAuthProgress;

    public AuthRepo() {
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    public static AuthRepo getInstance(Context context) {
        return ApplicationModified.from(context).getAuthRepo();
    }

    public LiveData<Pair<AuthProgress, String>> login(@NonNull String email, @NonNull String password) {
        log("loginUser");
        mAuthProgress = new MutableLiveData<>(new Pair<>(AuthProgress.IN_PROGRESS, ""));
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteAuthProgressListener())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorLog(e.getMessage(), null);
                        mAuthProgress.postValue(new Pair<>(AuthProgress.ERROR, e.getMessage()));
                    }
                });
        return mAuthProgress;
    }

    public LiveData<Pair<AuthProgress, String>> register(@NonNull String email, @NonNull String password) {
        mAuthProgress = new MutableLiveData<>(new Pair<>(AuthProgress.IN_PROGRESS, ""));
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteAuthProgressListener())
                .addOnFailureListener(e -> {
                    errorLog(e.getMessage(), null);
                    mAuthProgress.postValue(new Pair<>(AuthProgress.ERROR, e.getMessage()));
                });
        return mAuthProgress;
    }

    public void addNewUser(Context context) {
        String id = FirebaseAuth.getInstance().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ApiRepository.from(context).getUserApi().addUser(id, new UserApi.User(id, email))
                .enqueue(new Callback<UserApi.User>() {
            @Override
            public void onResponse(Call<UserApi.User> call, Response<UserApi.User> response) {
                switch (response.code()) {
                    case FAILED_WRITE_DB_CODE:
                        errorLog("Problem with Auth", null);
                        break;
                    case SUCCESS_CODE:
                        log("User was successfully added to database");
                        break;
                }
            }

            @Override
            public void onFailure(Call<UserApi.User> call, Throwable t) {
                errorLog("Fail with added player to database", t);
            }
        });
    }

    public LiveData<RestoreProgress> restorePassword(String email) {
        final MutableLiveData<RestoreProgress> mRestoreProgress = new MutableLiveData<>(RestoreProgress.IN_PROGRESS);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mRestoreProgress.postValue(RestoreProgress.OK);
                }
                else{
                    mRestoreProgress.postValue(RestoreProgress.FAILED);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mRestoreProgress.postValue(RestoreProgress.ERROR);
            }
        });
        return  mRestoreProgress;
    }

    enum AuthProgress {
        IN_PROGRESS,
        SUCCESS,
        FAILED,
        ERROR
    }

    enum RestoreProgress {
        IN_PROGRESS,
        OK,
        FAILED,
        ERROR
    }

    private class OnCompleteAuthProgressListener implements OnCompleteListener<AuthResult> {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                log("createUserWithEmail:success");
                mAuthProgress.postValue(new Pair<>(AuthProgress.SUCCESS, mAuth.getCurrentUser().getEmail()));
            } else {
                warningLog("createUserWithEmail:failure", task.getException());
                mAuthProgress.postValue(new Pair<>(AuthProgress.FAILED, ""));
            }
        }
    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }

    private void errorLog(String message, Throwable tr) {
        Log.e(LOG_TAG, message, tr);
    }

    private void warningLog(String message, Throwable tr) {

    }

    @Override
    public void execute(Runnable command) {
        log("executing command ..." + command.toString());
        Thread t = new Thread(command);
        t.start();
    }
}
