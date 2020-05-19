package ca.cmpt276.restaurantinspection.Model;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/** Mapping Lookup of Violations Based on Code; Contains Long & Short Descriptions **/
public class ViolationsMap {
    private Context context;
    private static ViolationsMap instance = null;
    private static Map<String, String[]> violationsLookup;

    private ViolationsMap(InputStream file, Context context) {
        this.context = context;
        violationsLookup = readViolationsData(file);
    }

    public static ViolationsMap getInstance() {
        if (instance == null) {
            throw new AssertionError(
                    "ViolationsMap.init(InputStream file) must be called first.");
        }

        return instance;
    }

    public static ViolationsMap init(InputStream violationsFile, Context current) {
        if (instance != null) {
            return null;
        }

        instance = new ViolationsMap(violationsFile, current);
        return instance;
    }

    /** Returning Violation Details from MAP **/
    public static String[] getViolationFromMap(String key) {
        return violationsLookup.get(key);
    }

    /** REFERENCE: https://stackoverflow.com/questions/38415680/how-to-parse-csv-file-into-an-array-in-android-studio **/
    private Map<String, String[]> readViolationsData(InputStream file) {
        Map<String, String[]> violationsLookup = new HashMap<>();

        BufferedReader input = new BufferedReader(new InputStreamReader(file));

        try {
            String violation;

            while ((violation = input.readLine()) != null) {
                String[] line = violation.split(",");
                line[0] = line[0].substring(1);
                String name = "str_" + line[0];

                String packageName = context.getPackageName();
                int resID = context.getResources().getIdentifier(name, "string", packageName);
                String vio = context.getResources().getString(resID);
                String[] info = vio.split(",");

                violationsLookup.put(line[0], info);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("ERROR: Failed to read in " + file);
        }
        try {
            file.close();
        }
        catch (IOException ex) {
            throw new RuntimeException("ERROR: Failed to close " + file);
        }
        return violationsLookup;
    }
}
