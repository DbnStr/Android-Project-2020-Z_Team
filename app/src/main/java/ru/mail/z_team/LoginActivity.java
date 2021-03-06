package ru.mail.z_team;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class LoginActivity extends AppCompatActivity implements LifecycleOwner {

    private static final String LOG_TAG = "LoginActivity";
    private Logger logger;

    Button signInBtn;
    TextView toRegistration, recoverPassword;
    TextInputLayout emailEt, passwordEt;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private ru.mail.z_team.AuthViewModel authViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = new Logger(LOG_TAG, true);
        logger.log("startOnCreate");
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);

        signInBtn = findViewById(R.id.sign_in);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        toRegistration = findViewById(R.id.toRegistration);
        recoverPassword = findViewById(R.id.forgotPasswordTv);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        mAuth = FirebaseAuth.getInstance();
        authViewModel = new ViewModelProvider(this).get(ru.mail.z_team.AuthViewModel.class);
        authViewModel.getRestoreProgress().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == getString(R.string.SUCCESS)){
                    progressDialog.dismiss();
                }
                else if (s == getString(R.string.FAILED)){
                    StyleableToast.makeText(LoginActivity.this, "Failed to sent email:(",
                            R.style.CustomToast).show();
                }
                else if (s == getString(R.string.ERROR)){
                    StyleableToast.makeText(LoginActivity.this, "ERROR",
                            R.style.CustomToast).show();
                }
            }
        });
        authViewModel.getProgress().observe(this, new Observer<Pair<String, String>>() {
            @Override
            public void onChanged(Pair<String, String> stringStringPair) {
                String authState = stringStringPair.first;
                String message = stringStringPair.second;
                if (authState == getString(R.string.ON_PROGRESS)) {
                    progressDialog.show();
                } else if (authState == getString(R.string.SUCCESS)) {
                    logger.log("signInWithEmail:success");
                    progressDialog.dismiss();
                    StyleableToast.makeText(LoginActivity.this, message + " signed in successfully",
                            R.style.CustomToast).show();
                    startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                    finish();
                } else if (authState == getString(R.string.FAILED)) {
                    progressDialog.dismiss();
                    StyleableToast.makeText(LoginActivity.this, "Authentication failed.",
                            R.style.CustomToast).show();
                } else if (authState == getString(R.string.ERROR)) {
                    progressDialog.dismiss();
                    StyleableToast.makeText(LoginActivity.this, message, R.style.CustomToast).show();
                }
            }
        });

        toRegistration.setOnClickListener(new TvListener());
        recoverPassword.setOnClickListener(new TvListener());
        signInBtn.setOnClickListener(v -> {
            String email = emailEt.getEditText().getText().toString().trim();
            String password = passwordEt.getEditText().getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEt.setError("Invalid Email");
            } else {
                loginUser(email, password);
            }
        });
    }

    private class TvListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.toRegistration:
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    finish();
                    break;
                case R.id.forgotPasswordTv:
                    showRecoverPasswordDialog();
                    break;
            }
        }
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEt.getText().toString().trim();
                recoverPassword(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void recoverPassword(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        authViewModel.recoverPassword(email);
    }

    private void loginUser(String email, String password) {
        logger.log("loginUser");
        authViewModel.loginUser(email, password);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
