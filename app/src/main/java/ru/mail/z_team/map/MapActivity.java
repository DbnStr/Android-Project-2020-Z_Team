package ru.mail.z_team.map;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

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

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            openMapFragment();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    public void openMapFragment() {
        if (fragmentManager.findFragmentById(container) == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(container, new MapFragment(), MAP_TAG)
                    .commit();
        }
    }

    public void addStory(Story story) {
        stories.add(story);
    }

    public FeatureCollection getWalkGeoJSON() {
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
}