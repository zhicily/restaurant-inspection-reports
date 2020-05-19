package ca.cmpt276.restaurantinspection.TestModel;

public class TestInspection {
    private String numCrit;
    private String numNonCrit;
    private String date;
    private String hazard;

    public TestInspection(String hl, String nc, String nnc, String idate) {
        hazard = hl;
        numCrit = nc;
        numNonCrit = nnc;
        date = idate;
    }

    public String getDate() {
        return date;
    }

    public String getNumCrit() {
        return numCrit;
    }

    public String getNumNonCrit() {
        return numNonCrit;
    }

    public String getHazard() {
        return hazard;
    }
}
