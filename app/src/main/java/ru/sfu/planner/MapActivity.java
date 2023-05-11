package ru.sfu.planner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import java.util.ArrayList;
import java.util.Arrays;

public class MapActivity extends AppCompatActivity{
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private Marker marker = null;
    private GeoPoint geoPoint = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx,
                PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);


        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        String[] s = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissionsIfNecessary(s);

        map.setVerticalMapRepetitionEnabled(false);
        map.setScrollableAreaLimitLatitude(
                MapView.getTileSystem().getMaxLatitude(),
                MapView.getTileSystem().getMinLatitude(),
                0
        );
        map.getController().setZoom(12.0);
        map.getController().setCenter(new GeoPoint(56.0184, 92.8672));

        map.getOverlays().add(touchOverlay);
    }

    Overlay touchOverlay = new Overlay(){
        @Override
        public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
            Projection proj = mapView.getProjection();
            geoPoint = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
            System.out.println("- Longitude = " + geoPoint.getLongitude() +
                               ", Latitude = " + geoPoint.getLatitude() );

            if (marker == null) {
                marker = new Marker(map);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(marker);
            }

            marker.setPosition(geoPoint);
            map.getController().setCenter(map.getMapCenter());
            return true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>(
                Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void save(View view) {
        if (geoPoint != null) {
            Intent data = new Intent();
            data.putExtra("location", geoPoint.toDoubleString());
            setResult(RESULT_OK, data);
        } else {
            setResult(RESULT_CANCELED);
        }
        this.finish();
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        this.finish();
    }
}