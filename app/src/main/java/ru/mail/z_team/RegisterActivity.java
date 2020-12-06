package ru.mail.z_team;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = "RegisterActivity";
    Button registerBtn;
    EditText emailEt, passwordEt;
    TextView toLogin;
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
        toLogin = findViewById(R.id.toLogin);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        authViewModel = new ViewModelProvider(this).get(ru.mail.z_team.AuthViewModel.class);
        authViewModel.getProgress().observe(this, stringStringPair -> {
            String authState = stringStringPair.first;
            String message = stringStringPair.second;
            if (authState == getString(R.string.ON_PROGRESS)) {
                progressDialog.show();
            } else if (authState == getString(R.string.SUCCESS)) {
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
        });

        registerBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEt.setError("Invalid Email");
                emailEt.setFocusable(true);
            } else if (password.length() < 6) {
                passwordEt.setError("Password must be at least 6 characters");
                passwordEt.setFocusable(true);
            } else {
                registerUser(email, password);
            }

        });
        toLogin.setOnClickListener(new TvListener());
    }

    private class TvListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.toLogin:
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                    break;
            }
        }
    }

    private void registerUser(String email, String password) {
        progressDialog.setMessage("Registration...");
        progressDialog.show();
        authViewModel.registerUser(email, password);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
