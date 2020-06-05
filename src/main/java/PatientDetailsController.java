import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import org.hl7.fhir.r4.model.Patient;

import java.net.URL;
import java.util.ResourceBundle;

public class PatientDetailsController implements Initializable {
    private PatientModel patient;

    @FXML
    Text texttest;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setup(){
        texttest.setText(patient.getName());

    }

    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }
}
