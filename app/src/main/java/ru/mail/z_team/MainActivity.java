package ru.mail.z_team;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button authBtn, registerBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate: ");
        setContentView(R.layout.activity_main);

        authBtn = findViewById(R.id.auth_btn);
        registerBtn = findViewById(R.id.register_btn);
        loginBtn = findViewById(R.id.login_btn);

        registerBtn.setOnClickListener(new BtnOnClickListener());
        authBtn.setOnClickListener(new BtnOnClickListener());
        loginBtn.setOnClickListener(new BtnOnClickListener());
    }


    private class BtnOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.auth_btn):
                    startActivity(new Intent(MainActivity.this, FirebaseUIActivity.class));
                case(R.id.register_btn):
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                case (R.id.login_btn):
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }
    }
}