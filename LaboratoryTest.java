import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LaboratoryTest extends JFrame {

    RecordManager recordManager = new RecordManager();
    List<LabTest> currentTests = new ArrayList<>();
    List<JTextField> resultFields = new ArrayList<>();
    double packagePrice = 0;

    // User interface
    JTextField idIn, nameIn, dobIn, ageIn, contactIn;
    JComboBox<String> sexIn, typeIn, specimenIn, fastingIn, packageIn;
    JPanel resultPanel;
    JTextArea repArea;
    JLabel totalPriceLabel;

    // Checkbox dropdown for individual test selection
    JButton testDropdownBtn;
    JCheckBox[] testCheckBoxes;
    JPopupMenu testPopup;

    DefaultTableModel historyModel;
    JTable historyTable;

    //  RAD user interface
    JList<String> radiologyTestList;
    DefaultListModel<String> radiologyListModel;
    List<RadiologyTest> currentRadiologyTests = new ArrayList<>();
    List<JTextField> radiologyFindingsFields  = new ArrayList<>();
    List<JTextField> radiologyImpressionFields = new ArrayList<>();
    JPanel radiologyResultPanel;
    JLabel radiologyPriceLabel;

    static final String[] INDIVIDUAL_TEST_NAMES = {
        "FBS", "RBS", "Total Cholesterol", "HDL", "LDL", "Triglycerides",
        "Creatinine", "Uric Acid", "BUN", "AST/SGOT", "ALT/SGPT",
        "Sodium", "Potassium", "Chloride", "Total Calcium", "Ionized Calcium",
        "Alkaline Phosphatase", "Globulin", "Phosphorus"
    };

    static final String[] PACKAGE_NAMES = {
        "-- Select Package --", "Lipid Panel (P650)", "Kidney Function (P450)",
        "Liver Function (P350)" //, "Electrolyte Panel (P550)"
    };

    // Names shown in the radiology selection list
    static final String[] RADIOLOGY_TEST_NAMES = {
        "Chest X-Ray (PA View)",
        "Whole Abdomen Ultrasound",
        "KUB X-Ray"
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

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG);
        tabs.setForeground(FG);

        JPanel labDashboard = new JPanel(new GridLayout(1, 3, 10, 0));
        labDashboard.setBackground(BG);
        labDashboard.setBorder(new EmptyBorder(15, 15, 15, 15));

        labDashboard.add(buildPatientColumn());
        labDashboard.add(buildTestColumn());
        labDashboard.add(buildHistoryColumn());

        JPanel labTab = new JPanel(new BorderLayout(0, 0));
        labTab.setBackground(BG);
        labTab.add(labDashboard, BorderLayout.CENTER);
        labTab.add(buildBottomReportArea(), BorderLayout.SOUTH);

        tabs.addTab("Laboratory", labTab);
        tabs.addTab("Radiology", buildRadiologyTab());

        add(tabs, BorderLayout.CENTER);

        idIn.setText(recordManager.generatePatientID());
    }
    //input patient column AND UI

    private JPanel buildPatientColumn() {
        JPanel col = columnPanel("PATIENT & SPECIMEN");

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.setBackground(BG);

        form.add(label("Patient ID (Auto)"));
        idIn = darkField(10); idIn.setEditable(false); form.add(idIn);

        form.add(label("Full Name"));
        nameIn = darkField(15); form.add(nameIn);

        form.add(label("Date of Birth (YYYY-MM-DD)"));
        dobIn = darkField(10); form.add(dobIn);

        form.add(label("Age"));
        ageIn = darkField(3); form.add(ageIn);

        form.add(label("Sex"));
        sexIn = darkCombo(new String[]{"Male", "Female"}); form.add(sexIn);

        form.add(label("Contact Number"));
        contactIn = darkField(12); form.add(contactIn);

        form.add(label("Patient Type"));
        typeIn = darkCombo(new String[]{"Outpatient", "Inpatient", "ER"}); form.add(typeIn);

        form.add(label("Specimen Type"));
        specimenIn = darkCombo(new String[]{"Serum", "Plasma", "Whole Blood", "Urine"}); form.add(specimenIn);

        form.add(label("Fasting Status"));
        fastingIn = darkCombo(new String[]{"Fasting", "Non-fasting", "Not indicated"}); form.add(fastingIn);

        // FIX: wrap in scroll pane so fields are never clipped
        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.getViewport().setBackground(BG);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        col.add(formScroll, BorderLayout.CENTER);

        JButton newPatientBtn = successButton("NEW PATIENT");
        newPatientBtn.addActionListener(e -> resetForNewPatient());
        col.add(newPatientBtn, BorderLayout.SOUTH);
        return col;
    }
    //testing column and UI

    private JPanel buildTestColumn() {
        JPanel col = columnPanel("TEST SELECTION");

        // FIX: Checkbox dropdown — button shows/hides a popup with checkboxes
        testCheckBoxes = new JCheckBox[INDIVIDUAL_TEST_NAMES.length];
        testPopup = new JPopupMenu();
        testPopup.setBackground(SURFACE);
        testPopup.setBorder(BorderFactory.createLineBorder(BORDER));

        // "Select All" / "Clear All" row at the top of popup
        JPanel popupHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        popupHeader.setBackground(SURFACE);
        JButton selAllBtn = new JButton("Select All");
        JButton clrAllBtn = new JButton("Clear All");
        selAllBtn.setBackground(ACCENT); selAllBtn.setForeground(Color.WHITE);
        selAllBtn.setFocusPainted(false); selAllBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        clrAllBtn.setBackground(DANGER); clrAllBtn.setForeground(Color.WHITE);
        clrAllBtn.setFocusPainted(false); clrAllBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        selAllBtn.addActionListener(e -> { for (JCheckBox cb : testCheckBoxes) cb.setSelected(true); updateDropdownLabel(); });
        clrAllBtn.addActionListener(e -> { for (JCheckBox cb : testCheckBoxes) cb.setSelected(false); updateDropdownLabel(); });
        popupHeader.add(selAllBtn);
        popupHeader.add(clrAllBtn);
        testPopup.add(popupHeader);
        testPopup.addSeparator();

        // Panel that holds all checkboxes
        JPanel checkboxContainer = new JPanel();
        checkboxContainer.setLayout(new BoxLayout(checkboxContainer, BoxLayout.Y_AXIS));
        checkboxContainer.setBackground(SURFACE);

        // Add checkboxes
        for (int i = 0; i < INDIVIDUAL_TEST_NAMES.length; i++) {
            testCheckBoxes[i] = new JCheckBox(INDIVIDUAL_TEST_NAMES[i]);
            testCheckBoxes[i].setBackground(SURFACE);
            testCheckBoxes[i].setForeground(FG);
            testCheckBoxes[i].setFont(new Font("SansSerif", Font.PLAIN, 12));

            testCheckBoxes[i].addActionListener(e -> updateDropdownLabel());

            checkboxContainer.add(testCheckBoxes[i]);
        }

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(checkboxContainer);
        scrollPane.setPreferredSize(new Dimension(250, 200)); // controls visible height
        scrollPane.setBorder(null);

        // Add to popup
        testPopup.add(scrollPane);

        // Dropdown trigger button
        testDropdownBtn = new JButton("Select Individual Tests ▼");
        testDropdownBtn.setBackground(SURFACE);
        testDropdownBtn.setForeground(FG);
        testDropdownBtn.setFocusPainted(false);
        testDropdownBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER), new EmptyBorder(5, 8, 5, 8)));
        testDropdownBtn.addActionListener(e -> {
            testPopup.setPreferredSize(new Dimension(testDropdownBtn.getWidth(), 320));
            testPopup.show(testDropdownBtn, 0, testDropdownBtn.getHeight());
        });

        JPanel top = darkPanel(new BorderLayout(0, 5));
        top.add(label("Select Individual Tests:"), BorderLayout.NORTH);
        top.add(testDropdownBtn, BorderLayout.CENTER);

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
        JPanel col = columnPanel("RECORDS HISTORY");
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

    // Updates the dropdown button label to show how many tests are checked
    private void updateDropdownLabel() {
        int count = 0;
        for (JCheckBox cb : testCheckBoxes) if (cb.isSelected()) count++;
        testDropdownBtn.setText(count == 0 ? "Select Individual Tests ▼" : count + " test(s) selected ▼");
    }

    private void loadTests() {
        currentTests.clear(); resultFields.clear(); resultPanel.removeAll();
        packagePrice = 0;

        // this block of code prevents duplication of test
        List<String> addedTestNames = new ArrayList<>();

        // Read from checkboxes instead of JList
        for (int i = 0; i < testCheckBoxes.length; i++) {
            if (testCheckBoxes[i].isSelected()) {
                String testName = INDIVIDUAL_TEST_NAMES[i];
                if (!addedTestNames.contains(testName)) {
                    LabTest t = makeTest(testName);
                    if (t != null) {
                        currentTests.add(t);
                        addedTestNames.add(testName);
                    }
                }
            }
        }
        ////
        int pkgIdx = packageIn.getSelectedIndex();
        if (pkgIdx > 0) {
            TestPackage pkg = makePackage(pkgIdx);
            if (pkg != null) {
                packagePrice = pkg.packagePrice;
                for (LabTest t : pkg.getTests()) {
                    if (!addedTestNames.contains(t.testName)) {
                        t.fromPackage = true;
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
            if (nameIn.getText().trim().isEmpty())
                 throw new Exception("Name is required.");
            if (ageIn.getText().trim().isEmpty()) 
                throw new Exception("Age is required.");

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
            acc.packagePrice = packagePrice;
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
        for (JCheckBox cb : testCheckBoxes) cb.setSelected(false);
        updateDropdownLabel();
        packageIn.setSelectedIndex(0);
        resultPanel.removeAll();
        resultFields.clear();
        currentTests.clear();
        packagePrice = 0;
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
        double total = packagePrice;
        if (packagePrice == 0) {
            for (LabTest t : currentTests) total += t.price;
        } else {
            int pkgIdx = packageIn.getSelectedIndex();
            TestPackage pkg = pkgIdx > 0 ? makePackage(pkgIdx) : null;
            List<String> pkgTestNames = new ArrayList<>();
            if (pkg != null) for (LabTest t : pkg.getTests()) pkgTestNames.add(t.testName);
            for (LabTest t : currentTests) {
                if (!pkgTestNames.contains(t.testName)) total += t.price;
            }
        }
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
            case "Alkaline Phosphatase":
            return new AlkalinePhosphatase();
            case "Globulin":
            return new Globulin();
            case "Phosphorus":
            return new Phosphorus();
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

    private JPanel buildRadiologyTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 0));
        tab.setBackground(BG);

        JPanel dashboard = new JPanel(new GridLayout(1, 2, 10, 0));
        dashboard.setBackground(BG);
        dashboard.setBorder(new EmptyBorder(15, 15, 10, 15));

        dashboard.add(buildRadiologySelectionColumn());
        dashboard.add(buildRadiologyReportInputColumn());

        tab.add(dashboard, BorderLayout.CENTER);
        tab.add(buildRadiologyBottomArea(), BorderLayout.SOUTH);
        return tab;
    }

    private JPanel buildRadiologySelectionColumn() {
        JPanel col = columnPanel("RADIOLOGY EXAM SELECTION");

        JPanel infoNote = darkPanel(new GridLayout(0, 1, 3, 3));
        infoNote.add(label("* Patient details are entered in the Laboratory tab."));
        infoNote.add(label("  Fill in patient info there before generating a radiology report."));
        col.add(infoNote, BorderLayout.NORTH);

        // Radiology exam list
        radiologyListModel = new DefaultListModel<>();
        for (String n : RADIOLOGY_TEST_NAMES) radiologyListModel.addElement(n);
        radiologyTestList = new JList<>(radiologyListModel);
        radiologyTestList.setBackground(SURFACE);
        radiologyTestList.setForeground(FG);
        radiologyTestList.setSelectionBackground(ACCENT);

        JScrollPane listScroll = new JScrollPane(radiologyTestList);
        styleScroll(listScroll);

        JPanel listWrapper = darkPanel(new BorderLayout(0, 5));
        listWrapper.add(label("Select Radiology Exam(s):"), BorderLayout.NORTH);
        listWrapper.add(listScroll, BorderLayout.CENTER);

        JButton loadBtn = accentButton("LOAD SELECTED EXAMS");
        loadBtn.addActionListener(e -> loadRadiologyTests());
        listWrapper.add(loadBtn, BorderLayout.SOUTH);

        col.add(listWrapper, BorderLayout.CENTER);
        return col;
    }

    private JPanel buildRadiologyReportInputColumn() {
        JPanel col = columnPanel("FINDINGS & IMPRESSION");

        radiologyResultPanel = darkPanel(new GridLayout(0, 1, 4, 4));
        JScrollPane scroll = new JScrollPane(radiologyResultPanel);
        styleScroll(scroll);

        col.add(label("Enter Findings and Impression per exam:"), BorderLayout.NORTH);
        col.add(scroll, BorderLayout.CENTER);
        return col;
    }

    private JPanel buildRadiologyBottomArea() {
        JPanel p = darkPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(0, 15, 15, 15));

        JPanel header = darkPanel(new BorderLayout());

        radiologyPriceLabel = new JLabel("  Radiology Total: P0.00");
        radiologyPriceLabel.setForeground(ACCENT);
        radiologyPriceLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        header.add(radiologyPriceLabel, BorderLayout.WEST);

        JPanel btns = darkPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton genBtn  = successButton("GENERATE RADIOLOGY REPORT");
        JButton clrBtn  = dangerButton("CLEAR RADIOLOGY");
        genBtn.addActionListener(e -> generateRadiologyReport());
        clrBtn.addActionListener(e -> clearRadiology());
        btns.add(genBtn);
        btns.add(clrBtn);
        header.add(btns, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        JTextArea radRepArea = new JTextArea(10, 80);
        radRepArea.setName("radRepArea");
        radRepArea.setEditable(false);
        radRepArea.setBackground(SURFACE);
        radRepArea.setForeground(FG);
        radRepArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(radRepArea);
        styleScroll(scroll);
        // Store reference so generateRadiologyReport() can write into it
        this.radReportArea = radRepArea;
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // Separate text area for the radiology report display
    JTextArea radReportArea;

    private void loadRadiologyTests() {
        currentRadiologyTests.clear();
        radiologyFindingsFields.clear();
        radiologyImpressionFields.clear();
        radiologyResultPanel.removeAll();

        for (int i : radiologyTestList.getSelectedIndices()) {
            RadiologyTest rt = makeRadiologyTest(RADIOLOGY_TEST_NAMES[i]);
            if (rt == null) continue;
            currentRadiologyTests.add(rt);

            JLabel examLabel = new JLabel("  " + rt.testName
                + "  |  " + rt.modality
                + "  |  Technique: " + rt.technique
                + "  |  P" + String.format("%.2f", rt.price));
            examLabel.setForeground(ACCENT);
            examLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            radiologyResultPanel.add(examLabel);

            // Findings row
            JPanel findingsRow = darkPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel fLbl = new JLabel("Findings   :");
            fLbl.setForeground(FG);
            JTextField fField = darkField(40);
            fField.setPreferredSize(new Dimension(380, 24));
            radiologyFindingsFields.add(fField);
            findingsRow.add(fLbl);
            findingsRow.add(fField);
            radiologyResultPanel.add(findingsRow);

            // Impression row
            JPanel impressionRow = darkPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel iLbl = new JLabel("Impression :");
            iLbl.setForeground(FG);
            JTextField iField = darkField(40);
            iField.setPreferredSize(new Dimension(380, 24));
            radiologyImpressionFields.add(iField);
            impressionRow.add(iLbl);
            impressionRow.add(iField);
            radiologyResultPanel.add(impressionRow);

            JLabel sep = new JLabel("  ---------------------------------------------------------------");
            sep.setForeground(BORDER);
            radiologyResultPanel.add(sep);
        }

        updateRadiologyPrice();
        radiologyResultPanel.revalidate();
        radiologyResultPanel.repaint();
    }

    private RadiologyTest makeRadiologyTest(String name) {
        switch (name) {
            case "Chest X-Ray (PA View)":    
              return new ChestXray();
            case "Whole Abdomen Ultrasound": 
              return new WholeAbdomenUltrasound();
            case "KUB X-Ray":        
                      return new KUBXray();
            default:           
                     return null;
        }
    }

    private void generateRadiologyReport() {
        if (currentRadiologyTests.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No radiology exams loaded. Please select and load exams first.");
            return;
        }
        try {
            if (nameIn.getText().trim().isEmpty()) 
                throw new Exception("Patient name is required. Fill in the Laboratory tab first.");
            if (ageIn.getText().trim().isEmpty()) 
                throw new Exception("Patient age is required. Fill in the Laboratory tab first.");

            for (int i = 0; i < currentRadiologyTests.size(); i++) {
                String findings   = radiologyFindingsFields.get(i).getText().trim();
                String impression = radiologyImpressionFields.get(i).getText().trim();
                if (findings.isEmpty())   
                    throw new Exception("Findings missing for: " + currentRadiologyTests.get(i).testName);
                if (impression.isEmpty()) 
                    throw new Exception("Impression missing for: " + currentRadiologyTests.get(i).testName);
                currentRadiologyTests.get(i).setReport(findings, impression);
            }

            String patientID   = idIn.getText();
            String patientName = nameIn.getText();
            String dob         = dobIn.getText();
            String age         = ageIn.getText().trim();
            String sex         = (String) sexIn.getSelectedItem();
            String time        = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Compute total radiology price
            double radTotal = 0;
            for (RadiologyTest rt : currentRadiologyTests) 
                radTotal += rt.price;

            // Disyplay radiology report
            StringBuilder sb = new StringBuilder();
            sb.append("================================================================================\n");
            sb.append("                       RADIOLOGY DEPARTMENT REPORT                             \n");
            sb.append("================================================================================\n");
            sb.append(String.format(" Patient ID : %-20s  Patient Name: %s\n", patientID, patientName));
            sb.append(String.format(" Age / Sex  : %s / %-15s  DOB: %s\n", age, sex, dob));
            sb.append(String.format(" Date/Time  : %s\n", time));
            sb.append("--------------------------------------------------------------------------------\n");
            for (RadiologyTest rt : currentRadiologyTests) {
                sb.append(rt.getFormattedReport());
                sb.append("--------------------------------------------------------------------------------\n");
            }
            sb.append(String.format(" TOTAL RADIOLOGY CHARGES: P%.2f\n", radTotal));
            sb.append("================================================================================\n");

            radReportArea.setText(sb.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Clears all radiology selections and input fields
    private void clearRadiology() {
        radiologyTestList.clearSelection();
        currentRadiologyTests.clear();
        radiologyFindingsFields.clear();
        radiologyImpressionFields.clear();
        radiologyResultPanel.removeAll();
        radiologyResultPanel.revalidate();
        radiologyResultPanel.repaint();
        updateRadiologyPrice();
        if (radReportArea != null) radReportArea.setText("");
    }

    private void updateRadiologyPrice() {
        double total = 0;
        for (RadiologyTest rt : currentRadiologyTests) total += rt.price;
        radiologyPriceLabel.setText("  Radiology Total: P" + String.format("%.2f", total));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LaboratoryTest().setVisible(true));
    }
}
