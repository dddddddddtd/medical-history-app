import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventEditController {

    @FXML
    private Text eventText;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField valueField;

    @FXML
    private Text valueText;

    @FXML
    private Button saveButton;

    public void setup(EventModel eventModel){
        eventText.setText(eventModel.getType() + "\n" + eventModel.getEvent());
        datePicker.setValue(LocalDate.parse(eventModel.getDate(), DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        valueField.setText(eventModel.getValue());
        valueText.setText(valueText.getText()+" ["+eventModel.getUnit()+"]");
    }

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        //TODO save current data to the server
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
