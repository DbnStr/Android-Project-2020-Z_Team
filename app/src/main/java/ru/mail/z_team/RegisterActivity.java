package ru.mail.z_team;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    Button registerBtn;
    EditText emailEt, passwordEt;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private ru.mail.z_team.AuthViewModel authViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        registerBtn = findViewById(R.id.register);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registration...");
        mAuth = FirebaseAuth.getInstance();

        authViewModel = new ViewModelProvider(this).get(ru.mail.z_team.AuthViewModel.class);
        authViewModel.getProgress().observe(this, new Observer<Pair<String, String>>() {
            @Override
            public void onChanged(Pair<String, String> stringStringPair) {
                String authState = stringStringPair.first;
                String message = stringStringPair.second;
                if (authState == getString(R.string.ON_PROGRESS)) {
                    progressDialog.show();
                } else if (authState == getString(R.string.SUCCESS)) {
                    Log.d(TAG, "createUserWithEmail:success");
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, message + " registered",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainMenuActivity.class));
                    finish();
                } else if (authState == getString(R.string.FAILED)) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Registration failed.",
                            Toast.LENGTH_SHORT).show();
                } else if (authState == getString(R.string.ERROR)) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString().trim();
                String password = emailEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Invalid Email");
                    emailEt.setFocusable(true);
                } else if (password.length() < 6) {
                    passwordEt.setError("Password must be at least 6 characters");
                    passwordEt.setFocusable(true);
                } else {
                    registerUser(email, password);
                }

            }
        });
    }

    private void registerUser(String email, String password) {
        authViewModel.registerUser(email, password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, user.getEmail() + " registered",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainMenuActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
