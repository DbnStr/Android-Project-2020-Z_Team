package ru.mail.z_team.icon_fragments.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.HashMap;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.user.User;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

public class ProfileFragment extends Fragment {

    private static final String LOG_TAG = "ProfileFragment";

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int PICK_CAMERA_CODE = 300;
    private static final int PICK_GALLERY_CODE = 400;

    private static final CharSequence PLAIN_TEXT = "plain-text";

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Logger logger;

    private ProfileViewModel profileViewModel;
    private EditText name, age;
    private TextView idTextView;
    private ImageView image;
    private Button editAndSaveBtn, copyIdBtn;
    private User user;

    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger = new Logger(LOG_TAG, true);
        logger.log("OnCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.profile_name);
        age = view.findViewById(R.id.profile_age);
        image = view.findViewById(R.id.profile_image);
        editAndSaveBtn = view.findViewById(R.id.edit_btn);
        copyIdBtn = view.findViewById(R.id.profile_copy_id);
        idTextView = view.findViewById(R.id.profile_id);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        changeToStableMode();

        Observer<User> observer = user -> {
            if (user != null) {
                this.user = user;
                setProfileData(user);
            }
        };
        profileViewModel = new ViewModelProvider(getActivity())
                .get(ProfileViewModel.class);
        profileViewModel.updateCurrentUser();
        profileViewModel
                .getCurrentUser()
                .observe(getViewLifecycleOwner(), observer);
    }

    private void disableEditAbility(EditText editText) {
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void changeToStableMode() {
        disableEditAbility(name);
        disableEditAbility(age);

        image.setClickable(false);

        editAndSaveBtn.setText(getString(R.string.edit));
        editAndSaveBtn.setOnClickListener(v -> changeToEditMode());
    }

    private void changeToEditMode() {
        enableEditAbility(name);
        enableEditAbility(age);

        image.setClickable(true);
        image.setOnClickListener(v -> showImagePickDialog());

        editAndSaveBtn.setText(getString(R.string.save_changes));
        editAndSaveBtn.setOnClickListener(v -> {
            profileViewModel.changeCurrentUserInformation(getProfileInfo());
            changeToStableMode();
        });
    }

    private void enableEditAbility(EditText editText) {
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setBackgroundColor(Color.LTGRAY);
    }

    private HashMap<String, String> getProfileInfo() {
        HashMap<String, String> info = new HashMap<>();
        String nameText = name.getText().toString();
        String ageText = age.getText().toString();
        info.put("age", ageText);
        info.put("name", nameText);

        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("ProfileImages/" + user.id + "/" + imageUri.getLastPathSegment());
            storageReference.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    logger.log("successfully added the image");
                } else {
                    logger.errorLog("error in adding the image");
                }
            });

            info.put("imageUrl", storageReference.getPath());
        }

        return info;
    }

    private void initCopyBtn() {
        copyIdBtn.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                    .getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(PLAIN_TEXT, idTextView.getText());
            clipboardManager.setPrimaryClip(clipData);
            StyleableToast.makeText(getContext(), "Text is сopied to сlipboard",
                    R.style.CustomToast).show();
        });
    }

    private void setProfileData(@NonNull User user) {
        name.setText(user.getName());
        age.setText(String.valueOf(user.getAge()));
        idTextView.setText(user.getId());

        initCopyBtn();

        if (user.bitmap != null) {
            image.setImageBitmap(user.bitmap);
        }
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

        imageUri = getActivity().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getContext(),
                storagePermissions[0]) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(getContext(),
                cameraPermissions[0]) == (PackageManager.PERMISSION_GRANTED) && checkStoragePermission();
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
                        StyleableToast.makeText(getActivity(), "Permission is necessary", R.style.CustomToast).show();
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
                imageUri = data.getData();
                image.setImageURI(imageUri);
            } else if (requestCode == PICK_CAMERA_CODE) {
                image.setImageURI(imageUri);
            }

        } else {
            logger.errorLog("onActivityResult result code was not ok");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}