package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BedManagementServiceIntegrationTest extends BaseModuleWebContextSensitiveTest {
    
    @Autowired
    private BedManagementService bedManagementService;
    private int bedIdFromDataSetup = 11;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Test
    public void getAllLocationsBy_gets_locations_for_a_tag() {
        List<AdmissionLocation> admissionLocationList = bedManagementService.getAllAdmissionLocations();
        assertThat(admissionLocationList.size(), is(2));

        AdmissionLocation cardioWard = getWard(admissionLocationList, "Cardio ward on first floor");
        Assert.assertEquals(10, cardioWard.getTotalBeds());
        Assert.assertEquals(1, cardioWard.getOccupiedBeds());

        AdmissionLocation orthoWard = getWard(admissionLocationList, "Orthopaedic ward");
        Assert.assertEquals(6, orthoWard.getTotalBeds());
        Assert.assertEquals(2, orthoWard.getOccupiedBeds());
    }

    @Test
    public void getBedsForWard_gets_all_bed_layouts_for_ward() {
        LocationService locationService = Context.getLocationService();

        Location ward = locationService.getLocationByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");
        AdmissionLocation admissionLocation = bedManagementService.getLayoutForWard(ward);

        assertEquals(6, admissionLocation.getBedLayouts().size());
        assertEquals("Physical Location for Orthopaedic ward", admissionLocation.getBedLayouts().get(0).getLocation());
    }

    private AdmissionLocation getWard(List<AdmissionLocation> admissionLocationList, String wardName) {
        for (AdmissionLocation admissionLocation : admissionLocationList) {
            if (admissionLocation.getWard().getName().equals(wardName))
                return admissionLocation;
        }
        return null;
    }

    @Test
    public void shouldReturnBedAssignmentDetailsByPatient() {
        PatientService patientService = Context.getPatientService();
        Patient patient = patientService.getPatient(3);

        LocationService locationService = Context.getLocationService();
        Location ward = locationService.getLocation(123452);
        String bedNumFromDataSetup = "307-a";

        BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        assertEquals(ward.getId(), bedDetails.getPhysicalLocation().getId());
        assertEquals(bedIdFromDataSetup, bedDetails.getBedId());
        assertEquals(bedNumFromDataSetup, bedDetails.getBedNumber());

    }

    @Test
    public void shouldReturnEmptyBedAssignmentDetailsForNewPatient() {
        PatientService patientService = Context.getPatientService();
        Patient patient = patientService.getPatient(5);
        BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        assertEquals(null, bedDetails);
    }

    @Test
    public void shouldGetBedDetailsById() {
        int deluxeBedId = 1;
        BedDetails details = bedManagementService.getBedDetailsById(String.valueOf(deluxeBedId));
        assertNotNull(details);
        assertNotNull(details.getPatients());
        assertNotNull(details.getCurrentAssignments());
        assertEquals("deluxe", details.getBedType().getName());
    }

    @Test
    public void shouldAssignUnassignExistingPatientFromBed() throws Exception {
        int bedId = 9;
        PatientService patientService = Context.getPatientService();
        Patient patient = patientService.getPatient(3);

        EncounterService encounterService = Context.getEncounterService();
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(patient);

        BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        assertNotNull(bedDetails);
        Assert.assertEquals(11, bedDetails.getBedId());

        bedManagementService.assignPatientToBed(patient, encountersByPatient.get(0), String.valueOf(bedId));

        bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        assertEquals(bedId, bedDetails.getBedId());
    }


    @Test
    public void shouldUnAssingPatientFromBed() throws Exception {
        PatientService patientService = Context.getPatientService();
        Patient patient = patientService.getPatient(4);
        BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        assertEquals(Integer.valueOf(12), bedDetails.getBed().getId());
        BedDetails unAssignedBed = bedManagementService.unAssignPatientFromBed(patient);
        assertEquals(Integer.valueOf(12), unAssignedBed.getBed().getId());
        bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        assertNull(bedDetails);
    }

    @Test
    public void shouldAssignPatientToBed() throws Exception {
        PatientService patientService = Context.getPatientService();
        Patient patient = patientService.getPatient(5);
        BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        assertNull(bedDetails);
        EncounterService encounterService = Context.getEncounterService();
        List<Encounter> encountersByPatient = encounterService.getEncountersByPatient(patient);
        bedManagementService.assignPatientToBed(patient, encountersByPatient.get(0), "10");
        BedDetails assigned = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        assertNotNull(assigned);
        assertNotNull(assigned.getCurrentAssignments());
        assertNull(assigned.getLastAssignment());
    }
}
