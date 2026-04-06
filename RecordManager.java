import java.util.ArrayList;
import java.util.List;

public class RecordManager {
    private List<Accession> accessions;
    private int nextAccessionID = 1;
    private int nextPatientID = 1001;

    public RecordManager() {
        accessions = new ArrayList<>();
    }

    public void addAccession(Accession a) {
        accessions.add(a);
    }

    public List<Accession> getAccessions() {
        return accessions;
    }

    public String generatePatientID() {
        return "PID-" + (nextPatientID++);
    }

    public String generateAccessionNumber() {
        String date = java.time.LocalDate.now().toString().replace("-", "");
        return "ACC-" + date + "-" + String.format("%04d", nextAccessionID++);
    }

    public List<Accession> searchByPatientID(String patientID) {
        List<Accession> results = new ArrayList<>();
        for (Accession a : accessions) {
            if (a.patient.patientID.equalsIgnoreCase(patientID)) {
                results.add(a);
            }
        }
        return results;
    }

    public List<Accession> searchByPatientName(String name) {
        List<Accession> results = new ArrayList<>();
        for (Accession a : accessions) {
            if (a.patient.name.toLowerCase().contains(name.toLowerCase())) {
                results.add(a);
            }
        }
        return results;
    }
}