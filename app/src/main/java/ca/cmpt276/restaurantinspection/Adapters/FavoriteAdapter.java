package ca.cmpt276.restaurantinspection.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import ca.cmpt276.restaurantinspection.Model.Restaurant;
import ca.cmpt276.restaurantinspection.R;

/** RecyclerView Adapter for list of Favourite Restaurants with New Inspections **/
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private ArrayList<Restaurant> favoriteList;
    private ArrayList<Restaurant> restaurantList;
    private OnFavoriteListener favoriteListener;

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView hazardIcon;
        private TextView restaurantName;
        private TextView newestInspDate;

        OnFavoriteListener onFavoriteListener;

        private FavoriteViewHolder(@NonNull View itemView, FavoriteAdapter.OnFavoriteListener onFavoriteListener) {
            super(itemView);
            newestInspDate = itemView.findViewById(R.id.newest_inspection_date);
            hazardIcon = itemView.findViewById(R.id.hazard_level_icon);
            restaurantName = itemView.findViewById(R.id.restaurant_name);

            this.onFavoriteListener = onFavoriteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFavoriteListener.onViolationClick(getAdapterPosition());
        }
    }

    public interface OnFavoriteListener {
        void onViolationClick(int position);
    }

    public FavoriteAdapter(ArrayList<Restaurant> Favorites, ArrayList<Restaurant> Restaurants, FavoriteAdapter.OnFavoriteListener onFavoriteListener) {
        favoriteList = Favorites;
        restaurantList = Restaurants;
        favoriteListener = onFavoriteListener;
    }

    @NonNull
    @Override
    public FavoriteAdapter.FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_inspection_favorite_layout, parent, false);
        return new FavoriteAdapter.FavoriteViewHolder(view, favoriteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.FavoriteViewHolder holder, int position) {
        if (favoriteList.size() == 0) {
            return;
        }

        Restaurant currRestaurant = null;
        Restaurant currFavorite = favoriteList.get(position);

        if (currFavorite.getOldNumInspections() < currFavorite.getInspectionsList().size()) {
            currRestaurant = currFavorite;
        } else {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            lp.height = 0;
            holder.itemView.setLayoutParams(lp);
        }

        if (currRestaurant == null){
            return;
        }

        holder.restaurantName.setText(currRestaurant.getName());
        holder.newestInspDate.setText(currRestaurant.getDate());

        switch (currRestaurant.getHazard()) {
            case "High":
                holder.hazardIcon.setImageResource(R.drawable.circle_red);
                break;
            case "Moderate":
                holder.hazardIcon.setImageResource(R.drawable.circle_yellow);
                break;
            case "Low":
                holder.hazardIcon.setImageResource(R.drawable.circle_green);
                break;
            case "None":
                holder.hazardIcon.setImageResource(R.drawable.circle_green);
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
