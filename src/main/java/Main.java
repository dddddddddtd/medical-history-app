import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//import ca.uhn.fhir.context.FhirContext;
//import ca.uhn.fhir.rest.client.ServerValidationModeEnum;
//import ca.uhn.fhir.rest.client.IGenericClient;
//import ca.uhn.fhir.util.BundleUtil;
//import org.hl7.fhir.dstu3.model.*;
//import org.hl7.fhir.instance.model.api.IBaseBundle;
//import org.hl7.fhir.instance.model.api.IBaseResource;

public class Main extends Application {

//    private Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Medical History");

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
