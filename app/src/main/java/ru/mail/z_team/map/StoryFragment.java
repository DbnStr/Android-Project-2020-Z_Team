package ru.mail.z_team.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.geojson.Point;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

import static android.app.Activity.RESULT_OK;

public class StoryFragment extends Fragment {

    private final Point point;
    private TextView place;
    private EditText description;
    private Button addPhotoBtn, uploadBtn;
    private LinearLayout resourcesLayout;

    private static final String LOG_TAG = "StoryFragment";
    private final Logger logger;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int PICK_CAMERA_CODE = 300;
    private static final int PICK_GALLERY_CODE = 400;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri imageRui = null;

    public StoryFragment(Point storyPoint) {
        point = storyPoint;
        logger = new Logger(LOG_TAG, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        addPhotoBtn = view.findViewById(R.id.story_add_photo);
        uploadBtn = view.findViewById(R.id.story_upload);
        place = view.findViewById(R.id.story_place);
        description = view.findViewById(R.id.story_description);
        resourcesLayout = view.findViewById(R.id.story_resources);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //TODO geocode request to determine a place

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uploadBtn.setOnClickListener(v -> {
            logger.log("uploadBtn clicked");
            String descriptionText = description.getText().toString().trim();

            if (TextUtils.isEmpty(descriptionText)) {
                description.setError("Enter description");
                description.setFocusable(true);
                return;
            }

            Story story = new Story();
            story.setDescription(descriptionText);
            if (imageRui == null) {
                story.setRui("noImage");
            } else {
                story.setRui(String.valueOf(imageRui));
                story.setUri(imageRui);
            }

            ((MapActivity) getActivity()).addStory(story);

            //TODO there must be sth to close fragment
            getActivity().getSupportFragmentManager().popBackStack();
        });

        addPhotoBtn.setOnClickListener(v -> {
            showImagePickDialog();
        });
    }

    private void showImagePickDialog() {
        logger.log("showImagePickDialog");
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image from ...");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            }
            if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });

        builder.create().show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        logger.log("pickFromCamera");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "description");

        imageRui = getActivity().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageRui);

        startActivityForResult(intent, PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean res = ContextCompat.checkSelfPermission(getContext(),
                storagePermissions[0]) == (PackageManager.PERMISSION_GRANTED);
        return res;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        logger.log("checkCameraPermission");
        boolean res = ContextCompat.checkSelfPermission(getContext(),
                cameraPermissions[0]) == (PackageManager.PERMISSION_GRANTED) && checkStoragePermission();
        return res;
    }

    private void requestCameraPermission() {
        logger.log("requestCameraPermission");
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        logger.log("onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getActivity(), "Permissions are necessary", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getActivity(), "Permission is necessary", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        logger.log("onActivityResult");
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_GALLERY_CODE) {
                imageRui = data.getData();

                ImageView imageView = new ImageView(getContext());
                imageView.setImageURI(imageRui);

                resourcesLayout.addView(imageView);
            } else if (requestCode == PICK_CAMERA_CODE) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageURI(imageRui);

                resourcesLayout.addView(imageView);
            }

        }
        else {
            logger.errorLog("onActivityResult result code was not ok");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}