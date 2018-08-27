package com.iws.googlemapclustering;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static GeoService geoService;
    private static LatLng currentPosition;
    private ClusterManager<MarkerItemModel> mClusterManager;

    // Its a service bound state checker
    private static boolean isGeoServiceBound = false;

    private final ServiceConnection geoConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            geoService = ((GeoService.GeoServiceBinder) iBinder).getGeoService();
            isGeoServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isGeoServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Bind the service with Geo location service class
    @Override
    protected void onStart() {
        super.onStart();
        Intent geoServiceIntent = new Intent(MainActivity.this, GeoService.class);
        // Start the service first
        startService(new Intent(MainActivity.this, GeoService.class));
        // Bind with this server from this UI
        bindService(geoServiceIntent, geoConnection, BIND_AUTO_CREATE);
    }

    // Unbind when app goes stop
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(geoConnection);
    }

    // This callback is triggered when the map is ready to be used.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // For set my location properties, need this permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Setup various MAP properties to provide custom Map UI view
        mMap.getUiSettings().setCompassEnabled(true); // To show compass on MAP

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // Enabled normal MAP type, also have other four types

        // In latest API level it requires runtime permission to get this service
        mMap.setMyLocationEnabled(true); // Get user current location
        mMap.setTrafficEnabled(true); // To enabling traffic mode
        mMap.getUiSettings().setZoomGesturesEnabled(true); // Enable gesture zooming functionality

        // Add a marker in Sydney and move the camera
        LatLng india = new LatLng(28.5355, 77.3910);
        createMarker(mMap, india);

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MarkerItemModel>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Zoom when tap on cluster in map
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MarkerItemModel>() {
            @Override
            public boolean onClusterClick(Cluster<MarkerItemModel> cluster) {

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                        (float) Math.floor(mMap.getCameraPosition().zoom + 1)), 300, null);

                return true;
            }
        });

        // Set custom render for this cluster
        mClusterManager.setRenderer(new CustomClusterRender(MainActivity.this, googleMap, mClusterManager));

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    // Called by Geo location service, when user location is changed
    public static void updateLocation(Location location) {

        // Check location is not null
        if (isGeoServiceBound && null != location) {
            // Initialized the current location coordinates values
            currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    // Create marker for you current position in the map
    private static void createMarker(GoogleMap mMap, LatLng currentPosition) {
        mMap.addMarker(new MarkerOptions().position(currentPosition).title("Marker in here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
    }

    // Create 10 markers in this cluster
    private void addItems() {

        if (mClusterManager != null) {

            // Clear cluster
            mClusterManager.clearItems();

            // Set some lat/lng coordinates to start with.
            double lat = 28.5355;
            double lng = 77.3910;

            // Add ten cluster items in close proximity, for purposes of this example.
            for (int i = 0; i < 10; i++) {

                double offset = i / 60d;

                // If you do see marker in cluster, you may use this offset
                // double offset = 0.0000111d;
                // offset = offset * i;

                lat = lat + offset;
                lng = lng + offset;

                MarkerItemModel offsetItem = new MarkerItemModel(new LatLng(lat, lng), "Title " + (i), "Snipt " + (i));
                mClusterManager.addItem(offsetItem);
            }

            // Recluster items
            mClusterManager.cluster();
        }
    }
}