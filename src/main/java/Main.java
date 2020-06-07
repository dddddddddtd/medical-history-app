import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

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
