import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class HapiFhirHandler {
    private static FhirContext ctx = FhirContext.forR4();
    private static IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/baseR4");

    public static List<Patient> getPatients() {
        List<IBaseResource> fhirpatients = new ArrayList<>();

        Bundle bundle = client
                .search()
                .forResource(Patient.class)
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

        //cast IBaseResource to Patient
        List<Patient> finalPatients = (List<Patient>) (List<?>) fhirpatients;

        return finalPatients;
    }

    public static void getPatientEverything(Patient patient){
        try {
            Parameters outParams = client
                    .operation()
                    .onInstance(new IdType("Patient", patient.getIdElement().getIdPart()))
                    .named("$everything")
                    .withNoParameters(Parameters.class).useHttpGet()
                    .execute();


            Bundle result = (Bundle) outParams.getParameterFirstRep().getResource();

            while (result.getLink("next") != null) {
                result.getEntry().forEach(e -> {
                    Resource res = e.getResource();
                    switch (res.getClass().getSimpleName()) {
                        case "MedicationRequest": {
                            MedicationRequest medReq = (MedicationRequest) res;
                            System.out.println("MedicationRequest: " + medReq.getRequester().getDisplay());
                            break;
                        }
                        case "Observation": {
                            Observation obs = (Observation) res;
                            System.out.println("Observation: "+obs.getCode().getText()+ " - "+obs.getValueQuantity().getValue());
                            break;
                        }
                        case "Medication": {
                            Medication med = (Medication) res;
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                });
                result = client.loadPage().next(result).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
