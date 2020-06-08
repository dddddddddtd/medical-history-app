import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;

public class PatientModel {
    private Patient patient;
    private String id;
    private String name;
    private String gender;
    private String birthdate;
    private String deceased;
    private String address;
    private String mbirth;
    private String contact;
    private String communication;
    private String practitioner;
    private String org;

    public String getGender() {
        return gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getDeceased() {
        return deceased;
    }

    public String getAddress() {
        return address;
    }

    public String getMbirth() {
        return mbirth;
    }

    public String getContact() {
        return contact;
    }

    public String getCommunication() {
        return communication;
    }

    public String getPractitioner() {
        return practitioner;
    }

    public String getOrg() {
        return org;
    }

    PatientModel(Patient patient) {
        this.patient = patient;
        this.id = patient.getIdElement().getIdPart();
        this.name = patient.getName().get(0).getGivenAsSingleString() + " " + patient.getName().get(0).getFamily();
        this.gender = patient.getGender().getDisplay();
        this.birthdate = patient.getBirthDate().toString();
//        this.deceased = patient.getDeceased().toString();
        this.address = patient.getAddressFirstRep().getCity(); //co tutaj?
//        this.mbirth = patient.getMultipleBirth().toString();
//        Practitioner practitioner = (Practitioner) patient.getGeneralPractitionerFirstRep().getResource();

        this.practitioner= " ";
        this.contact = patient.getContactFirstRep().toString(); //co tutaj?
        this.communication = patient.getCommunicationFirstRep().toString();
        this.org = patient.getManagingOrganization().getDisplay();
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
