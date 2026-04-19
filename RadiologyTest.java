public abstract class RadiologyTest {
    String testName;
    String modality;       // X-Ray, Ultrasound
    String bodyPart;       // Chest, KUB, Abdomen
    String technique;      
    String findings;       
    String impression;     
    double price;
    String status;         
    String contrastUsed;   

    public RadiologyTest(String testName, String modality, String bodyPart,
                         String technique, double price) {
        this.testName    = testName;
        this.modality    = modality;
        this.bodyPart    = bodyPart;
        this.technique   = technique;
        this.price       = price;
        this.status      = "Pending";
        this.contrastUsed = "No";
        this.findings    = "";
        this.impression  = "";
    }

    // Caradiologist/tech encodes findings + impression
    public void setReport(String findings, String impression) {
        this.findings   = findings;
        this.impression = impression;
        this.status     = "Final";
    }

    //formatted report block 
    public String getFormattedReport() {
        return String.format(
            "  %-26s  Modality: %-12s  Technique: %s\n"
          + "  Findings  : %s\n"
          + "  Impression: %s\n",
            testName, modality, technique,
            findings.isEmpty()   ? "(pending)"   : findings,
            impression.isEmpty() ? "(pending)"   : impression
        );
    }
}