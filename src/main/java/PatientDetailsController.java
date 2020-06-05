import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import org.hl7.fhir.r4.model.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PatientDetailsController implements Initializable {
    private PatientModel patient;
    private List<Observation> observations;
    private List<Medication> medications;
    private List<MedicationRequest> medicationRequests;

    @FXML
    Text texttest;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setup(){
        texttest.setText(patient.getName());
        Patient r4patient = patient.getPatient();
        List<Resource> resources = FhirHandler.getPatientEverything(r4patient);
        medicationRequests=  resources.stream().filter(x-> x.getClass().getSimpleName() == "MedicationRequest").map(x -> (MedicationRequest) x).collect(Collectors.toList());
        medications=resources.stream().filter(x-> x.getClass().getSimpleName() == "Medication").map(x -> (Medication) x).collect(Collectors.toList());
        observations=resources.stream().filter(x-> x.getClass().getSimpleName() == "Observation").map(x -> (Observation) x).collect(Collectors.toList());
    }

    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }
}
