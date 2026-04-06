public class Patient {
    public String patientID;
    public String name;
    public String dob;
    public int age;
    public String sex;
    public String contact;
    public String patientType;

    public Patient(String patientID, String name, String dob, int age, String sex, String contact, String patientType) {
        this.patientID = patientID;
        this.name = name;
        this.dob = dob;
        this.age = age;
        this.sex = sex;
        this.contact = contact;
        this.patientType = patientType;
    }
}