public class ReferenceRange {
    Double lower;
    Double upper;
    Double criticalLower;
    Double criticalUpper;

    public ReferenceRange(Double lower, Double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public ReferenceRange(Double lower, Double upper, Double criticalLower, Double criticalUpper) {
        this.lower = lower;
        this.upper = upper;
        this.criticalLower = criticalLower;
        this.criticalUpper = criticalUpper;
    }

    public String getStatus(double value) {
        if (criticalLower != null && value < criticalLower) return "CRITICAL LOW";
        if (criticalUpper != null && value > criticalUpper) return "CRITICAL HIGH";
        if (lower != null && value < lower) return "LOW";
        if (upper != null && value > upper) return "HIGH";
        return "NORMAL";
    }

    public String toString() {
        if (lower == null) return "< " + upper;
        return lower + " - " + upper;
    }
}