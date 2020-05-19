package ca.cmpt276.restaurantinspection.Model;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/** Singleton to Keep Track of Favourited Restaurants in Data **/
public class SearchManager implements Iterable<Restaurant> {
    private ArrayList<Restaurant> restaurantsList;
    private static SearchManager instance;
    private int currRestaurantPosition;
    private int currInspectionPosition;
    private int filter = 0;
    private int fromMap = 0;
    private int fromList = 0;

    private String search;
    private String hazard;
    private int lessThanNum;
    private int greaterThanNum;
    private int fave;

    /** Private to prevent anyone else from instantiating. **/
    private SearchManager() {
        restaurantsList = new ArrayList<>();

        this.search = "";
        this.hazard = "All";
        this.lessThanNum = Integer.MAX_VALUE;
        this.greaterThanNum = -1;
        this.fave = 0;
    }

    public static SearchManager getInstance() {
        if (instance == null) {
            return null;
        }

        return instance;
    }

    public static void init() {
        if (instance != null) {
            return;
        }

        instance = new SearchManager();
    }

    public void reset() {
        this.search = "";
        this.hazard = "All";
        this.lessThanNum = Integer.MAX_VALUE;
        this.greaterThanNum = -1;
        this.fave = 0;
        filter = 0;
    }

    public ArrayList<Restaurant> getList() {
        return restaurantsList;
    }

    public Restaurant getRestaurantAt(int index) {
        return restaurantsList.get(index);
    }

    public int getSize() {
        return restaurantsList.size();
    }

    public int getCurrRestaurantPosition() {
        return currRestaurantPosition;
    }

    public int getCurrInspectionPosition() {
        return currInspectionPosition;
    }

    public void setCurrRestaurantPosition(int position) {
        currRestaurantPosition = position;
    }

    public void setCurrInspectionPosition(int position) {
        currInspectionPosition = position;
    }

    public int getFromList() {
        return this.fromList;
    }

    public int getFromMap() {
        return this.fromMap;
    }

    public void setFromList(int i) {
        this.fromList = i;
    }

    public void setFromMap(int i) {
        this.fromMap = i;
    }

    public int getFilter() {
        return filter;
    }

    public String getSearch() {
        return search;
    }

    public String getHazard() {
        return hazard;
    }

    public int getGreaterThanNum() {
        return greaterThanNum;
    }

    public int getLessThanNum() {
        return lessThanNum;
    }

    public int getFave() {
        return fave;
    }

    public void populateSearchManager(int favourite, String search, String hazardLevel,
                                      int lessNumCrit, int greaterNumCrit) {

        readRestaurantData(favourite, search, hazardLevel, lessNumCrit, greaterNumCrit);
        filter = 1;
    }

    private void addNew(Restaurant restaurant) {
        /** Don't add duplicate restaurants across the two data sources **/
        for (Restaurant restaurantInList : restaurantsList) {
            if (restaurant.getId().equals(restaurantInList.getId())) {
                return;
            }
        }

        restaurantsList.add(restaurant);
    }

    private void readRestaurantData(int favourite, String search, String hazardLevel,
                                    int lessNumCrit, int greaterNumCrit) {

        this.restaurantsList = new ArrayList<>();
        this.search = search;
        this.hazard = hazardLevel;
        this.lessThanNum = lessNumCrit;
        this.greaterThanNum = greaterNumCrit;
        this.fave = favourite;

        ArrayList<Restaurant> restaurantList;

        if (favourite == 1) {
            restaurantList = RestaurantManager.getInstance().getFaveList();
        } else {
            restaurantList = RestaurantManager.getInstance().getList();
        }

        for (Restaurant restaurant : restaurantList) {
            if (!hazardLevel.equals("All")) {
                if (restaurant.getName().toLowerCase().contains(search.toLowerCase()) &&
                        restaurant.getNumCriticalIssues() <= lessNumCrit &&
                        restaurant.getNumCriticalIssues() >= greaterNumCrit &&
                        restaurant.getHazard().equals(hazardLevel)) {

                    this.addNew(restaurant);
                }
            } else {
                if (restaurant.getName().toLowerCase().contains(search.toLowerCase()) &&
                        restaurant.getNumCriticalIssues() <= lessNumCrit &&
                        restaurant.getNumCriticalIssues() >= greaterNumCrit) {

                    this.addNew(restaurant);
                }
            }
        }

        this.sortRestaurants();
    }

    private void sortRestaurants() {
        Collections.sort(restaurantsList, new SortRestaurantsByNameAlphabet());
    }

    @Override
    @NonNull
    public Iterator<Restaurant> iterator() {
        return restaurantsList.iterator();
    }
}

class SortRestaurantsByNameAlphabet implements Comparator<Restaurant> {
    @Override
    public int compare(Restaurant o1, Restaurant o2) {
        return o1.compareTo(o2);
    }
}