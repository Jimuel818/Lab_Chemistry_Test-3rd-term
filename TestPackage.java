public class TestPackage {
    String packageName;
    double packagePrice;
    LabTest[] tests;
    int count;

    public TestPackage(String packageName, double packagePrice, LabTest[] tests) {
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        this.tests = tests;
        this.count = tests.length;
    }

    public LabTest[] getTests() {
        return tests;
    }

    public String toString() {
        return packageName + " (Package - " + count + " tests) - P" + String.format("%.2f", packagePrice);
    }
}