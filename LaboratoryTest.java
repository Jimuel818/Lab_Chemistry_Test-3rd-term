import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LaboratoryTest extends JFrame {

    RecordManager recordManager = new RecordManager();
    List<LabTest> currentTests = new ArrayList<>();
    List<JTextField> resultFields = new ArrayList<>();

    // UI
    JTextField idIn, nameIn, dobIn, ageIn, contactIn;
    JComboBox<String> sexIn, typeIn, specimenIn, fastingIn, packageIn;
    JList<String> testList;
    DefaultListModel<String> testListModel;
    JPanel resultPanel;
    JTextArea repArea;
    JLabel totalPriceLabel;

    DefaultTableModel historyModel;
    JTable historyTable;

    static final String[] INDIVIDUAL_TEST_NAMES = {
        "FBS", "RBS", "Total Cholesterol", "HDL", "LDL", "Triglycerides",
        "Creatinine", "Uric Acid", "BUN", "AST/SGOT", "ALT/SGPT",
        "Sodium", "Potassium", "Chloride", "Total Calcium", "Ionized Calcium"
    };

    static final String[] PACKAGE_NAMES = {
        "-- Select Package --", "Lipid Panel (P650)", "Kidney Function (P450)",
        "Liver Function (P350)", "Electrolyte Panel (P550)"
    };

    static final Color BG      = new Color(18, 18, 24),
                       SURFACE = new Color(30, 30, 40),
                       BORDER  = new Color(55, 55, 75),
                       FG      = new Color(225, 225, 235),
                       ACCENT  = new Color(70, 130, 230),
                       DANGER  = new Color(210, 70, 70),
                       SUCCESS = new Color(60, 170, 90);

    public LaboratoryTest() {
        setTitle("Clinical Laboratory Information System (LIS) Dashboard");
        setSize(1280, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG);

        JPanel mainDashboard = new JPanel(new GridLayout(1, 3, 10, 0));
        mainDashboard.setBackground(BG);
        mainDashboard.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainDashboard.add(buildPatientColumn());
        mainDashboard.add(buildTestColumn());
        mainDashboard.add(buildHistoryColumn());

        add(mainDashboard, BorderLayout.CENTER);
        add(buildBottomReportArea(), BorderLayout.SOUTH);

        idIn.setText(recordManager.generatePatientID());
    }
    //input patient column AND UI

    private JPanel buildPatientColumn() {
        JPanel col = columnPanel("1. PATIENT & SPECIMEN");
        JPanel form = darkPanel(new GridLayout(0, 1, 5, 5));
        form.add(label("Patient ID (Auto)")); idIn = darkField(10); idIn.setEditable(false); form.add(idIn);
        form.add(label("Full Name")); nameIn = darkField(15); form.add(nameIn);
        form.add(label("Date of Birth (YYYY-MM-DD)")); dobIn = darkField(10); form.add(dobIn);
        form.add(label("Age")); ageIn = darkField(3); form.add(ageIn);
        form.add(label("Sex")); sexIn = darkCombo(new String[]{"Male", "Female"}); form.add(sexIn);
        form.add(label("Contact Number")); contactIn = darkField(12); form.add(contactIn);
        form.add(label("Patient Type")); typeIn = darkCombo(new String[]{"Outpatient", "Inpatient", "ER"}); form.add(typeIn);
        form.add(label("Specimen Type")); specimenIn = darkCombo(new String[]{"Serum", "Plasma", "Whole Blood", "Urine"}); form.add(specimenIn);
        form.add(label("Fasting Status")); fastingIn = darkCombo(new String[]{"Fasting", "Non-fasting", "Not indicated"}); form.add(fastingIn);
        col.add(form, BorderLayout.CENTER);
        JButton newPatientBtn = successButton("NEW PATIENT");
        newPatientBtn.addActionListener(e -> resetForNewPatient());
        col.add(newPatientBtn, BorderLayout.SOUTH);
        return col;
    }
    //testing column and UI

    private JPanel buildTestColumn() {
        JPanel col = columnPanel("2. TEST SELECTION");
        testListModel = new DefaultListModel<>();
        for (String n : INDIVIDUAL_TEST_NAMES) testListModel.addElement(n);
        testList = new JList<>(testListModel);
        testList.setBackground(SURFACE); testList.setForeground(FG);
        testList.setSelectionBackground(ACCENT);
        JScrollPane listScroll = new JScrollPane(testList);
        styleScroll(listScroll);
        JPanel top = darkPanel(new BorderLayout(0, 5));
        top.add(label("Select Individual Tests:"), BorderLayout.NORTH);
        top.add(listScroll, BorderLayout.CENTER);
        JPanel mid = darkPanel(new BorderLayout(0, 5));
        mid.add(label("Or Select Package:"), BorderLayout.NORTH);
        packageIn = darkCombo(PACKAGE_NAMES);
        mid.add(packageIn, BorderLayout.CENTER);
        JButton loadBtn = accentButton("LOAD SELECTED TESTS");
        loadBtn.addActionListener(e -> loadTests());
        mid.add(loadBtn, BorderLayout.SOUTH);
        resultPanel = darkPanel(new GridLayout(0, 1, 2, 2));
        JScrollPane resScroll = new JScrollPane(resultPanel);
        styleScroll(resScroll);
        JPanel center = darkPanel(new BorderLayout(0, 5));
        center.add(label("Enter Results:"), BorderLayout.NORTH);
        center.add(resScroll, BorderLayout.CENTER);
        col.add(top, BorderLayout.NORTH);
        col.add(center, BorderLayout.CENTER);
        col.add(mid, BorderLayout.SOUTH);
        return col;
    }
    //history column and ui

    private JPanel buildHistoryColumn() {
        JPanel col = columnPanel("3. RECORDS HISTORY");
        String[] cols = {"ID", "Patient Name", "Date", "Status"};
        historyModel = new DefaultTableModel(cols, 0);
        historyTable = new JTable(historyModel);
        historyTable.setBackground(SURFACE); historyTable.setForeground(FG);
        historyTable.setGridColor(BORDER);
        historyTable.setSelectionBackground(ACCENT);
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && historyTable.getSelectedRow() != -1) {
                viewSelectedRecord(historyTable.getSelectedRow());
            }
        });
        JScrollPane scroll = new JScrollPane(historyTable);
        styleScroll(scroll);
        col.add(scroll, BorderLayout.CENTER);

        // button for history column and ui
        JButton refreshBtn = accentButton("REFRESH RECORDS");
        refreshBtn.addActionListener(e -> refreshHistoryTable());
        col.add(refreshBtn, BorderLayout.SOUTH);
        return col;
    }
    //button and ui for creating new patient or clear 
    private JPanel buildBottomReportArea() {
        JPanel p = darkPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(0, 15, 15, 15));
        JPanel header = darkPanel(new BorderLayout());
        totalPriceLabel = new JLabel("  Total: P0.00");
        totalPriceLabel.setForeground(ACCENT); totalPriceLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        header.add(totalPriceLabel, BorderLayout.WEST);
        JPanel btns = darkPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton runBtn    = successButton("GENERATE & SAVE REPORT");
        JButton newTestBtn = accentButton("NEW TEST (SAME PATIENT)");
        JButton clrBtn    = dangerButton("CLEAR ALL");
        runBtn.addActionListener(e -> generateReport());
        newTestBtn.addActionListener(e -> resetForNewTest());
        clrBtn.addActionListener(e -> clearAll());
        btns.add(newTestBtn); btns.add(runBtn); btns.add(clrBtn);
        header.add(btns, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);
        repArea = new JTextArea(12, 80);
        repArea.setEditable(false); repArea.setBackground(SURFACE); repArea.setForeground(FG);
        repArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(repArea);
        styleScroll(scroll);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void loadTests() {
        currentTests.clear(); resultFields.clear(); resultPanel.removeAll();

        // this block of code prevents duplication of test
        List<String> addedTestNames = new ArrayList<>();

        for (int i : testList.getSelectedIndices()) {
            String testName = INDIVIDUAL_TEST_NAMES[i];
            if (!addedTestNames.contains(testName)) {
                LabTest t = makeTest(testName);
                if (t != null) {
                    currentTests.add(t);
                    addedTestNames.add(testName);
                }
            }
        }

        int pkgIdx = packageIn.getSelectedIndex();
        if (pkgIdx > 0) {
            TestPackage pkg = makePackage(pkgIdx);
            if (pkg != null) {
                for (LabTest t : pkg.getTests()) {
                    if (!addedTestNames.contains(t.testName)) {
                        currentTests.add(t);
                        addedTestNames.add(t.testName);
                    }
                }
               
            }
        }

        for (LabTest t : currentTests) {
            JPanel row = darkPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel lbl = new JLabel(String.format("%-20s (%s)", t.testName, t.unit));
            lbl.setForeground(FG); JTextField f = darkField(8);
            resultFields.add(f); row.add(lbl); row.add(f);
            resultPanel.add(row);
        }
        updatePrice(); resultPanel.revalidate(); resultPanel.repaint();
    }

    private void generateReport() {
        if (currentTests.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tests loaded. Please load tests first.");
            return;
        }
        try {
            if (nameIn.getText().trim().isEmpty()) throw new Exception("Name is required.");
            if (ageIn.getText().trim().isEmpty()) throw new Exception("Age is required.");

            Patient p = new Patient(
                idIn.getText(), nameIn.getText(), dobIn.getText(),
                Integer.parseInt(ageIn.getText().trim()),
                (String) sexIn.getSelectedItem(),
                contactIn.getText(),
                (String) typeIn.getSelectedItem()
            );
            String accNum = recordManager.generateAccessionNumber();
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Accession acc = new Accession(accNum, p, time);

            for (int i = 0; i < currentTests.size(); i++) {
                LabTest t = currentTests.get(i);
                String val = resultFields.get(i).getText().trim();
                if (val.isEmpty()) throw new Exception("Result missing for: " + t.testName);
                t.setResult(Double.parseDouble(val));
                t.specimenType  = (String) specimenIn.getSelectedItem();
                t.fastingStatus = (String) fastingIn.getSelectedItem();
                acc.addTest(t);
            }
            acc.updateStatus();
            recordManager.addAccession(acc);
            viewSelectedRecord(recordManager.getAccessions().size() - 1);
            historyModel.addRow(new Object[]{acc.accessionNumber, p.name, time, acc.status});

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number in Age or a result field. Please check inputs.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void viewSelectedRecord(int row) {
        Accession acc = recordManager.getAccessions().get(row);

        String specimen = acc.tests.isEmpty() ? "N/A" : acc.tests.get(0).specimenType;
        String fasting  = acc.tests.isEmpty() ? "N/A" : acc.tests.get(0).fastingStatus;
        //printing the report of the patient
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append("                      CLINICAL LABORATORY FINAL REPORT                          \n");
        sb.append("================================================================================\n");
        sb.append(String.format(" Accession #: %-20s  Date/Time: %s\n", acc.accessionNumber, acc.timestamp));
        sb.append(String.format(" Patient ID : %-20s  Patient Name: %s\n", acc.patient.patientID, acc.patient.name));
        sb.append(String.format(" Age / Sex  : %d / %-15s  DOB: %s\n", acc.patient.age, acc.patient.sex, acc.patient.dob));
        sb.append(String.format(" Specimen   : %-20s  Fasting: %s\n", specimen, fasting));
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format(" %-20s %10s %-10s %-25s %s\n", "TEST NAME", "RESULT", "UNIT", "REFERENCE RANGE", "STATUS"));
        sb.append("--------------------------------------------------------------------------------\n");
        for (LabTest t : acc.tests) sb.append(t.getFormattedResult(acc.patient.sex)).append("\n");
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format(" REPORT STATUS: %-20s  TOTAL PRICE: P%.2f\n", acc.status, acc.getTotalPrice()));
        sb.append("================================================================================\n");
        repArea.setText(sb.toString());
    }

    private void refreshHistoryTable() {
        historyModel.setRowCount(0);
        for (Accession a : recordManager.getAccessions()) {
            historyModel.addRow(new Object[]{a.accessionNumber, a.patient.name, a.timestamp, a.status});
        }
    }

    private void resetForNewPatient() {
        clearAll();
        idIn.setText(recordManager.generatePatientID());
    }

    private void resetForNewTest() {
        testList.clearSelection();
        packageIn.setSelectedIndex(0);
        resultPanel.removeAll();
        resultFields.clear();
        currentTests.clear();
        updatePrice();
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private void clearAll() {
        nameIn.setText(""); dobIn.setText(""); ageIn.setText(""); contactIn.setText("");
        resetForNewTest();
        repArea.setText("");
        idIn.setText(recordManager.generatePatientID());
    }

    private void updatePrice() {
        double total = 0;
        for (LabTest t : currentTests) total += t.price;
        totalPriceLabel.setText("  Total: P" + String.format("%.2f", total));
    }

    private LabTest makeTest(String name) {
        switch (name) {
            case "FBS":             
             return new FastingBloodSugar();
            case "RBS":             
             return new RandomBloodSugar();
            case "Total Cholesterol":
                return new TotalCholesterol();
            case "HDL":              
            return new HDL();
            case "LDL":              
            return new LDL();
            case "Triglycerides":    
            return new Triglycerides();
            case "Creatinine":       
            return new Creatinine();
            case "Uric Acid":        
            return new UricAcid();
            case "BUN":              
            return new BUN();
            case "AST/SGOT":         
            return new AST();
            case "ALT/SGPT":         
            return new ALT();
            case "Sodium":           
            return new Sodium();
            case "Potassium":        
            return new Potassium();
            case "Chloride":         
            return new Chloride();
            case "Total Calcium":    
            return new TotalCalcium();
            case "Ionized Calcium":  
            return new IonizedCalcium();
            default:                 
            return null;
        }
    }

    private TestPackage makePackage(int idx) {
        switch (idx) {
            case 1: 
            return new LipidPanel();
            case 2: 
            return new KidneyFunctionPanel();
            case 3:
            return new LiverFunctionPanel();
            default:
             return null;
        }
    }

    //  UI column pannel
    private JPanel columnPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER), new EmptyBorder(10, 10, 10, 10)));
        JLabel l = new JLabel(title);
        l.setForeground(ACCENT); l.setFont(new Font("SansSerif", Font.BOLD, 14));
        p.add(l, BorderLayout.NORTH);
        return p;
    }

    private JPanel darkPanel(LayoutManager l) {
        JPanel p = new JPanel(l); p.setBackground(BG); return p;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(150, 150, 170));
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        return l;
    }

    private JTextField darkField(int c) {
        JTextField f = new JTextField(c);
        f.setBackground(SURFACE); f.setForeground(FG); f.setCaretColor(FG);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER), new EmptyBorder(4, 4, 4, 4)));
        return f;
    }

    private JComboBox<String> darkCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setBackground(SURFACE); c.setForeground(FG); return c;
    }

    private void styleScroll(JScrollPane s) {
        s.setBorder(BorderFactory.createLineBorder(BORDER));
        s.getViewport().setBackground(BG);
    }

    private JButton accentButton(String t) {
        JButton b = new JButton(t); b.setBackground(ACCENT);
        b.setForeground(Color.WHITE); b.setFocusPainted(false); return b;
    }

    private JButton successButton(String t) {
        JButton b = new JButton(t); b.setBackground(SUCCESS);
        b.setForeground(Color.WHITE); b.setFocusPainted(false); return b;
    }

    private JButton dangerButton(String t) {
        JButton b = new JButton(t); b.setBackground(DANGER);
        b.setForeground(Color.WHITE); b.setFocusPainted(false); return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LaboratoryTest().setVisible(true));
    }
}