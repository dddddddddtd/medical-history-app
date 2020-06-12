import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventModel {
    private Resource resource;
    private String date;
    private String type;
    private String event;
    private String valueunit;
    private String value;
    private String unit;

    EventModel(MedicationRequest medicationRequest) {
        this.resource = medicationRequest;

        this.date = formatDate(medicationRequest.getAuthoredOn());

        this.type = "Medication Request";
        if (medicationRequest.hasMedicationReference())
            this.event = ((Medication) medicationRequest.getMedicationReference().getResource()).getCode().getText();
        if (medicationRequest.hasMedicationCodeableConcept())
            this.event = medicationRequest.getMedicationCodeableConcept().getText();

    }

    public void setDate(String date) {
        this.date = date;
    }

    public Resource getResource() {
        return resource;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    EventModel(Observation observation) {
        this.resource = observation;
        this.date = formatDate(observation.getIssued());
        this.type = "Observation";
        this.event = observation.getCode().getText();

        if (observation.hasValueQuantity()) {
            this.value = observation.getValueQuantity().getValue().toString();
            this.unit = observation.getValueQuantity().getUnit();
            this.valueunit=this.value+" "+this.unit;
        } else if (observation.hasValueCodeableConcept())
            this.valueunit = observation.getValueCodeableConcept().getText();
    }

    EventModel(Observation observation, Integer number) {
        this.resource = observation;

        this.date = formatDate(observation.getIssued());
        this.type = "Observation";
//        this.event = observation.getCode().getText();

        Observation.ObservationComponentComponent component = observation.getComponent().get(number);
        this.event = component.getCode().getText();
        this.valueunit = component.getValueQuantity().getValue() + " " + component.getValueQuantity().getUnit();
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(date);
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getEvent() {
        return event;
    }

    public String getValueunit() {
        return valueunit;
    }
}

