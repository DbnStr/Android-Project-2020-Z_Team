package ru.mail.z_team.icon_fragments.walks;

import com.mapbox.geojson.FeatureCollection;

public class Walk extends WalkAnnotation{
    FeatureCollection map;

    public void setMap(FeatureCollection map) {
        this.map = map;
    }

    public FeatureCollection getMap() {
        return map;
    }
}
