package ca.cmpt276.restaurantinspection.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;
import ca.cmpt276.restaurantinspection.Model.Inspection;
import ca.cmpt276.restaurantinspection.R;

/** RecyclerView Adapter for list of Inspections for a Restaurant **/
public class InspectionAdapter extends RecyclerView.Adapter<InspectionAdapter.InspectionViewHolder> {
    private ArrayList<Inspection> inspectionList;
    private OnInspectionListener onInspectionListener;

    public static class InspectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView inspBackground;
        private TextView inspDate;
        private TextView numCritIssues;
        private TextView numNonCritIssues;

        OnInspectionListener onInspectionListener;

        private InspectionViewHolder(@NonNull View itemView, OnInspectionListener onInspectionListener) {
            super(itemView);
            inspBackground = itemView.findViewById(R.id.inspection_button_background);
            inspDate = itemView.findViewById(R.id.inspection_date);
            numCritIssues = itemView.findViewById(R.id.num_crit_issues);
            numNonCritIssues = itemView.findViewById(R.id.num_non_crit_issues);

            this.onInspectionListener = onInspectionListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onInspectionListener.onInspectionClick(getAdapterPosition());
        }
    }

    public interface OnInspectionListener {
        void onInspectionClick(int position);
    }

    public InspectionAdapter(ArrayList<Inspection> inspections, OnInspectionListener onInspectionListener) {
        inspectionList = inspections;
        this.onInspectionListener = onInspectionListener;
    }

    @NonNull
    @Override
    public InspectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inspection_layout, parent, false);

        return new InspectionViewHolder(view, onInspectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull InspectionViewHolder holder, int position) {
        Inspection currInspection = inspectionList.get(position);

        switch(currInspection.getHazardRating()) {
            case "High":
                holder.inspBackground.setImageResource(R.drawable.inspection_high);
                break;
            case "Moderate":
                holder.inspBackground.setImageResource(R.drawable.inspection_mod);
                break;
            case "Low":
                holder.inspBackground.setImageResource(R.drawable.inspection_low);
                break;
            case "None":
                holder.inspBackground.setImageResource(R.drawable.inspection_none);
            default:
                break;
        }

        holder.inspDate.setText(currInspection.getDateDisplay());
        holder.numCritIssues.setText(String.format(Locale.CANADA,
                                        "%d",currInspection.getNumCritical()));
        holder.numNonCritIssues.setText(String.format(Locale.CANADA,
                                        "%d",currInspection.getNumNonCritical()));
    }

    @Override
    public int getItemCount() {
        return inspectionList.size();
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
