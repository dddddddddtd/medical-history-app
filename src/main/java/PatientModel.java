import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;

public class PatientModel {
    private final Patient patient;
    private final String id;
    private final String name;
    private final String gender;
    private final String birthdate;
    private final String phonenumber;
    private final String postalcode;
    private final String country;

    private final String city;
    private final String street;

    public String getGender() {
        return gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    PatientModel(Patient patient) {
        this.patient = patient;
        this.id = patient.getIdElement().getIdPart();
        this.name = patient.getName().get(0).getGivenAsSingleString() + " " + patient.getName().get(0).getFamily();
        this.gender = patient.getGender().getDisplay();
        this.birthdate = patient.getBirthDate().toString();
        this.city = patient.getAddressFirstRep().getCity();
        this.street = patient.getAddressFirstRep().getLine().get(0).toString();
        this.postalcode = patient.getAddressFirstRep().getPostalCode();
        this.country = patient.getAddressFirstRep().getCountry();
        this.phonenumber = patient.getTelecomFirstRep().getValue();
    }

    public Patient getPatient() {
        return patient;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }
}
