import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PatientDetailsController implements Initializable {
    private Main mainController;
    private PatientModel patient;
    private List<MedicationRequest> medicationRequests = new ArrayList<>();
    Map<String, List<Observation>> observations;

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

        List<Observation> tempObservations = new ArrayList<>();
        for (Resource res : resources) {
            switch (res.getClass().getSimpleName()) {
                case "MedicationRequest":
                    medicationRequests.add((MedicationRequest) res);
                    break;
                case "Observation":
                    tempObservations.add((Observation) res);
                    break;
            }
        }
        observations = tempObservations.stream()
                .collect(Collectors.groupingBy((x -> x.getCode().getText())));

        observations.get("Hemoglobin A1c/Hemoglobin.total in Blood").forEach(x->{
            System.out.println(x.getValueQuantity().getValue()+" "+x.getValueQuantity().getUnit());
        });
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

        observations.keySet().forEach(x->{
            System.out.println(x);
            int i=1;
            for (Observation obs : observations.get(x)) {
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
        });
        Integer i = 0;
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
