package com.wycherley.trackmybus.ui.parent;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wycherley.trackmybus.R;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView ivProfile, ivNotifications;
    private TextView tvEstimateTime;
    private BottomNavigationView bottomNavigation;

    // Sample coordinates for Colombo, Sri Lanka
    private LatLng busCurrentLocation = new LatLng(6.9271, 79.8612);
    private LatLng schoolLocation = new LatLng(6.9319, 79.8478);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initViews();
        setupMap();
        setupBottomNavigation();
    }

    private void initViews() {
        ivProfile = findViewById(R.id.ivProfile);
        ivNotifications = findViewById(R.id.ivNotifications);
        tvEstimateTime = findViewById(R.id.tvEstimateTime);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        tvEstimateTime.setText("07:10 AM");
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Add markers
        addBusMarker();
        addSchoolMarker();
        addRouteStops();

        // Draw route
        drawRoute();

        // Move camera to show entire route
        LatLng centerPoint = new LatLng(
                (busCurrentLocation.latitude + schoolLocation.latitude) / 2,
                (busCurrentLocation.longitude + schoolLocation.longitude) / 2
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, 13));
    }

    private void addBusMarker() {
        // Add bus location marker (red marker)
        MarkerOptions busMarker = new MarkerOptions()
                .position(busCurrentLocation)
                .title("School Bus")
                .snippet("WP NA - 8965")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(busMarker);
    }

    private void addSchoolMarker() {
        // Add school location marker (green marker)
        MarkerOptions schoolMarker = new MarkerOptions()
                .position(schoolLocation)
                .title("Wycherley International School")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(schoolMarker);
    }

    private void addRouteStops() {
        // Sample route stops (pickup points)
        List<LatLng> stops = new ArrayList<>();
        stops.add(new LatLng(6.9290, 79.8550)); // Stop 1
        stops.add(new LatLng(6.9305, 79.8520)); // Stop 2
        stops.add(new LatLng(6.9310, 79.8490)); // Stop 3

        // Add markers for each stop
        for (int i = 0; i < stops.size(); i++) {
            MarkerOptions stopMarker = new MarkerOptions()
                    .position(stops.get(i))
                    .title("Stop " + (i + 1))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mMap.addMarker(stopMarker);
        }
    }

    private void drawRoute() {
        // Create a route line from bus to school
        List<LatLng> routePoints = new ArrayList<>();
        routePoints.add(busCurrentLocation);
        routePoints.add(new LatLng(6.9290, 79.8550)); // Stop 1
        routePoints.add(new LatLng(6.9305, 79.8520)); // Stop 2
        routePoints.add(new LatLng(6.9310, 79.8490)); // Stop 3
        routePoints.add(schoolLocation);

        // Draw blue polyline
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(routePoints)
                .width(10)
                .color(Color.parseColor("#1E3A8A")) // Primary blue
                .geodesic(true);

        mMap.addPolyline(polylineOptions);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_map);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_buses) {
                // TODO: Navigate to Buses
                return true;
            } else if (itemId == R.id.nav_map) {
                return true;
            } else if (itemId == R.id.nav_feedback) {
                // TODO: Navigate to Feedback
                return true;
            } else if (itemId == R.id.nav_about) {
                // TODO: Navigate to About
                return true;
            }
            return false;
        });
    }
}