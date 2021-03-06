import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.hl7.fhir.r4.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class PatientListController implements Initializable {

    private Main mainController;
    public TextField filter_text_box;
    public TableView<PatientModel> mainTable;
    private ObservableList<PatientModel> patients;
    private final ArrayList<PatientModel> filtered_patients = new ArrayList<PatientModel>();

    @FXML
    TableColumn<PatientModel, String> idColumn;

    @FXML
    TableColumn<PatientModel, String> nameColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("PatientDetailsController initalized");
        System.out.println("FhirHandler.hasPatients: " + FhirHandler.hasPatients());
        if (!FhirHandler.hasPatients()) {
            FhirHandler.getPatients();
        }
        patients = FXCollections.observableArrayList(FhirHandler.getGlobalPatients());
        idColumn.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("name"));
        mainTable.setItems(patients);
    }

    public void showDetails(ActionEvent actionEvent) throws IOException {
        PatientModel selectedPatient = mainTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/patient_details.fxml"));
            Parent root = loader.load();

            PatientDetailsController controller =
                    loader.<PatientDetailsController>getController();
            Platform.runLater(() -> {
                Main.getMainStage().setScene(new Scene(root));
            });

            controller.setPatient(selectedPatient);
            controller.setMainController(this.mainController);
            controller.setup();
        }
    }

    public void filtering(KeyEvent onKeyReleased) {
        filtered_patients.clear();
        for (PatientModel patient : patients) {
            if (patient.getName().toLowerCase().contains(filter_text_box.getText().toLowerCase())) {
                filtered_patients.add(patient);
            }
        }
        mainTable.setItems(FXCollections.observableArrayList(filtered_patients));
    }

    public void exit(ActionEvent actionEvent) {
        Main.getMainStage().close();
    }

    public void setMainController(Main mainController) {
        this.mainController = mainController;
    }
}
