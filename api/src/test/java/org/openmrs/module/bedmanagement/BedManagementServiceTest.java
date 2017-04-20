package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BedManagementServiceTest extends BaseModuleWebContextSensitiveTest {
    private String superUser;
    private String superUserPassword;
    private String normalUser;
    private String normalUserPassword;
    private Patient patient;
    private Location location;
    private Encounter encounter;
    private String bedNumber;

    @Before
    public void setUp() throws Exception {
        superUser = "test-user";
        superUserPassword = "test";
        normalUser = "normal-user";
        normalUserPassword = "normal-password";
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
        patient = Context.getPatientService().getPatient(3);
        location = Context.getLocationService().getLocation(12347);
        encounter = Context.getEncounterService().getEncounter(2);
        bedNumber = "11";
    }

    @Test
    public void getAllAdmissionLocations_gets_locations_that_support_admission() {
        ArrayList<AdmissionLocation> expectedWards = new ArrayList<AdmissionLocation>();

        BedManagementDAO bedManagementDao = mock(BedManagementDAO.class);
        when(bedManagementDao.getAdmissionLocationsBy(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION)).thenReturn(expectedWards);

        BedManagementServiceImpl bedManagementService = new BedManagementServiceImpl();
        bedManagementService.setDao(bedManagementDao);

        List<AdmissionLocation> wards = bedManagementService.getAllAdmissionLocations();
        Assert.assertSame(expectedWards, wards);
    }

    @Test
    public void shouldPassIfUserHasGetAdmissionLocationsPrivilege() {
        Context.authenticate(superUser, superUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        assertNotNull(bedManagementService.getAllAdmissionLocations());
        assertNotNull(bedManagementService.getLayoutForWard(location));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetAdmissionLocationsPrivilege() {
        Context.authenticate(normalUser, normalUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        bedManagementService.getAllAdmissionLocations();
        bedManagementService.getLayoutForWard(location);
    }

    @Test
    public void shouldPassIfUserHasAssignBedsAndEditAdmissionLocationsPrivileges() {
        Context.authenticate(superUser, superUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        assertNotNull(bedManagementService.assignPatientToBed(patient, encounter, bedNumber));
        assertNotNull(bedManagementService.unAssignPatientFromBed(patient));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveAssignBedsAndEditAdmissionLocationsPrivileges() {
        Context.authenticate(normalUser, normalUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        bedManagementService.assignPatientToBed(patient, encounter, bedNumber);
        bedManagementService.unAssignPatientFromBed(patient);
    }

    @Test
    public void shouldPassIfUserHasGetBedsAndEditAdmissionLocationsPrivileges() {
        Context.authenticate(superUser, superUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        assertNotNull(bedManagementService.getBedAssignmentDetailsByPatient(patient));
        assertNotNull(bedManagementService.getBedDetailsById("12"));
        assertNotNull(bedManagementService.getBedDetailsByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb"));
        assertNotNull(bedManagementService.getBedPatientAssignmentByUuid("7819d653-393b-4118-9c83-a3715b82d4dd"));
        assertNotNull(bedManagementService.getLatestBedDetailsByVisit("8cfda6ae-6b78-11e0-93c3-18a905e044dc"));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetBedsAndEditAdmissionLocationsPrivileges() {
        Context.authenticate(normalUser, normalUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        bedManagementService.getBedAssignmentDetailsByPatient(patient);
        bedManagementService.getBedDetailsById("13");
        bedManagementService.getBedDetailsByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        bedManagementService.getBedPatientAssignmentByUuid("7819d653-393b-4118-9c83-a3715b82d4dd");
        bedManagementService.getLatestBedDetailsByVisit("8cfda6ae-6b78-11e0-93c3-18a905e044dc");
    }
}