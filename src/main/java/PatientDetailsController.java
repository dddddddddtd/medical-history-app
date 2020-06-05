import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PatientDetailsController implements Initializable {
    private PatientModel patient;
    private List<Observation> observations = new ArrayList<>();
    private List<Medication> medications = new ArrayList<>();
    private List<MedicationRequest> medicationRequests = new ArrayList<>();

    @FXML
    Text patientText;

    @FXML
    Button medReqButton;

    @FXML
    Button medButton;

    @FXML
    Button obsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setup() {
        patientText.setText(patient.getName());
        Patient r4patient = patient.getPatient();
        List<Resource> resources = FhirHandler.getPatientEverything(r4patient);

        for (Resource res : resources) {
            switch (res.getClass().getSimpleName()) {
                case "MedicationRequest":
                    medicationRequests.add((MedicationRequest) res);
                    break;
                case "Medication":
                    medications.add((Medication) res);
                    break;
                case "Observation":
                    observations.add((Observation) res);
                    break;
            }
        }
        System.out.println(medicationRequests.size());
        System.out.println(medications.size());
        System.out.println(observations.size());
    }

    public void printMedReq() {
        System.out.println(medicationRequests.size());
        medicationRequests.forEach(medReq -> System.out.println("MedicationRequest: " + medReq.getRequester().getDisplay()));
    }

    public void printMed() {
        System.out.println(medications.size());
//        medications.forEach(med -> System.out.println(med.));
    }

    public void printObs() {
        System.out.println(observations.size());
        Integer i = 0;

        for (Observation obs : observations) {
            if (obs.hasValueQuantity())
                System.out.println(i + ". Observation: " + obs.getCode().getText() + " - " + obs.getValueQuantity().getValue() + " " + obs.getValueQuantity().getUnit());
            else if (obs.hasValueCodeableConcept())
                System.out.println(i + ". Observation: " + obs.getCode().getText() + " - " + obs.getValueCodeableConcept().getText());
            else if (obs.hasComponent()) {
                System.out.println(i + ". Observation:");
                int j = 0;
                for (Observation.ObservationComponentComponent component : obs.getComponent()) {
                    System.out.println(j + ". Component: " + component.getCode().getText() + " - " + component.getValueQuantity().getValue() + " " + component.getValueQuantity().getUnit());
                    j++;
                }
            }
            i++;
        }
    }

    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }
}
