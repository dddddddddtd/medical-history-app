import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class EventEditController {

    private EventModel eventModel;

    private PatientDetailsController patientDetailsController;



    @FXML
    private Text eventText;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField valueField;

    @FXML
    private Text unitText;

    @FXML
    private Button saveButton;

    @FXML
    private Button resetButton;

    public void setEventModel(EventModel eventModel) {
        this.eventModel = eventModel;
        setup();
    }

    public void setup() {
        eventText.setText(eventModel.getType() + "\n" + eventModel.getEvent());
        datePicker.setValue(LocalDate.parse(eventModel.getDate(), DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        valueField.setText(eventModel.getValue());
        unitText.setText("[" + eventModel.getUnit() + "]");
    }

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        eventModel.setDate(datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        eventModel.setValue(valueField.getText());
        patientDetailsController.refresh();

        Observation observation = (Observation) eventModel.getResource();
        observation.setIssued(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        //TODO updating date works, have to fix value
//        if (observation.hasValueQuantity())
//            observation.getValueQuantity().setValue(Long.parseLong(valueField.getText()));
//        else if (observation.hasValueCodeableConcept()){
//            observation.getValueCodeableConcept().setText(valueField.getText());
//        }

        FhirHandler.updateObservation(observation);

        //TODO save current data to the server
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    public void setPatientDetailsController(PatientDetailsController patientDetailsController) {
        this.patientDetailsController = patientDetailsController;
    }
}
