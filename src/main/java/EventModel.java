import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;
import sun.java2d.pipe.SpanShapeRenderer;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventModel {
    private Resource resource;
    private String date;
    private String type;
    private String event;
    private String value;

    EventModel(MedicationRequest medicationRequest) {
        this.resource = medicationRequest;

        this.date = formatDate(medicationRequest.getAuthoredOn());

        this.type = "Medication Request";
        if (medicationRequest.hasMedicationReference())
            this.event = ((Medication) medicationRequest.getMedicationReference().getResource()).getCode().getText();
        if (medicationRequest.hasMedicationCodeableConcept())
            this.event = medicationRequest.getMedicationCodeableConcept().getText();

    }

    EventModel(Observation observation) {
        this.resource = observation;
        this.date = formatDate(observation.getIssued());
        this.type = "Observation";
        this.event = observation.getCode().getText();

        if (observation.hasValueQuantity())
            this.value = observation.getValueQuantity().getValue() + " " + observation.getValueQuantity().getUnit();
        else if (observation.hasValueCodeableConcept())
            this.value = observation.getValueCodeableConcept().getText();
    }

    EventModel(Observation observation, Integer number) {
        this.resource = observation;

        this.date = formatDate(observation.getIssued());
        this.type = "Observation";
//        this.event = observation.getCode().getText();

        Observation.ObservationComponentComponent component = observation.getComponent().get(number);
        this.event = component.getCode().getText();
        this.value = component.getValueQuantity().getValue() + " " + component.getValueQuantity().getUnit();
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

    public String getValue() {
        return value;
    }
}

