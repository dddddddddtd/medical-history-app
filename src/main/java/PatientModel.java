import org.hl7.fhir.r4.model.Patient;

public class PatientModel {
    private Patient patient;
    private String name;

    PatientModel(Patient patient) {
        this.patient = patient;
        this.name = patient.getName().get(0).getGivenAsSingleString() +" "+ patient.getName().get(0).getFamily();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
