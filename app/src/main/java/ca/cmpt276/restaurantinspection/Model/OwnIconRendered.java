package ca.cmpt276.restaurantinspection.Model;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/** Intelligent Clustering of Restaurant Pegs on Map **/
public class OwnIconRendered extends DefaultClusterRenderer<CustomMarker> {
    private RestaurantManager manager = RestaurantManager.getInstance();
    private int position = manager.getCurrRestaurantPosition();
    private Restaurant restaurant = manager.getRestaurantAt(position);
    private String name = restaurant.getName();
    private LatLng pos = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());

    public OwnIconRendered(Context context, GoogleMap map,
                           ClusterManager<CustomMarker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(CustomMarker item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getIcon());
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected  void onClusterItemRendered (CustomMarker item, Marker marker){
        super.onClusterItemRendered(item, marker);
        if (SearchManager.getInstance()!= null && SearchManager.getInstance().getFilter() == 1){
            SearchManager searchManager = SearchManager.getInstance();
            int searchPosition = searchManager.getCurrRestaurantPosition();
            Restaurant searchRestaurant = searchManager.getRestaurantAt(searchPosition);
            String searchName = searchRestaurant.getName();
            LatLng searchPos = new LatLng(searchRestaurant.getLatitude(), searchRestaurant.getLongitude());
            if (item.getPosition().equals(searchPos) && item.getTitle().equals(searchName)) {
                getMarker(item).showInfoWindow();
            }
        }
        else{
            if (item.getPosition().equals(pos) && item.getTitle().equals(name)) {
                getMarker(item).showInfoWindow();
            }
        }

    }
}
