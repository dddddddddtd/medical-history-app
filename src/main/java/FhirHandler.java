import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FhirHandler {
    private static FhirContext ctx = FhirContext.forR4();
    private static IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/baseR4");
    private static final List<String> resourceStrings = new ArrayList<String>(Arrays.asList("MedicationRequest", "Medication", "Observation"));
    private static List<PatientModel> globalPatients = new ArrayList<PatientModel>();
    private static Boolean hasPatients = false;

    public static List<PatientModel> getGlobalPatients() {
        return globalPatients;
    }

    public static void getPatients() {
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
        globalPatients = finalPatients.stream().map(x -> new PatientModel(x)).collect(Collectors.toList());
        hasPatients = true;
    }

    public static List<Resource> getPatientEverything(Patient patient) {
        try {
            Parameters outParams = client
                    .operation()
                    .onInstance(new IdType("Patient", patient.getIdElement().getIdPart()))
                    .named("$everything")
                    .withNoParameters(Parameters.class).useHttpGet()
                    .execute();


            Bundle result = (Bundle) outParams.getParameterFirstRep().getResource();

            List<Resource> resources = new ArrayList<>();

            while (result.getLink("next") != null) {
                for (Bundle.BundleEntryComponent e : result.getEntry()) {
                    resources.add(e.getResource());
                }
                result = client.loadPage().next(result).execute();
            }
            resources = resources.stream().filter(x -> resourceStrings.contains(x.getClass().getSimpleName())).collect(Collectors.toList());
            return resources;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateObservation(Observation observation, EventEditController eventEditController) {
        MethodOutcome methodOutcome = client.update().resource(observation).execute();
        eventEditController.updateEventModel((Resource) methodOutcome.getResource());
    }

    public static Boolean hasPatients() {
        return hasPatients;
    }
}
