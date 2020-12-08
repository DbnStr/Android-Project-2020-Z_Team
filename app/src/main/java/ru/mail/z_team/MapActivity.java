package ru.mail.z_team;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.icon_fragments.go_out.SavingWalkFragment;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MapActivity";
    private Logger logger;

    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String LAYER_BELOW_ID = "layer-below-id";
    private static final String DIRECTIONS_LAYER_ID = "directions-layer-id";
    private static final String SAVE_TAG = "save-walk";

    private MapView mapView;
    private FeatureCollection walkGeoJSON;
    private Point startPos = null, destinationPos = null;
    private final ArrayList<DirectionsRoute> routes = new ArrayList<>();

    private FloatingActionButton saveMapButton;

    public MapActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = new Logger(LOG_TAG, true);
        logger.log("OnCreate");

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(55.765762, 37.685479))
                    .zoom(14)
                    .tilt(20)
                    .build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
            initLayers(style);
            mapboxMap.addOnMapClickListener(point -> {
                if (startPos == null) {
                    startPos = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                    addMarker(mapboxMap, point);
                } else if (destinationPos == null) {
                    destinationPos = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                    addMarker(mapboxMap, point);
                    getRoute(mapboxMap, startPos, destinationPos);
                } else {
                    Toast.makeText(getApplication(), "You have chosen two points", Toast.LENGTH_LONG).show();
                }
                return true;
            });
            saveMapButton = findViewById(R.id.save_map_btn);
            saveMapButton.setOnClickListener(v -> {
                mapView.setVisibility(View.GONE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.map_container, new SavingWalkFragment(), SAVE_TAG)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            });
        }));
    }

    private void addMarker(MapboxMap mapboxMap, LatLng point) {
        mapboxMap.addMarker(new MarkerOptions()
                .position(point)
                .title("new point"));
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
        loadedMapStyle.addLayerBelow(
                new LineLayer(
                        DIRECTIONS_LAYER_ID, ROUTE_SOURCE_ID).withProperties(
                        lineWidth(4f),
                        lineColor(Color.BLACK)
                ), LAYER_BELOW_ID);
    }

    private void getRoute(MapboxMap mapboxMap, Point startPos, Point destinationPos) {
        MapboxDirections client = MapboxDirections.builder()
                .origin(startPos)
                .destination(destinationPos)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    logger.log("No routes found, make sure you set the right user and access token.");
                } else if (response.body().routes().size() == 0) {
                    logger.log("No routes found");
                } else {
                    routes.addAll(response.body().routes());
                }

                if (mapboxMap != null) {
                    mapboxMap.getStyle(style -> {
                        GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                        if (source != null) {
                            logger.log("routes count = " + routes.size());
                            Feature walkRouteGeoJSON = Feature.fromGeometry(LineString.fromPolyline(routes.get(0).geometry(), PRECISION_6));
                            Feature walkStartPointGeoJSON = Feature.fromGeometry(startPos);
                            Feature walkDestinationPointGeoJSON = Feature.fromGeometry(destinationPos);
                            ArrayList<Feature> walkList = new ArrayList<>();
                            walkList.add(walkRouteGeoJSON);
                            walkList.add(walkStartPointGeoJSON);
                            walkList.add(walkDestinationPointGeoJSON);
                            walkGeoJSON = FeatureCollection.fromFeatures(walkList);
                            source.setGeoJson(LineString.fromPolyline(routes.get(0).geometry(), PRECISION_6));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                logger.errorLog(t.getMessage());
            }
        });
    }

    public FeatureCollection getWalkGeoJSON() {
        return walkGeoJSON;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}