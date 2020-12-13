package ru.mail.z_team.map;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.mapbox.geojson.FeatureCollection;

import java.util.ArrayList;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

public class MapActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MapActivity";
    private static final String MAP_TAG = "open map fragment";
    private Logger logger;

    private FeatureCollection walkGeoJSON = null;
    private final ArrayList<Story> stories = new ArrayList<>();

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

        if (fragmentManager.findFragmentById(container) == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(container, new MapFragment(), MAP_TAG)
                    .commit();
        }
    }

    public void setWalkGeoJSON(FeatureCollection walkGeoJSON) {
        this.walkGeoJSON = walkGeoJSON;
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
}