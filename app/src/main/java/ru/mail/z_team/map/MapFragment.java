package ru.mail.z_team.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
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
import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapFragment extends Fragment {

    private static final String LOG_TAG = "MapFragment";
    private final Logger logger;

    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String LAYER_BELOW_ID = "layer-below-id";
    private static final String DIRECTIONS_LAYER_ID = "directions-layer-id";
    private static final String SAVE_TAG = "save-walk";
    private static final String STORY_TAG = "story";

    private MapView mapView;
    private Point startPos = null, destinationPos = null;
    private final ArrayList<DirectionsRoute> routes = new ArrayList<>();
    private final ArrayList<Feature> walkList = new ArrayList<>();

    private FloatingActionButton saveMapButton;

    public MapFragment() {
        logger = new Logger(LOG_TAG, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        logger.log("onCreateView");

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.map_view);
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
                    onMapClicked(mapboxMap, point);
                }
                return true;
            });
            saveMapButton = view.findViewById(R.id.save_map_btn);
            saveMapButton.setOnClickListener(v -> {
                mapView.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.map_activity_container, new SavingWalkFragment(), SAVE_TAG)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            });
        }));

        return view;
    }

    private void onMapClicked(MapboxMap mapboxMap, LatLng point) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                Point storyPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                addStoryMarker(mapboxMap, point);

                Feature feature = Feature.fromGeometry(storyPoint);
                feature.addStringProperty("markerType", "storyMarker");

                walkList.add(feature);
                ((MapActivity) getActivity()).setWalkGeoJSON(FeatureCollection.fromFeatures(walkList));

                mapView.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.map_activity_container, new StoryFragment(storyPoint), STORY_TAG)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();

            });
        }
    }

    private void addStoryMarker(MapboxMap mapboxMap, LatLng point) {
        IconFactory iconFactory = IconFactory.getInstance(getActivity());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_photo_24);

        Icon icon = iconFactory.fromBitmap(bitmap);

        //TODO resolve problem with custom icon

        mapboxMap.addMarker(new MarkerOptions()
                .position(point)
                //.icon(icon)
                .title("story point"));
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
                .profile(DirectionsCriteria.PROFILE_WALKING)
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
                            walkList.add(walkRouteGeoJSON);
                            walkList.add(walkStartPointGeoJSON);
                            walkList.add(walkDestinationPointGeoJSON);
                            ((MapActivity) getActivity()).setWalkGeoJSON(FeatureCollection.fromFeatures(walkList));
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

    @Override
    public void onResume() {
        logger.log("onResume");
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        logger.log("onStart");
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        logger.log("onStop");
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        logger.log("onPause");
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        logger.log("onSaveInstance");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        logger.log("onDestroy");
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}