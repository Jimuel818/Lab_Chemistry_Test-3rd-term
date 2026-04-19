public abstract class LabTest {
    String testName;
    String unit;
    Double result;
    double price;
    String category;
    ReferenceRange maleRange;
    ReferenceRange femaleRange;
    String specimenType;
    String fastingStatus;
    String collectionTime;
    String status; // Pending, Preliminary, Final
    boolean fromPackage = false;

    public LabTest(String testName, String unit, double price, String category) {
        this.testName = testName;
        this.unit = unit;
        this.price = price;
        this.category = category;
        this.status = "Pending";
    }

    public void setResult(Double result) {
        this.result = result;
        this.status = "Final";
    }

    public String getRangeString(String sex) {
        ReferenceRange r = sex.equals("Male") ? maleRange : femaleRange;
        return r.toString() + " " + unit + " (" + sex + ")";
    }

    public String evaluate(String sex) {
        if (result == null) return "PENDING";
        ReferenceRange r = sex.equals("Male") ? maleRange : femaleRange;
        if (r.lower == null) {
            return (result <= r.upper) ? "NORMAL" : "HIGH";
        }
        return r.getStatus(result);
    }

    public String getFormattedResult(String sex) {
        String evalStatus = evaluate(sex);
        String flag = evalStatus.equals("NORMAL") ? "" : "  [!] " + evalStatus;
        return String.format("  %-20s %8.2f %-8s  Ref: %-25s%s",
                testName, result == null ? 0.0 : result, unit, getRangeString(sex), flag);
    }
}