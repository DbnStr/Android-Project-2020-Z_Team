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
    private final String NONE = getApplication().getResources().getString(R.string.NONE);
    private final String ON_PROGRESS = getApplication().getResources().getString(R.string.ON_PROGRESS);
    private final String ERROR = getApplication().getResources().getString(R.string.ERROR);
    private final String SUCCESS = getApplication().getResources().getString(R.string.SUCCESS);
    private final String FAILED = getApplication().getResources().getString(R.string.FAILED);
    private final MediatorLiveData<Pair<String, String>> mAuthState = new MediatorLiveData<>();
    FirebaseAuth mAuth;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mAuthState.setValue(new Pair<>(NONE, ""));
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Pair<String, String>> getProgress() {
        return mAuthState;
    }

    public void loginUser(String email, String password) {
        mAuthState.postValue(new Pair<>(ON_PROGRESS, ""));
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mAuthState.postValue(new Pair<String, String>(SUCCESS, mAuth.getCurrentUser().getEmail()));
                        } else {
                            // If sign in fails, display a message to the user.
                            mAuthState.postValue(new Pair<>(FAILED, task.getException().toString()));
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mAuthState.postValue(new Pair<>(ERROR, e.getMessage()));
                    }
                });
    }

    public void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            mAuthState.postValue(new Pair<>(SUCCESS, mAuth.getCurrentUser().getEmail()));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mAuthState.postValue(new Pair<>(FAILED, ""));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mAuthState.postValue(new Pair<>(ERROR, e.getMessage()));
                    }
                });
    }


    @Override
    public void execute(Runnable command) {
        Thread t = new Thread(command);
        t.start();
    }
}
