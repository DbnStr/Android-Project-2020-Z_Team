package ru.mail.z_team.icon_fragments.walks;

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

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.map.Story;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class WalkFragment extends Fragment {

    private WalkAnnotation walkAnnotation;
    private Walk walk;
    private MapView mapView;

    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String SYMBOL_SOURCE_ID = "symbol-source-id";
    private static final String LAYER_BELOW_ID = "layer-below-id";
    private static final String SYMBOL_LAYER_ID = "symbol-layer-id";
    private static final String DIRECTIONS_LAYER_ID = "directions-layer-id";

    private static final String STORY_TAG = "open story fragment";
    private static final String LOG_TAG = "WalkFragment";
    private Logger logger;

    private WalkProfileViewModel viewModel;

    public WalkFragment() { }

    public WalkFragment(WalkAnnotation walkAnnotation) {
        this.walkAnnotation = walkAnnotation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        logger = new Logger(LOG_TAG, true);

        logger.log("onCreateView");
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        View view = inflater.inflate(R.layout.fragment_walk, container, false);

        mapView = view.findViewById(R.id.walk_map_view);
        mapView.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WalkProfileViewModel.class);

        if (savedInstanceState != null) {
            viewModel.getAnnotation().observe(getActivity(), annotation -> {
                walkAnnotation = annotation;
                viewModel.updateCurrentDisplayedWalk(walkAnnotation);
                viewModel.getCurrentDisplayedWalk().observe(getActivity(), walk -> {
                    if (walk != null) {
                        this.walk = walk;
                        onMapCreated();
                    }
                });
            });
        }
        else {
            viewModel.updateCurrentDisplayedWalk(walkAnnotation);
            viewModel.getCurrentDisplayedWalk().observe(getActivity(), walk -> {
                if (walk != null) {
                    this.walk = walk;
                    onMapCreated();
                }
            });
        }
    }

    private void onMapCreated() {
        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

            ArrayList<Point> routePoints = new ArrayList<>();
            for (Feature feature : walk.getMap().features()) {
                if (feature.geometry() instanceof LineString) {
                    routePoints = (ArrayList<Point>) ((LineString) feature
                            .geometry()).coordinates();
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

            initLayers(style);
            showWalk(mapboxMap);
            addMarkers(mapboxMap);

            mapboxMap.addOnMapClickListener(point -> {
                final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
                List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, SYMBOL_LAYER_ID);

                if (features.size() > 0 && features.get(0).getStringProperty("markerType").equals("storyMarker")) {
                    logger.log("pin was clicked");
                    for (Story story : walk.getStories()) {
                        logger.log("ids: " + features.get(0).getStringProperty("id") +  " vs " + story.getId());
                        if (features.get(0).getStringProperty("id").equals(story.getId())){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.current_menu_container, new StoryFragment(story), STORY_TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        }
                    }
                }
                return false;
            });
        }));
    }

    private void addMarkers(MapboxMap mapboxMap) {
        mapboxMap.getStyle(style -> {
            GeoJsonSource source = style.getSourceAs(SYMBOL_SOURCE_ID);

            if (source != null) {
                source.setGeoJson(walk.getMap());
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
                new LineLayer(
                        DIRECTIONS_LAYER_ID, ROUTE_SOURCE_ID).withProperties(
                        lineWidth(4f),
                        lineColor(Color.BLACK)
                ), LAYER_BELOW_ID);

        loadedMapStyle.addLayerBelow(
                new SymbolLayer(SYMBOL_LAYER_ID, SYMBOL_SOURCE_ID)
                        .withProperties(iconAllowOverlap(true),
                                iconImage("red-image"),
                                iconAllowOverlap(true),
                                iconAnchor(Property.ICON_ANCHOR_BOTTOM)),
                LAYER_BELOW_ID);
    }

    private void showWalk(MapboxMap mapboxMap) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                if (source != null) {
                    source.setGeoJson(walk.getMap());
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
        if (viewModel != null) {
            viewModel.setAnnotation(walkAnnotation);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null){
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}