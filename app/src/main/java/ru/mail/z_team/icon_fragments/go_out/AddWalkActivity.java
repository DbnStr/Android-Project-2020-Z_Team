package ru.mail.z_team.icon_fragments.go_out;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

public class AddWalkActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AddWalkActivity";
    private Logger logger;
    Button addWalk;
    EditText walkTitle;
    String title;
    GoOutViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_walk);

        logger = new Logger(LOG_TAG, true);

        addWalk = findViewById(R.id.add_walk_btn);
        walkTitle = findViewById(R.id.walk_title_et);

        addWalk.setOnClickListener(v -> {
            title = walkTitle.getText().toString().trim();
            if (title.equals("")) {
                walkTitle.setError("Title can't be empty");
                walkTitle.setFocusable(true);
            }
            else {
                postWalk();
            }
        });

        viewModel = new GoOutViewModel(getApplication());
    }

    private void postWalk() {
        logger.log("postWalk");
        viewModel.postWalk(title);
        viewModel.getPostStatus().observe(this, s -> {
            if (s == getString(R.string.SUCCESS)){
                Toast.makeText(this, "Posted successfully", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Failed post the walk", Toast.LENGTH_LONG).show();
            }
            finish();
        });
    }
}