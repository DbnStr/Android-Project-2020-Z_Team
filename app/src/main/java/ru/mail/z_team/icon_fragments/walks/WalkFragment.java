package ru.mail.z_team.icon_fragments.walks;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class WalkFragment extends Fragment {

    private final Walk walk;
    private MapView mapView;

    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String LAYER_BELOW_ID = "layer-below-id";
    private static final String DIRECTIONS_LAYER_ID = "directions-layer-id";

    private static final String LOG_TAG = "WalkFragment";
    private final Logger logger;

    public WalkFragment(Walk walk) {
        this.walk = walk;
        logger = new Logger(LOG_TAG, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        logger.log("onCreateView");
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        View view = inflater.inflate(R.layout.fragment_walk, container, false);

        mapView = view.findViewById(R.id.walk_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(55.765762, 37.685479))
                    .zoom(14)
                    .tilt(20)
                    .build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);

            initLayers(style);
            showWalk(mapboxMap);
        }));
        return view;
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
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}