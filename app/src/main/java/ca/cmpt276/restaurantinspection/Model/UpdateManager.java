package ca.cmpt276.restaurantinspection.Model;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Manages New Updates of Data from Server, and Checks if Update is Needed **/
public class UpdateManager {
    private static UpdateManager instance;
    private Context context;
    private String lastModifiedInspections;
    private String lastModifiedRestaurants;

    private int updated = -1;
    private int cancelled = 0;

    private UpdateManager(Context context) {
        this.context = context;
        this.lastModifiedInspections = null;

        SharedPreferences pref = context.getSharedPreferences("UpdatePref", 0);
        SharedPreferences.Editor editor = pref.edit();

        /** Set some default last updated date **/
        if (pref.getString("last_updated", null) == null) {
            editor.putString("last_updated", "2020-03-01 00:00:00");
        }

        editor.apply();
    }

    public static UpdateManager getInstance() {
        if (instance == null) {
            throw new AssertionError(
                    "UpdateManager.init(InputStream file) must be called first.");
        }

        return instance;
    }

    public static UpdateManager init(Context context) {
        if (instance != null) {
            return null;
        }

        instance = new UpdateManager(context);
        return instance;
    }

    public void setUpdated(int i) {
        this.updated = i;
    }

    public int getUpdated() {
        return updated;
    }

    public void setCancelled(int i) {
        this.cancelled = i;
    }

    public int getCancelled() {
        return cancelled;
    }

    public String getLastModifiedInspections() {
        return lastModifiedInspections;
    }

    public void setLastModifiedInspectionsPrefs(String lastModifiedDate) {
        this.lastModifiedInspections = "2000-01-01 00:00:00";

        SharedPreferences pref = context.getSharedPreferences("UpdatePref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("last_modified_inspections_by_server", lastModifiedDate);
        editor.apply();
    }

    public void setLastModifiedInspections(String lastModifiedDate) {
        this.lastModifiedInspections = lastModifiedDate;
    }

    public String getLastModifiedRestaurants() {
        return lastModifiedRestaurants;
    }

    public void setLastModifiedRestaurantsPrefs(String lastModifiedDate) {
        this.lastModifiedRestaurants = "2000-01-01 00:00:00";

        SharedPreferences pref = context.getSharedPreferences("UpdatePref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("last_modified_restaurants_by_server", lastModifiedDate);
        editor.apply();
    }

    public void setLastModifiedRestaurants(String lastModifiedDate) {
        this.lastModifiedRestaurants = lastModifiedDate;
    }

    public void setLastUpdatedDatePrefs(String lastUpdatedDate) {
        SharedPreferences pref = context.getSharedPreferences("UpdatePref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("last_updated", lastUpdatedDate);
        editor.apply();
    }

    public boolean twentyHrsSinceUpdate() {
        SharedPreferences pref = context.getSharedPreferences("UpdatePref", 0);

        /** Sleep thread to allow app time to get & process server data **/
        try {
            while (pref.getString("last_updated", null) == null) {
                Thread.sleep(50);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        String rawDate = pref.getString("last_updated", null);

        String replacedDate = rawDate.replace("T", " ");
        String[] dateSplit = replacedDate.split("\\.");

        String dateCompare = dateSplit[0];

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        Calendar cal = Calendar.getInstance();

        String today = sdf.format(cal.getTime());

        Date dateStart;
        Date dateEnd;
        long hoursApart;

        try {
            dateStart = sdf.parse(dateCompare);
            dateEnd = sdf.parse(today);

            long diff = dateEnd.getTime() - dateStart.getTime();
            hoursApart =  diff / (60 * 60 * 1000);

            /** Adjusted 3 hours for EST conversion of Server time **/
            if (hoursApart >= 17) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkUpdateNeeded() {
        /** Check if modified time from newly pulled in data equals one in Saved Prefs **/
        SharedPreferences pref = context.getSharedPreferences("UpdatePref", 0);

        try {
            while (pref.getString("last_modified_inspections_by_server", null) == null
                    || pref.getString("last_modified_restaurants_by_server", null) == null) {
                Thread.sleep(10);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        String savedRestaurantsDate = pref.getString("last_modified_restaurants_by_server",
                                                    null);
        String savedInspectionsDate = pref.getString("last_modified_inspections_by_server",
                                                    null);
        String lastUpdatedDate = pref.getString("last_updated", null);

        if (lastUpdatedDate == null) { return true; }

        if (isBeforeDate(lastUpdatedDate, savedRestaurantsDate)) { return true; }

        if (isBeforeDate(lastUpdatedDate, savedInspectionsDate)) { return true; }

        return false;
    }

    private boolean isBeforeDate(String date1, String date2) {
        String firstCleanDate;
        if (date1.contains("T")) {
            String replacedFirstDate = date1.replace("T", " ");
            String[] firstDateSplit = replacedFirstDate.split("\\.");

            firstCleanDate = firstDateSplit[0];
        } else {
            firstCleanDate = date1;
        }

        String replacedSecondDate = date2.replace("T", " ");
        String[] secondDateSplit = replacedSecondDate.split("\\.");

        String secondCleanDate = secondDateSplit[0];

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

        try {
            Date firstDate = sdf.parse(firstCleanDate);
            Date secondDate = sdf.parse(secondCleanDate);

            long diff = secondDate.getTime() - firstDate.getTime();
            float hoursApart =  diff / (60 * 60 * 1000);

            /** Adjusted 3 hours for EST conversion of Server time **/
            if (hoursApart > -3) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
