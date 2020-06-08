import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.hl7.fhir.r4.model.*;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    Button obsButton;

    @FXML
    ScatterChart<Number, Number> chart;

    @FXML
    ChoiceBox<String> chartChoice;

    @FXML
    private Text idText;

    @FXML
    private Text nameText;

    @FXML
    private Text genderText;

    @FXML
    private Text birthdateText;

    @FXML
    private Text deceasedText;

    @FXML
    private Text addressText;

    @FXML
    private Text mbirthText;

    @FXML
    private Text contactText;

    @FXML
    private Text communicationText;

    @FXML
    private Text practitionerText;

    @FXML
    private Text orgText;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("PatientDetailsController initialized");
    }

    public void setup() {
        patientText.setText(patient.getName());
        idText.setText(patient.getId());
        nameText.setText(patient.getName());
        genderText.setText(patient.getGender());
        birthdateText.setText(patient.getBirthdate());
        deceasedText.setText("?");
        addressText.setText(patient.getAddress());
        mbirthText.setText("?");
        contactText.setText("?");
        communicationText.setText("?");
        practitionerText.setText(patient.getPractitioner());
        orgText.setText("?");



        List<Resource> resources = FhirHandler.getPatientEverything(patient.getPatient());

        List<Observation> tempObservations = new ArrayList<>();
        if (resources != null) {
            for (Resource res : resources)
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

        chartChoice.setItems(FXCollections.observableArrayList(observations.keySet()));
        chartChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                chart.getData().clear();

                XYChart.Series<Number, Number> series = new XYChart.Series<>();

                for (Observation x : observations.get(newValue)) {
                    if (x.hasValueQuantity()) {
                        Number value = x.getValueQuantity().getValue();
                        System.out.println(x.getIssued());

                        series.getData().add(new XYChart.Data(x.getIssued().getTime(), value));
                    }
                }

                chart.getData().add(series);
                for (XYChart.Series<Number, Number> s : chart.getData()) {
                    for (XYChart.Data<Number, Number> d : s.getData()) {
                        Tooltip tooltip = new Tooltip(d.getXValue().toString() + "\n" +
                                "Value: " + d.getYValue());
                        Tooltip.install(d.getNode(), tooltip);
                    }
                }
                NumberAxis xAxis = (NumberAxis) chart.getXAxis();
                xAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    @Override
                    public String toString(Number object) {
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        return df.format(new Date(object.longValue()));
                    }

                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });

                List<Long> xvalues = series.getData().stream().map(x -> x.getXValue().longValue()).sorted().collect(Collectors.toList());
                Long lowerbound = xvalues.get(0);
                Long upperbound = xvalues.get(xvalues.size() - 1);
                xAxis.setAutoRanging(false);
                xAxis.setLowerBound(lowerbound);
                xAxis.setUpperBound(upperbound);
            }
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

        observations.keySet().forEach(x -> {
            System.out.println(x);
            int i = 1;
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
