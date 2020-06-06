import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

//import ca.uhn.fhir.context.FhirContext;
//import ca.uhn.fhir.rest.client.ServerValidationModeEnum;
//import ca.uhn.fhir.rest.client.IGenericClient;
//import ca.uhn.fhir.util.BundleUtil;
//import org.hl7.fhir.dstu3.model.*;
//import org.hl7.fhir.instance.model.api.IBaseBundle;
//import org.hl7.fhir.instance.model.api.IBaseResource;

public class Main extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        loadPatientList();
    }

    public void loadPatientList() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/patient_list.fxml"));
        Parent root = loader.load();

        PatientListController controller =
                loader.<PatientListController>getController();
        controller.setMainController(this);

        mainStage.setScene(new Scene(root));
        mainStage.setTitle("Medical History");
        mainStage.show();

    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
