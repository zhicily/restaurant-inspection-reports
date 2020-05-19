package ca.cmpt276.restaurantinspection.Model;

import android.content.Context;
import java.util.ArrayList;
import ca.cmpt276.restaurantinspection.R;

/** Restaurant Object to Represent Each Restaurant in Data **/
public class Restaurant implements Comparable<Restaurant>{
    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private ArrayList<Inspection> inspectionsList;
    private int oldNumInspections;
    private boolean favorite;
    private Context context;

    public Restaurant(String restaurantLump, Context context) {
        restaurantLump = restaurantLump.replaceAll(", ", " ");
        restaurantLump = restaurantLump.replaceAll("\"", "");

        String[] restaurantInfo = restaurantLump.split(",");
        /** [0: ID, 1: Name, 2: PhysAddress, 3: PhysCity, 4: FacType, 5: Latitude, 6: Longitude] **/
        id = restaurantInfo[0];
        name = restaurantInfo[1];
        address = restaurantInfo[2] + ", " + restaurantInfo[3];
        latitude = Double.parseDouble(restaurantInfo[5]);
        longitude = Double.parseDouble(restaurantInfo[6]);
        favorite = false;
        inspectionsList = new ArrayList<>();
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setOldNumInspections(int i) {
        this.oldNumInspections = i;
    }

    public int getOldNumInspections() {
        return oldNumInspections;
    }

    public Inspection getInspectionAt(int index) {
        return inspectionsList.get(index);
    }

    public ArrayList<Inspection> getInspectionsList() {
        return inspectionsList;
    }

    public String getHazard() {
        if (this.inspectionsList.size() > 0) {
            return inspectionsList.get(0).getHazardRating();
        }
        return "Low";
    }

    public String getDate() {
        if (this.inspectionsList.size() > 0) {
            return inspectionsList.get(0).getDateDisplay();
        }
        return context.getResources().getString(R.string.str_no_recent_insp);
    }

    public String getNumIssues() {
        if (this.inspectionsList.size() > 0) {
            int num = inspectionsList.get(0).getNumCritical() + inspectionsList.get(0).getNumNonCritical();
            return Integer.toString(num);
        }

        return "0";
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isFavorite() {
        if (favorite){
            return true;
        }

        return false;
    }

    public int getNumCriticalIssues() {
        int num = 0;

        if (this.inspectionsList.size() > 0) {
            for (Inspection inspection : inspectionsList) {
                if (inspection.getWithinLastYear()) {
                    num += inspection.getNumCritical();
                }
            }
        }

        return num;
    }

    public void addInspection(Inspection inspection) {
        inspectionsList.add(inspection);
    }

    @Override
    public int compareTo(Restaurant restaurant) {
        int l1 = this.name.length();
        int l2 = restaurant.getName().length();
        int min = Math.min(l1, l2);

        for (int i = 0; i < min; i++) {
            int s1Char = (int)this.name.charAt(i);
            int s2Char = (int)restaurant.getName().charAt(i);

            if(s1Char != s2Char)
                return s1Char - s2Char;
        }

        if (l1 != l2) { return l1 - l2; }
        else { return 0; }
    }
}