package ca.cmpt276.restaurantinspection.Model;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import ca.cmpt276.restaurantinspection.R;

/** Setting up Map Pegs depending on Hazard Level of a Restaurant's Latest Inspection **/
public class CustomMarker implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private BitmapDescriptor mIcon;

    public CustomMarker(double latitude, double longitude, String title, String snippet, String hazard) {
        mPosition = new LatLng(latitude, longitude);
        mTitle = title;
        mSnippet = snippet;

        switch(hazard){
            case "Low":
                mIcon = BitmapDescriptorFactory.fromResource(R.drawable.peg_low);
                break;
            case "Moderate":
                mIcon = BitmapDescriptorFactory.fromResource(R.drawable.peg_mod);
                break;
            case "High":
                mIcon = BitmapDescriptorFactory.fromResource(R.drawable.peg_high);
                break;
            default:
                break;
        }
    }

    public BitmapDescriptor getIcon(){ return mIcon;}

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
