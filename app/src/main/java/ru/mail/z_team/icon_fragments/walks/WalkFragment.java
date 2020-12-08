package ru.mail.z_team.icon_fragments.walks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;

import ru.mail.z_team.R;

public class WalkFragment extends Fragment {

    private final Walk walk;
    private MapView mapView;

    public WalkFragment(Walk walk) {
        this.walk = walk;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        }));
        return view;
    }
}