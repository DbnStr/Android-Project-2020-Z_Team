package ru.mail.z_team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    Button registerBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerBtn = findViewById(R.id.register_btn);
        loginBtn = findViewById(R.id.login_btn);

        registerBtn.setOnClickListener(new BtnOnClickListener());
        loginBtn.setOnClickListener(new BtnOnClickListener());
    }


    private class BtnOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case(R.id.register_btn):
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                    break;
                case (R.id.login_btn):
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    break;
            }
        }
    }
}