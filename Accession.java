import java.util.ArrayList;
import java.util.List;

public class Accession {
    public String accessionNumber;
    public Patient patient;
    public List<LabTest> tests;
    public String timestamp;
    public String status; // Pending, Preliminary, Final
    public double packagePrice = 0;
    public String packageName = "";

    public Accession(String accessionNumber, Patient patient, String timestamp) {
        this.accessionNumber = accessionNumber;
        this.patient = patient;
        this.timestamp = timestamp;
        this.tests = new ArrayList<>();
        this.status = "Pending";
    }

    public void addTest(LabTest t) {
        tests.add(t);
    }

    public double getTotalPrice() {
        if (packagePrice > 0) {
            double extra = 0;
            for (LabTest t : tests) {
                if (t.fromPackage) continue;
                extra += t.price;
            }
            return packagePrice + extra;
        }
        double total = 0;
        for (LabTest t : tests) total += t.price;
        return total;
    }

    public void updateStatus() {
        if (tests.isEmpty()) { status = "Pending"; return; }
        boolean allFinal = true;
        boolean anyFinal = false;
        for (LabTest t : tests) {
            if (t.status.equals("Final")) anyFinal = true;
            else allFinal = false;
        }
        if (allFinal) status = "Final";
        else if (anyFinal) status = "Preliminary";
        else status = "Pending";
    }
}