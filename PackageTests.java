// sub classes for package test 

class LipidPanel extends TestPackage {
    public LipidPanel() {
        super("Lipid Panel",
              650.00,
              new LabTest[]{new TotalCholesterol(), new HDL(), new LDL(), new Triglycerides()});
    }
}

class KidneyFunctionPanel extends TestPackage {
    public KidneyFunctionPanel() {
        super("Kidney Function Panel",
              450.00,
              new LabTest[]{new Creatinine(), new UricAcid(), new BUN()});
    }
}

class LiverFunctionPanel extends TestPackage {
    public LiverFunctionPanel() {
        super("Liver Function Panel",
              350.00,
              new LabTest[]{new AST(), new ALT()});
    }
}
