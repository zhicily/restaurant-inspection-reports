package ca.cmpt276.restaurantinspection.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import ca.cmpt276.restaurantinspection.R;

/** Adapter for Restaurant Info Window Pop-up when Map Marker is Toggled **/
public class RestaurantInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;

    public RestaurantInfoWindowAdapter(Context context) {
        this.mWindow = LayoutInflater.from(context).inflate(R.layout.restaurant_info_window, null);
    }

    private void renderWindowText(Marker marker, View view){
        String title = marker.getTitle();

        TextView tvTitle = view.findViewById(R.id.title);
        tvTitle.setText(title);

        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);

        tvSnippet.setText(snippet);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
