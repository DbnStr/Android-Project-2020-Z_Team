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

public class AuthRepo implements Executor{

    private static final String TAG = "AuthRepo";
    FirebaseAuth mAuth;

    public AuthRepo() {
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    public static AuthRepo getInstance(Context context) {
        return ApplicationModified.from(context).getAuthRepo();
    }

    private MutableLiveData<Pair<AuthProgress, String>> mAuthProgress;

    public LiveData<Pair<AuthProgress, String>> login(@NonNull String email, @NonNull String password) {
        Log.d(TAG, "loginUser");
        mAuthProgress = new MutableLiveData<>(new Pair<>(AuthProgress.IN_PROGRESS, ""));
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mAuthProgress.postValue(new Pair<>(AuthProgress.SUCCESS, mAuth.getCurrentUser().getEmail()));
                        } else {
                            // If sign in fails, display a message to the user.
                            mAuthProgress.postValue(new Pair<>(AuthProgress.FAILED, task.getException().toString()));
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mAuthProgress.postValue(new Pair<>(AuthProgress.ERROR, e.getMessage()));
                    }
                });
        return mAuthProgress;
    }

    public LiveData<Pair<AuthProgress, String>> register(@NonNull String email, @NonNull String password) {
        mAuthProgress = new MutableLiveData<>(new Pair<>(AuthProgress.IN_PROGRESS, ""));
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            mAuthProgress.postValue(new Pair<>(AuthProgress.SUCCESS, mAuth.getCurrentUser().getEmail()));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mAuthProgress.postValue(new Pair<>(AuthProgress.FAILED, ""));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                        mAuthProgress.postValue(new Pair<>(AuthProgress.ERROR, e.getMessage()));
                    }
                });
        return mAuthProgress;
    }

    enum AuthProgress {
        IN_PROGRESS,
        SUCCESS,
        FAILED,
        ERROR
    }

    @Override
    public void execute(Runnable command) {
        Log.d(TAG, "executing command ..." + command.toString());
        Thread t = new Thread(command);
        t.start();
    }
}
