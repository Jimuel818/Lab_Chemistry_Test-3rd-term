//sub classes for chemistry test

class FastingBloodSugar extends LabTest {
    public FastingBloodSugar() {
        super("FBS", "mg/dL", 150.00, "Blood Sugar");
        this.maleRange   = new ReferenceRange(74.0, 100.0, 40.0, 400.0);
        this.femaleRange = new ReferenceRange(74.0, 100.0, 40.0, 400.0);
    }
}

class RandomBloodSugar extends LabTest {
    public RandomBloodSugar() {
        super("RBS", "mg/dL", 150.00, "Blood Sugar");
        this.maleRange   = new ReferenceRange(70.0, 199.0, 40.0, 400.0);
        this.femaleRange = new ReferenceRange(70.0, 199.0, 40.0, 400.0);
    }
}

class TotalCholesterol extends LabTest {
    public TotalCholesterol() {
        super("Total Cholesterol", "mg/dL", 200.00, "Lipid Panel");
        this.maleRange   = new ReferenceRange(150.0, 200.0);
        this.femaleRange = new ReferenceRange(150.0, 200.0);
    }
}

class HDL extends LabTest {
    public HDL() {
        super("HDL", "mg/dL", 200.00, "Lipid Panel");
        this.maleRange   = new ReferenceRange(35.0, 80.0);
        this.femaleRange = new ReferenceRange(42.0, 88.0);
    }
}

class LDL extends LabTest {
    public LDL() {
        super("LDL", "mg/dL", 200.00, "Lipid Panel");
        this.maleRange   = new ReferenceRange(50.0, 130.0);
        this.femaleRange = new ReferenceRange(50.0, 130.0);
    }
}

class Triglycerides extends LabTest {
    public Triglycerides() {
        super("Triglycerides", "mg/dL", 200.00, "Lipid Panel");
        this.maleRange   = new ReferenceRange(60.0, 165.0);
        this.femaleRange = new ReferenceRange(40.0, 140.0);
    }
}

class Creatinine extends LabTest {
    public Creatinine() {
        super("Creatinine", "mg/dL", 180.00, "Kidney");
        this.maleRange   = new ReferenceRange(0.9, 1.3);
        this.femaleRange = new ReferenceRange(0.6, 1.2);
    }
}

class UricAcid extends LabTest {
    public UricAcid() {
        super("Uric Acid", "mg/dL", 180.00, "Kidney");
        this.maleRange   = new ReferenceRange(3.5, 7.2);
        this.femaleRange = new ReferenceRange(2.6, 6.0);
    }
}

class BUN extends LabTest {
    public BUN() {
        super("BUN", "mg/dL", 180.00, "Kidney");
        this.maleRange   = new ReferenceRange(6.0, 20.0);
        this.femaleRange = new ReferenceRange(6.0, 20.0);
    }
}

class AST extends LabTest {
    public AST() {
        super("AST/SGOT", "U/L", 190.00, "Liver");
        this.maleRange   = new ReferenceRange(null, 46.0);
        this.femaleRange = new ReferenceRange(null, 46.0);
    }
}

class ALT extends LabTest {
    public ALT() {
        super("ALT/SGPT", "U/L", 190.00, "Liver");
        this.maleRange   = new ReferenceRange(null, 49.0);
        this.femaleRange = new ReferenceRange(null, 49.0);
    }
}

class Sodium extends LabTest {
    public Sodium() {
        super("Sodium", "mEq/L", 160.00, "Electrolytes");
        this.maleRange   = new ReferenceRange(135.0, 145.0, 120.0, 160.0);
        this.femaleRange = new ReferenceRange(135.0, 145.0, 120.0, 160.0);
    }
}

class Potassium extends LabTest {
    public Potassium() {
        super("Potassium", "mEq/L", 160.00, "Electrolytes");
        this.maleRange   = new ReferenceRange(3.5, 5.0, 2.5, 6.5);
        this.femaleRange = new ReferenceRange(3.5, 5.0, 2.5, 6.5);
    }
}

class Chloride extends LabTest {
    public Chloride() {
        super("Chloride", "mEq/L", 160.00, "Electrolytes");
        this.maleRange   = new ReferenceRange(96.0, 110.0);
        this.femaleRange = new ReferenceRange(96.0, 110.0);
    }
}

class TotalCalcium extends LabTest {
    public TotalCalcium() {
        super("Total Calcium", "mg/dL", 170.00, "Electrolytes");
        this.maleRange   = new ReferenceRange(8.6, 10.28, 6.0, 13.0);
        this.femaleRange = new ReferenceRange(8.6, 10.28, 6.0, 13.0);
    }
}

class IonizedCalcium extends LabTest {
    public IonizedCalcium() {
        super("Ionized Calcium", "mg/dL", 190.00, "Electrolytes");
        this.maleRange   = new ReferenceRange(4.4, 5.2);
        this.femaleRange = new ReferenceRange(4.4, 5.2);
    }
}