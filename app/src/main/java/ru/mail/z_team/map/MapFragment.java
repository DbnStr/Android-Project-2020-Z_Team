package ru.mail.z_team.map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.walks.StoryFragment;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapFragment extends Fragment {

    private static final String LOG_TAG = "MapFragment";
    private final Logger logger;

    private static final String SYMBOL_LAYER_ID = "symbol-layer-id";
    private static final String SYMBOL_SOURCE_ID = "symbol-source-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String LAYER_BELOW_ID = "layer-below-id";
    private static final String DIRECTIONS_LAYER_ID = "directions-layer-id";
    private static final String SAVE_TAG = "save-walk";
    private static final String STORY_TAG = "story";

    private LocationComponent locationComponent;
    private MapView mapView = null;
    private Point startPos = null, destinationPos = null;
    private final ArrayList<DirectionsRoute> routes = new ArrayList<>();

    private boolean isMapClickable;

    private FloatingActionButton saveMapButton, addStoryButton;

    private boolean isFeatureListEmpty = false;

    private MapViewModel viewModel;

    public MapFragment() {
        logger = new Logger(LOG_TAG, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        logger.log("onCreateView");

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        if (savedInstanceState != null) {
            restoreMapFragment();
        }

        return view;
    }

    public void restoreMapFragment() {
        viewModel.getStartPos().observe(getActivity(), point -> {
            startPos = point;
        });
        viewModel.getDestinationPos().observe(getActivity(), point -> {
            destinationPos = point;
        });
        viewModel.getIsClickable().observe(getActivity(), b -> {
            isMapClickable = b;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logger.log("onViewCreated");
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            initLocationComponent(mapboxMap, style);

            animateCamera(mapboxMap);

            initLayers(style);
            showWalk(mapboxMap);

            mapboxMap.addOnMapClickListener(point -> {
                if (startPos == null) {
                    startPos = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                    viewModel.setStartPos(startPos);
                    addMarker(mapboxMap, point);
                } else if (destinationPos == null) {
                    destinationPos = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                    viewModel.setDestinationPos(destinationPos);
                    addMarker(mapboxMap, point);
                    getRoute(mapboxMap, startPos, destinationPos);
                } else {
                    onMapClicked(mapboxMap, point);
                }
                return true;
            });

            addStoryButton = view.findViewById(R.id.add_story_btn);
            saveMapButton = view.findViewById(R.id.save_map_btn);
            addStoryButton.setOnClickListener(v -> {
                addStory(mapboxMap);
            });
            saveMapButton.setOnClickListener(v -> {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.map_activity_container, new SavingWalkFragment(), SAVE_TAG)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            });
        }));
    }

    private void animateCamera(MapboxMap mapboxMap) {
        isFeatureListEmpty = ((MapActivity) getActivity()).getWalkGeoJSON().features().isEmpty();
        if (!isFeatureListEmpty) {
            ArrayList<Point> routePoints = new ArrayList<>();
            for (Feature feature : ((MapActivity) getActivity()).getWalkGeoJSON().features()) {
                if (feature.geometry() instanceof LineString) {
                    routePoints.addAll(((LineString) feature
                            .geometry()).coordinates());
                } else if (feature.geometry() instanceof Point) {
                    routePoints.add((Point) feature.geometry());
                }
            }
            ArrayList<LatLng> routeLatLngs = new ArrayList<>();
            for (Point point : routePoints) {
                routeLatLngs.add(new LatLng(point.latitude(), point.longitude()));
            }
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .includes(routeLatLngs)
                    .build();
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));
        } else {
            LatLng target = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude());
            CameraPosition position = new CameraPosition.Builder()
                    .target(target)
                    .zoom(14)
                    .tilt(20)
                    .build();
            //new LatLng(55.765762, 37.685479)
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
        }
    }

    private void addStory(MapboxMap mapboxMap) {
        String[] options = {"My location", "Click on map"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose story location ...");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                LatLng point = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                        locationComponent.getLastKnownLocation().getLongitude());
                openStoryFragment(mapboxMap, point);
            }
            if (which == 1) {
                isMapClickable = true;
                viewModel.setIsClickable(true);
            }
        });

        builder.create().show();
    }

    @SuppressLint("MissingPermission")
    private void initLocationComponent(MapboxMap mapboxMap, Style style) {
        locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(getContext(), style).build());

        locationComponent.setLocationComponentEnabled(true);

        locationComponent.setRenderMode(RenderMode.COMPASS);
    }

    private void showWalk(MapboxMap mapboxMap) {
        logger.log("showWalk");
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                GeoJsonSource routeSource = style.getSourceAs(ROUTE_SOURCE_ID);
                if (routeSource != null) {
                    routeSource.setGeoJson(((MapActivity) getActivity())
                            .getWalkGeoJSON());
                }
                GeoJsonSource symbolSource = style.getSourceAs(SYMBOL_SOURCE_ID);
                if (symbolSource != null) {
                    symbolSource.setGeoJson(((MapActivity) getActivity())
                            .getWalkGeoJSON());
                }
            });
        }
    }

    public void openStoryFragment(MapboxMap mapboxMap, LatLng point) {
        mapboxMap.getStyle(style -> {
            Point storyPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.map_activity_container, new SaveStoryFragment(storyPoint), STORY_TAG)
                    .addToBackStack(null)
                    .commit();

        });
    }

    private void onMapClicked(MapboxMap mapboxMap, LatLng point) {
        if (mapboxMap != null) {
            final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
            List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, SYMBOL_LAYER_ID);

            if (features.size() > 0
                    && features.get(0).hasProperty("markerType")
                    && features.get(0).getStringProperty("markerType").equals("storyMarker")) {
                logger.log("pin was clicked");
                for (Story story : ((MapActivity) getActivity()).getStories()) {
                    logger.log("places: " + features.get(0).getStringProperty("placeName") + " vs " + story.getPlace());
                    if (features.get(0).getStringProperty("placeName").equals(story.getPlace())) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.map_activity_container, new StoryFragment(story), STORY_TAG)
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                }
            } else if (isMapClickable) {
                isMapClickable = false;
                viewModel.setIsClickable(false);
                openStoryFragment(mapboxMap, point);
            }
        }
    }

    private void addMarker(MapboxMap mapboxMap, LatLng point) {
        logger.log("addMarker");
        mapboxMap.getStyle(style -> {
            GeoJsonSource source = style.getSourceAs(SYMBOL_SOURCE_ID);

            if (source != null) {
                Feature walkPointGeoJSON = Feature.fromGeometry(Point.fromLngLat(point.getLongitude(),
                        point.getLatitude()));
                walkPointGeoJSON.addStringProperty("markerType", "routeMarker");
                ((MapActivity) getActivity()).addToWalkGeoJSON(walkPointGeoJSON);
                source.setGeoJson(((MapActivity) getActivity())
                        .getWalkGeoJSON());
            }
        });
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
        loadedMapStyle.addSource(new GeoJsonSource(SYMBOL_SOURCE_ID));

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_push_pin_24, null);
        Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
        loadedMapStyle.addImage("red-image", mBitmap);

        loadedMapStyle.addLayerBelow(
                new SymbolLayer(SYMBOL_LAYER_ID, SYMBOL_SOURCE_ID)
                        .withProperties(iconAllowOverlap(true),
                                iconImage("red-image")),
                LAYER_BELOW_ID);
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
                            ((MapActivity) getActivity()).addToWalkGeoJSON(walkRouteGeoJSON);
                            source.setGeoJson(((MapActivity) getActivity())
                                    .getWalkGeoJSON());
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
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        logger.log("onDestroy");
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}