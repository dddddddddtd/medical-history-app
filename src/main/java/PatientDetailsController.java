import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.hl7.fhir.r4.model.*;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class PatientDetailsController implements Initializable {
    private Main mainController;
    private PatientModel patient;
    private List<MedicationRequest> medicationRequests = new ArrayList<>();
    private ObservableList<EventModel> events;
    private final ArrayList<EventModel> filtered_events = new ArrayList<EventModel>();
    Map<String, List<Observation>> observations;

    @FXML
    Label patientText;

    @FXML
    ScatterChart<Number, Number> chart;

    @FXML
    ChoiceBox<String> chartChoice;

    @FXML
    private Text idText;

    @FXML
    private TextField filter_text_box;

    @FXML
    private Text nameText;

    @FXML
    private Text genderText;

    @FXML
    private Text birthdateText;

    @FXML
    private Text phoneNumberText;

    @FXML
    private Text countryText;

    @FXML
    private Text cityText;

    @FXML
    private Text streetText;

    @FXML
    private Text postalCodeText;


    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private TableView<EventModel> eventTable;

    @FXML
    private TableColumn<EventModel, String> dateColumn;

    @FXML
    private TableColumn<EventModel, String> typeColumn;

    @FXML
    private TableColumn<EventModel, String> eventColumn;

    @FXML
    private TableColumn<EventModel, String> valueColumn;


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
        countryText.setText(patient.getCountry());
        cityText.setText(patient.getCity());
        streetText.setText(patient.getStreet());
        postalCodeText.setText(patient.getPostalcode());
        phoneNumberText.setText(patient.getPhonenumber());

        List<Resource> resources = FhirHandler.getPatientEverything(patient.getPatient());
        List<EventModel> tempEvents = new ArrayList<>();
        List<Observation> tempObservations = new ArrayList<>();
        if (resources != null) {
            for (Resource res : resources)
                switch (res.getClass().getSimpleName()) {
                    case "MedicationRequest":
                        MedicationRequest medicationRequest = (MedicationRequest) res;
                        medicationRequests.add(medicationRequest);
                        tempEvents.add(new EventModel(medicationRequest));
                        break;
                    case "Observation":
                        Observation observation = (Observation) res;
                        tempObservations.add(observation);
                        if (observation.hasComponent()) {
                            for (int i = 0; i < observation.getComponent().size(); i++) {
                                tempEvents.add(new EventModel(observation, i));
                            }
                        } else
                            tempEvents.add(new EventModel(observation));
                        break;
                }
        }

        observations = tempObservations.stream()
                .collect(Collectors.groupingBy((x -> x.getCode().getText())));

        NumberAxis xAxis = (NumberAxis) chart.getXAxis();

        Map<String, List<Observation>> filtered = observations.entrySet().stream().filter(a -> a.getValue().stream().anyMatch(l -> l.hasValueQuantity())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        chartChoice.setItems(FXCollections.observableArrayList(filtered.keySet()).sorted());
        chartChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                chart.getData().clear();

                XYChart.Series<Number, Number> series = new XYChart.Series<>();

                for (Observation x : filtered.get(newValue)) {
                    if (x.hasValueQuantity()) {
                        Number value = x.getValueQuantity().getValue();
                        series.getData().add(new XYChart.Data(x.getIssued().getTime(), value, x));
                    }
                }

                chart.getData().add(series);
                for (XYChart.Series<Number, Number> s : chart.getData()) {
                    for (XYChart.Data<Number, Number> d : s.getData()) {
                        Observation observation = (Observation) d.getExtraValue();
                        String tooltipText =
                                "Date: " + observation.getIssued().toString() + "\n" +
                                        "Value: " + d.getYValue() + " [" + observation.getValueQuantity().getUnit()+"]";
                        Tooltip tooltip = new Tooltip(tooltipText);
                        Tooltip.install(d.getNode(), tooltip);
                    }
                }

                chart.getYAxis().setLabel(filtered.get(newValue).get(0).getValueQuantity().getUnit());
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

                List<Long> xvalues = series.getData().stream().map(x -> x.getXValue().longValue()).collect(Collectors.toList());
                xvalues.sort(null);

                Calendar calendar = Calendar.getInstance();

                calendar.setTimeInMillis(xvalues.get(0));
                calendar.add(Calendar.MONTH, -1);
                startDate.setValue(calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                Long lowerbound = calendar.getTimeInMillis();

                calendar.setTimeInMillis(xvalues.get(xvalues.size() - 1));
                calendar.add(Calendar.MONTH, 1);
                endDate.setValue(calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                Long upperbound = calendar.getTimeInMillis();

                xAxis.setAutoRanging(false);
                xAxis.setLowerBound(lowerbound);
                xAxis.setUpperBound(upperbound);
                xAxis.setTickUnit((upperbound - lowerbound) / 10);
            }
        });

        startDate.valueProperty().addListener(((observable, oldValue, newValue) -> {
            xAxis.setLowerBound(Date.from(newValue.atStartOfDay(ZoneId.systemDefault()).toInstant()).toInstant().toEpochMilli());
            xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10);
        }));
        endDate.valueProperty().addListener(((observable, oldValue, newValue) -> {
            xAxis.setUpperBound(Date.from(newValue.atStartOfDay(ZoneId.systemDefault()).toInstant()).toInstant().toEpochMilli());
            xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10);
        }));

        dateColumn.setCellValueFactory(new PropertyValueFactory<EventModel, String>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<EventModel, String>("type"));
        eventColumn.setCellValueFactory(new PropertyValueFactory<EventModel, String>("event"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<EventModel, String>("value"));

        eventTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount()==2){
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("./event_edit.fxml"));
                        Parent root = loader.load();

                        EventEditController controller =
                                loader.<EventEditController>getController();
                        controller.setup(eventTable.getSelectionModel().getSelectedItem());

                        Stage stage = new Stage();
                        stage.setTitle("Event Editor");
                        stage.setScene(new Scene(root, 400, 200));
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        this.events = FXCollections.observableArrayList(tempEvents);
        eventTable.setItems(events);
    }

    public void filtering(KeyEvent onKeyReleased) {
        filtered_events.clear();
        for (EventModel event : events) {
            if (String.format("%s %s %s %s", event.getDate(), event.getType(), event.getEvent(), event.getValueunit()).toLowerCase().contains(filter_text_box.getText().toLowerCase())) {
                filtered_events.add(event);
            }
        }
        eventTable.setItems(FXCollections.observableArrayList(filtered_events));
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
