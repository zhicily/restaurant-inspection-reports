package ca.cmpt276.restaurantinspection;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import ca.cmpt276.restaurantinspection.Adapters.ViolationAdapter;
import ca.cmpt276.restaurantinspection.Model.Inspection;
import ca.cmpt276.restaurantinspection.Model.Restaurant;
import ca.cmpt276.restaurantinspection.Model.RestaurantManager;
import ca.cmpt276.restaurantinspection.Model.SearchManager;
import ca.cmpt276.restaurantinspection.Model.Violation;

/** List of Violations Associated with an Inspection Record **/
public class RestaurantViolationActivity extends AppCompatActivity implements ViolationAdapter.OnViolationListener {
    private Inspection inspection;
    private ArrayList<Violation> violationList;
    private Toast toast;
    private RestaurantManager restaurantManager = RestaurantManager.getInstance();
    private SearchManager searchManager;
    private int restaurantIndex;
    private int inspectionIndex;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation);

        if (SearchManager.getInstance() != null && SearchManager.getInstance().getFilter() == 1) {
            searchManager = SearchManager.getInstance();
            restaurantIndex = searchManager.getCurrRestaurantPosition();
            inspectionIndex = searchManager.getCurrInspectionPosition();
            restaurant = searchManager.getRestaurantAt(restaurantIndex);
            inspection = restaurant.getInspectionAt(inspectionIndex);
        } else {
            restaurantIndex = restaurantManager.getCurrRestaurantPosition();
            inspectionIndex = restaurantManager.getCurrInspectionPosition();
            restaurant = restaurantManager.getRestaurantAt(restaurantIndex);
            inspection = restaurant.getInspectionAt(inspectionIndex);
        }

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(restaurant.getName());
            actionBar.setElevation(0);
        }

        setViolationTextAndImages();

        extractInspectionViolations();
    }

    private void setViolationTextAndImages() {
        ImageView inspectionOverview = this.findViewById(R.id.inspection_overview);
        TextView dateAndLevel = this.findViewById(R.id.date_level);
        TextView inspectionType = this.findViewById(R.id.inspection_type);
        TextView numNonCritIssues = this.findViewById(R.id.num_noncrit_issues_details);
        TextView numCritIssues = this.findViewById(R.id.num_crit_issues_details);

        String hazard = "";
        switch(inspection.getHazardRating()) {
            case "High":
                inspectionOverview.setImageResource(R.drawable.inspection_det_high);
                hazard = getResources().getString(R.string.str_high);
                break;
            case "Moderate":
                inspectionOverview.setImageResource(R.drawable.inspection_det_med);
                hazard = getResources().getString(R.string.str_moderate);
                break;
            case "Low":
                inspectionOverview.setImageResource(R.drawable.inspection_det_low);
                hazard = getResources().getString(R.string.str_low);
                break;
            case "None":
                inspectionOverview.setImageResource(R.drawable.inspection_det_none);
                hazard = getResources().getString(R.string.str_none);
                break;
        }

        dateAndLevel.setText(getString(R.string.str_date_level, inspection.getFullDate(),
                                        hazard));
        inspectionType.setText(inspection.getInspType());
        numCritIssues.setText(getString(R.string.str_num_issues, inspection.getNumCritical()));
        numNonCritIssues.setText(getString(R.string.str_num_issues, inspection.getNumNonCritical()));
    }

    private void extractInspectionViolations() {
        RecyclerView violationRecyclerView;
        RecyclerView.Adapter violationAdapter;
        RecyclerView.LayoutManager violationLayoutManager;

        if (inspection.getViolationsList().size() == 0) {
            TextView noViolationsMsg = this.findViewById(R.id.no_violations_msg);
            noViolationsMsg.setText(R.string.str_no_violation);
        } else {
            violationList = inspection.getViolationsList();
            violationRecyclerView = findViewById(R.id.rv3);
            violationRecyclerView.setHasFixedSize(true);

            violationLayoutManager = new LinearLayoutManager(this);
            violationRecyclerView.setLayoutManager(violationLayoutManager);

            violationAdapter = new ViolationAdapter(violationList, this);
            violationRecyclerView.setAdapter(violationAdapter);
        }
    }

    @Override
    public void onViolationClick(int position) {
        if (toast != null) {
            toast.cancel();
        }

        String longDescription = violationList.get(position).getLongDescription();
        toast = Toast.makeText(this, longDescription, Toast.LENGTH_LONG);
        toast.show();
    }
}