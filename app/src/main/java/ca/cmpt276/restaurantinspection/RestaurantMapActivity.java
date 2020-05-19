package ca.cmpt276.restaurantinspection;

import androidx.annotation.NonNull;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import ca.cmpt276.restaurantinspection.Adapters.RestaurantInfoWindowAdapter;
import ca.cmpt276.restaurantinspection.Model.CustomMarker;
import ca.cmpt276.restaurantinspection.Model.OwnIconRendered;
import ca.cmpt276.restaurantinspection.Model.Restaurant;
import ca.cmpt276.restaurantinspection.Model.RestaurantManager;
import ca.cmpt276.restaurantinspection.Model.SearchManager;
import ca.cmpt276.restaurantinspection.Model.UpdateManager;
import ca.cmpt276.restaurantinspection.Model.ViolationsMap;
import ca.cmpt276.restaurantinspection.Model.DataManager;

/** Map Displaying GPS Location of All Restaurants **/
public class RestaurantMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private UpdateManager updateManager;
    private DataManager dataManager;
    private RestaurantManager restaurantManager;
    private SearchManager searchManager;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Algorithm<CustomMarker> clusterManagerAlgorithm;
    private ClusterManager <CustomMarker> mClusterManager;
    private OwnIconRendered mRender;
    private static final float DEFAULT_ZOOM = 17f;
    private GoogleMap mMap;
    private final String RESTAURANT_FILENAME = "update_restaurant";
    private final String INSPECTION_FILENAME = "update_inspection";

    private final int DOWNLOAD_REQUEST_CODE = 100;
    private final int SEARCH_REQUEST_CODE = 200;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_map);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_restaurant_map));
            actionBar.setElevation(0);
        }

        UpdateManager.init(this);
        DataManager.init(this);
        dataManager = DataManager.getInstance();

        updateChecker();

        InputStream restaurantsIn = getResources().openRawResource(R.raw.restaurants_itr1);
        InputStream inspectionsIn = getResources().openRawResource(R.raw.inspectionreports_itr1);

        FileInputStream internalRestaurants = null;
        FileInputStream internalInspections = null;

        try {
            internalRestaurants = openFileInput(RESTAURANT_FILENAME);
            internalInspections = openFileInput(INSPECTION_FILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStream violationsIn = getResources().openRawResource(R.raw.all_violations);

        ViolationsMap.init(violationsIn, this);

        if (internalRestaurants == null && internalInspections == null) {
            RestaurantManager.init(restaurantsIn, inspectionsIn, this);
        } else {
            RestaurantManager.init(internalRestaurants, internalInspections, this);
        }

        restaurantManager = RestaurantManager.getInstance();
        restaurantManager.readFavoriteList(this);

        if (SearchManager.getInstance() != null && SearchManager.getInstance().getFilter() == 1){
            searchManager = SearchManager.getInstance();
        } else {
            SearchManager.init();
            searchManager = SearchManager.getInstance();
        }

        searchManager.setFromMap(1);
        searchManager.setFromList(0);

        initMap();

        SharedPreferences pref = this.getSharedPreferences("UpdatePref", 0);
        SharedPreferences.Editor editor = pref.edit();

        if (updateManager.getUpdated() == 1) {
            editor.putString("last_modified_restaurants_by_server",
                    updateManager.getLastModifiedRestaurants());
            editor.putString("last_modified_inspections_by_server",
                    updateManager.getLastModifiedInspections());
            editor.apply();

            int check = 0;

            for (Restaurant res : restaurantManager.getFavoriteList()) {
                if (res.getOldNumInspections() < res.getInspectionsList().size()) {
                    check = 1;
                    break;
                }
            }

            if (check == 1) {
                Intent intent = new Intent(this, PopUpNewInspectionActivity.class);
                startActivity(intent);
            }

            updateManager.setUpdated(2);
        }

        startRestaurantListActivity();
    }

    private void updateChecker() {
        /** === CHECKING FOR UPDATES === **/
        updateManager = UpdateManager.getInstance();
        updateManager.twentyHrsSinceUpdate();
        /** === END CHECKING === **/

        if (updateManager.twentyHrsSinceUpdate() && updateManager.getCancelled() == 0) {
            /** and if an update exists then **/
            /** UNCOMMENT AFTER TESTING -- NO NEW DATA so pop-up won't show up **/
            if (updateManager.checkUpdateNeeded()) {
                startActivityForResult(new Intent(RestaurantMapActivity.this,
                        PopUpUpdateActivity.class), DOWNLOAD_REQUEST_CODE);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (Build.VERSION.SDK_INT >= 24) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        getDeviceLocation();

        setUpCluster();
        startMapActivityFromRestaurantInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                /** Permission Denied **/
                Toast.makeText(this, getResources().getString(R.string.str_location_turned_off), Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startMapActivityFromRestaurantInfo() {
        if (getCallingActivity() != null) {
            if (getCallingActivity().getClassName().equals("ca.cmpt276.restaurantinspection.RestaurantInfoActivity")) {
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            if (SearchManager.getInstance() == null || SearchManager.getInstance().getFilter() == 0) {
                                Restaurant restaurant = restaurantManager.getRestaurantAt(restaurantManager.getCurrRestaurantPosition());
                                LatLng position = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                                moveCamera(position, DEFAULT_ZOOM);
                            } else {
                                Restaurant restaurant = searchManager.getRestaurantAt(searchManager.getCurrRestaurantPosition());
                                LatLng position = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                                moveCamera(position, DEFAULT_ZOOM);
                            }
                        } else {
                            Toast.makeText(RestaurantMapActivity.this, getResources().getString(R.string.str_unable_get_rest_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    if (currentLocation != null) {
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                    }
                } else {
                    Toast.makeText(RestaurantMapActivity.this, getResources().getString(R.string.str_unable_get_current_location), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /** Obtain the SupportMapFragment and get notified when the map is ready to be used. **/
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setUpCluster() {
        /** Initialize the manager with context and the map **/
        mClusterManager = new ClusterManager<>(this, mMap);
        clusterManagerAlgorithm = new NonHierarchicalDistanceBasedAlgorithm<>();
        mClusterManager.setAlgorithm(clusterManagerAlgorithm);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        addMapItems();
        mRender = new OwnIconRendered(RestaurantMapActivity.this, mMap, mClusterManager);
        mClusterManager.setRenderer(mRender);

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<CustomMarker>() {
            @Override
            public void onClusterItemInfoWindowClick(CustomMarker marker) {
                int position = getRestaurantPosition(marker.getPosition());

                if (SearchManager.getInstance() == null || SearchManager.getInstance().getFilter() == 0) {
                    restaurantManager.setCurrRestaurantPosition(position);
                    restaurantManager.setFromMap(1);
                    restaurantManager.setFromList(0);
                } else {
                    searchManager.setCurrRestaurantPosition(position);
                    searchManager.setFromMap(1);
                    searchManager.setFromList(0);
                }

                Intent intent = new Intent(RestaurantMapActivity.this, RestaurantInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addMapItems() {
        mClusterManager.getMarkerCollection().setInfoWindowAdapter(new RestaurantInfoWindowAdapter(RestaurantMapActivity.this));
        if (SearchManager.getInstance() == null || SearchManager.getInstance().getFilter() == 0){
            for (Restaurant restaurant : restaurantManager) {
                if (restaurant != null) {
                    try {
                        double latitude = restaurant.getLatitude();
                        double longitude = restaurant.getLongitude();
                        String title = restaurant.getName();
                        String hazard = restaurant.getHazard();
                        String snippet = getString(R.string.str_map_snippet, restaurant.getAddress(), hazard);

                        CustomMarker location = new CustomMarker(latitude, longitude, title, snippet, hazard);
                        mClusterManager.addItem(location);
                    } catch (NullPointerException e) {
                        Log.e("", "markRestaurantLocations: NullPointerException: " + e.getMessage());
                    }
                }
            }
        } else {
            for (Restaurant restaurant : searchManager) {
                if (restaurant != null) {
                    try {
                        double latitude = restaurant.getLatitude();
                        double longitude = restaurant.getLongitude();
                        String title = restaurant.getName();
                        String hazard = restaurant.getHazard();
                        String snippet = getString(R.string.str_map_snippet, restaurant.getAddress(), hazard);

                        CustomMarker location = new CustomMarker(latitude, longitude, title, snippet, hazard);
                        mClusterManager.addItem(location);
                    } catch (NullPointerException e) {
                        Log.e("", "markRestaurantLocations: NullPointerException: " + e.getMessage());
                    }
                }
            }
        }

    }

    private void startRestaurantListActivity() {
        Button btn = findViewById(R.id.restaurants_button_inact);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = makeRestaurantListIntent(getApplicationContext());
                startActivity(intent);
                finish();
            }
        });
    }

    public static Intent makeRestaurantListIntent(Context c) {
        return new Intent(c, RestaurantActivity.class);
    }

    private int getRestaurantPosition(LatLng position) {
        double lat = position.latitude;
        double lng = position.longitude;
        if (SearchManager.getInstance() == null || SearchManager.getInstance().getFilter() == 0) {
            for (int i = 0; i < restaurantManager.getList().size(); i++) {
                Restaurant restaurant = restaurantManager.getList().get(i);

                if (restaurant.getLatitude() == lat && restaurant.getLongitude() == lng) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < searchManager.getList().size(); i++) {
                Restaurant restaurant = searchManager.getList().get(i);
                if (restaurant.getLatitude() == lat && restaurant.getLongitude() == lng) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DOWNLOAD_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.str_map_refreshing),
                    Toast.LENGTH_SHORT);

            toast.show();

            restaurantManager.reset();
            searchManager.reset();

            if (updateManager.getUpdated() == 1) {
                dataManager.reset();
            }

            startActivity(new Intent(this, RestaurantMapActivity.class));
            finish();

        } else if (requestCode == DOWNLOAD_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.str_download_canceled),
                    Toast.LENGTH_SHORT);

            toast.show();

        } else if (requestCode == SEARCH_REQUEST_CODE && resultCode == RESULT_OK) {
            startActivity(new Intent(this, RestaurantMapActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            searchManager.setFromMap(1);
            searchManager.setFromList(0);
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, SEARCH_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

}