package ca.cmpt276.restaurantinspection.TestModel;

public class TestViolation {
    private String type;
    private String level;
    private String sDesc;
    private String lDesc;

    public TestViolation(String vtype, String vlevel, String vSDesc, String vLongDesc) {
        type = vtype;
        level = vlevel;
        sDesc = vSDesc;
        lDesc = vLongDesc;
    }

    public String getType() {
        return type;
    }

    public String getLevel() {
        return level;
    }

    public String getsDesc() {
        return sDesc;
    }

    public String getlDesc() {
        return lDesc;
    }
}
