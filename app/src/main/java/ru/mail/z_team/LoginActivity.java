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

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements LifecycleOwner {

    private static final String TAG = "LoginActivity";
    Button signInBtn;
    EditText emailEt, passwordEt;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private ru.mail.z_team.AuthViewModel authViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "startOnCreate");
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        signInBtn = findViewById(R.id.sign_in);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
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
                    Log.d(TAG, "signInWithEmail:success");
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, message + " signed in successfully",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                    finish();
                } else if (authState == getString(R.string.FAILED)) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                } else if (authState == getString(R.string.ERROR)) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Invalid Email");
                } else {
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        authViewModel.loginUser(email, password);
    }
}
