package ru.mail.z_team;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

@SuppressWarnings("WeakerAccess")
public class AuthViewModel extends AndroidViewModel implements Executor {

    private static final String TAG = "AuthViewModel";
    private final String none = getApplication().getResources().getString(R.string.NONE);
    private final String onProgress = getApplication().getResources().getString(R.string.ON_PROGRESS);
    private final String error = getApplication().getResources().getString(R.string.ERROR);
    private final String success = getApplication().getResources().getString(R.string.SUCCESS);
    private final String failed = getApplication().getResources().getString(R.string.FAILED);
    private final MediatorLiveData<Pair<String, String>> mAuthState = new MediatorLiveData<>();
    FirebaseAuth mAuth;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mAuthState.setValue(new Pair<>(none, ""));
    }

    public LiveData<Pair<String, String>> getProgress() {
        return mAuthState;
    }

    public void loginUser(String email, String password) {
        Log.d(TAG, "loginUser");
        mAuth = FirebaseAuth.getInstance();
        mAuthState.postValue(new Pair<>(onProgress, ""));
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mAuthState.postValue(new Pair<String, String>(success, mAuth.getCurrentUser().getEmail()));
                        } else {
                            // If sign in fails, display a message to the user.
                            mAuthState.postValue(new Pair<>(failed, task.getException().toString()));
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mAuthState.postValue(new Pair<>(error, e.getMessage()));
                    }
                });
    }

    public void registerUser(String email, String password) {
        Log.d(TAG, "registerUser");
        mAuth = FirebaseAuth.getInstance();
        mAuthState.postValue(new Pair<>(onProgress, ""));
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            mAuthState.postValue(new Pair<>(success, mAuth.getCurrentUser().getEmail()));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mAuthState.postValue(new Pair<>(failed, ""));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                        mAuthState.postValue(new Pair<>(error, e.getMessage()));
                    }
                });
    }


    @Override
    public void execute(Runnable command) {
        Log.d(TAG, "executing command ..." + command.toString());
        Thread t = new Thread(command);
        t.start();
    }
}
