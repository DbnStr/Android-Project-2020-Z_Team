package ru.mail.z_team.map;

import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.List;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

public class MapActivity extends AppCompatActivity implements PermissionsListener {

    private static final String LOG_TAG = "MapActivity";
    private static final String MAP_TAG = "open map fragment";

    private Logger logger;

    private FeatureCollection walkGeoJSON = null;
    private final ArrayList<Feature> walkList = new ArrayList<>();
    private final ArrayList<Story> stories = new ArrayList<>();

    private PermissionsManager permissionsManager;
    private FragmentManager fragmentManager;

    private MapViewModel viewModel;

    private int container;

    public MapActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = new Logger(LOG_TAG, true);
        logger.log("OnCreate");

        setContentView(R.layout.activity_map);
        fragmentManager = getSupportFragmentManager();
        container = R.id.map_activity_container;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("MAP");
        actionBar.setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        permissionsManager = new PermissionsManager(this);

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            openMapFragment();
        } else {
            permissionsManager.requestLocationPermissions(this);
        }

        if (savedInstanceState != null){
            viewModel.getWalkList().observe(this, fc -> {
                walkList.clear();
                walkList.addAll(fc);
            });
            viewModel.getStories().observe(this, s -> {
                stories.clear();
                stories.addAll(s);
            });
        }
    }

    public void openMapFragment() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (fragmentManager.findFragmentById(container) == null) {
                fragmentManager
                        .beginTransaction()
                        .replace(container, new MapFragment(), MAP_TAG)
                        .commit();
            }
        }
        else {
            StyleableToast.makeText(this, "Turn on GPS", R.style.CustomToast).show();
        }
    }

    public void addStory(Story story) {
        stories.add(story);
    }

    public FeatureCollection getWalkGeoJSON() {
        walkGeoJSON = FeatureCollection.fromFeatures(walkList);
        return walkGeoJSON;
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void addToWalkGeoJSON(Feature walkPointGeoJSON) {
        logger.log("addToWalkGeoJSON ... " + walkList.size());
        walkList.add(walkPointGeoJSON);
        walkGeoJSON = FeatureCollection.fromFeatures(walkList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            openMapFragment();
        } else {
            Toast.makeText(this, "sth got wrong with location permission", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.setStories(stories);
        viewModel.setWalkList(walkList);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}