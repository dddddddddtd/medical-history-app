import org.hl7.fhir.r4.model.Patient;

public class PatientModel {
    private Patient patient;
    private String id;
    private String name;

    PatientModel(Patient patient) {
        this.patient = patient;
        this.id = patient.getIdElement().getIdPart();
        this.name = patient.getName().get(0).getGivenAsSingleString() +" "+ patient.getName().get(0).getFamily();
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
