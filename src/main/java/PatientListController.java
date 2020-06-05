import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class PatientListController implements Initializable {

    public TextField filter_text_box;
    public TableView<PatientModel> mainTable;
    private ObservableList<PatientModel> patients;
    private final ArrayList<PatientModel> filtered_patients = new ArrayList<PatientModel>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<Patient> r4patients = HapiFhirHandler.getPatients();
        List<PatientModel> tempPatients = r4patients.stream().map(x -> new PatientModel(x)).collect(Collectors.toList());


        patients = FXCollections.observableArrayList(tempPatients);
        TableColumn<PatientModel, String> namecol = (TableColumn<PatientModel, String>) mainTable.getColumns().get(1);

        namecol.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("name"));
        System.out.println(patients.size());
        mainTable.setItems(patients);
    }

    public void add(ActionEvent actionEvent) {
    }

    public void showDetails(ActionEvent actionEvent) throws IOException {
        //TODO getowanie zaznaczonego wiersza - pacjenta
        PatientModel selectedPatient = mainTable.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/patient_details.fxml"));
        Parent root = loader.load();

        PatientDetailsController controller =
                loader.<PatientDetailsController>getController();

        controller.setPatient(selectedPatient);
        controller.setup();

        Platform.runLater(() -> {
            Main.getMainStage().setScene(new Scene(root));
        });
    }

    public void delete(ActionEvent actionEvent) {
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
    }
}
