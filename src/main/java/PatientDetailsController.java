import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PatientDetailsController implements Initializable {
    private Main mainController;
    private PatientModel patient;
    private List<Observation> observations = new ArrayList<>();
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
        System.out.println("PatientDetailsController initalized");
    }

    public void setup() {
        patientText.setText(patient.getName());
        List<Resource> resources = FhirHandler.getPatientEverything(patient.getPatient());

        for (Resource res : resources) {
            switch (res.getClass().getSimpleName()) {
                case "MedicationRequest":
                    medicationRequests.add((MedicationRequest) res);
                    break;
                case "Observation":
                    observations.add((Observation) res);
                    break;
            }
        }
    }

    public void printMedReq() {
        Integer i = 0;
        for (MedicationRequest medReq : medicationRequests) {
            if (medReq.hasMedicationCodeableConcept())
                System.out.println(i + ". Medication: " + medReq.getMedicationCodeableConcept().getText());
            else if (medReq.hasMedicationReference())
                //nie wiem co z tym zrobic w sumie
                //z tych przykladowych co ladowalismy nie ma ani jednego przykladu medication, zeby przetestowac
                System.out.println(i + ". Medication: " + "test");
        }
        i++;
    }

    public void printObs() {
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

    public void goBack() throws IOException {
        mainController.loadPatientList();
    }

    public void setMainController(Main mainController) {
        this.mainController = mainController;
    }
}
