package ca.cmpt276.restaurantinspection.TestModel;

/** DELETE THIS CLASS AT THE END !!!!! **/

public class TestRestaurant {
    private String name;
    private String date;
    private String hazard;
    private String numIssues;

    public TestRestaurant(String rname, String rdate, String rhazard, String rNumIssues) {
        name = rname;
        date = rdate;
        hazard = rhazard;
        numIssues = rNumIssues;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getNumIssues() {
        return numIssues;
    }

    public String getHazard() {
        return hazard;
    }
}
