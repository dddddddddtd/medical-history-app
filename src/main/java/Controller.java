import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    public TextField filter_text_box;
    public TableView<PatientModel> mainTable;
    private ObservableList<PatientModel> patients;
    private final ArrayList<PatientModel> filtered_patients = new ArrayList<PatientModel>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        FhirContext ctx = FhirContext.forR4();
        String serverBase = "http://localhost:8080/baseR4";
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

// We'll populate this list
        List<IBaseResource> fhirpatients = new ArrayList<>();

// We'll do a search for all Patients and extract the first page
        Bundle bundle = client
                .search()
                .forResource(Patient.class)
                .count(100)
                .returnBundle(Bundle.class)
                .execute();
        fhirpatients.addAll(BundleUtil.toListOfResources(ctx, bundle));

// Load the subsequent pages
        while (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
            bundle = client
                    .loadPage()
                    .next(bundle)
                    .execute();
            fhirpatients.addAll(BundleUtil.toListOfResources(ctx, bundle));
        }

        System.out.println("Loaded " + fhirpatients.size() + " patients!");

        ArrayList<PatientModel> tempPatients = new ArrayList<PatientModel>();
        for(int i=0; i<fhirpatients.size(); i++){
            Patient r4patient = (Patient) fhirpatients.get(i);
            System.out.println(r4patient.getName().get(0).getGivenAsSingleString());
            tempPatients.add(new PatientModel(r4patient.getName().get(0).getGivenAsSingleString()));
        }
        patients = FXCollections.observableArrayList(tempPatients);
        TableColumn<PatientModel, String> namecol = (TableColumn<PatientModel, String>) mainTable.getColumns().get(1);

        namecol.setCellValueFactory(new PropertyValueFactory<PatientModel, String>("name"));
        System.out.println(patients.size());
        mainTable.setItems(patients);
    }

    public void add(ActionEvent actionEvent) {
    }

    public void showDetails(ActionEvent actionEvent) {
    }

    public void delete(ActionEvent actionEvent) {
    }


    public void filtering(KeyEvent onKeyReleased) {
        filtered_patients.clear();
        for(PatientModel patient : patients) {
            if (patient.name.toLowerCase().contains(filter_text_box.getText().toLowerCase())) {
                filtered_patients.add(patient);
            }
        }
        mainTable.setItems(FXCollections.observableArrayList(filtered_patients));
    }



    public void exit(ActionEvent actionEvent) {
    }
}
