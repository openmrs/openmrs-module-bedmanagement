package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.bedmanagement.service.impl.BedManagementServiceImpl;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LocationService locationService;

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
    public void shouldPassIfUserHasGetAdmissionLocationsPrivilege() {
        Context.authenticate(superUser, superUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        assertNotNull(bedManagementService.getAdmissionLocations());
        assertNotNull(bedManagementService.getAdmissionLocationByLocation(location));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetAdmissionLocationsPrivilege() {
        Context.authenticate(normalUser, normalUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        bedManagementService.getAdmissionLocations();
        bedManagementService.getAdmissionLocationByLocation(location);
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

    @Test
    public void shouldSetBedLayoutForAdmissionLocation() throws Exception {
        Context.authenticate(superUser, superUserPassword);

        Location location = Context.getService(LocationService.class).getLocationByUuid("e26cea2c-1b9f-666e-6511-f3ef6c88af6f");
        AdmissionLocation admissionLocation = Context.getService(BedManagementService.class).getAdmissionLocationByLocation(location);
        Context.getService(BedManagementService.class).setBedLayoutForAdmissionLocation(admissionLocation, 2, 3);
        List<BedLocationMapping> bedLocationMappings =  Context.getService(BedManagementService.class)
                .getBedLocationMappingByLocation(admissionLocation.getWard());

        Assert.assertEquals(6, bedLocationMappings.size());
        Assert.assertEquals(1, bedLocationMappings.get(0).getRow());
        Assert.assertEquals(1, bedLocationMappings.get(0).getColumn());
        Assert.assertEquals(2, bedLocationMappings.get(5).getRow());
        Assert.assertEquals(3, bedLocationMappings.get(5).getColumn());
    }

    @Test
    public void shouldReturnBedLocationMappingByLocation() throws Exception {
        Context.authenticate(superUser, superUserPassword);

        Location location = Context.getService(LocationService.class).getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        List<BedLocationMapping> bedLocationMappingList = Context.getService(BedManagementService.class).getBedLocationMappingByLocation(location);

        Assert.assertEquals(6, bedLocationMappingList.size());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", bedLocationMappingList.get(0).getLocation().getUuid());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", bedLocationMappingList.get(5).getLocation().getUuid());

        Location location2 = Context.getService(LocationService.class).getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        List<BedLocationMapping> bedLocationMappingList2 = Context.getService(BedManagementService.class).getBedLocationMappingByLocation(location2);
        Assert.assertEquals(18, bedLocationMappingList2.size());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMappingList2.get(0).getLocation().getUuid());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMappingList2.get(5).getLocation().getUuid());
        Assert.assertNull(bedLocationMappingList2.get(5).getBed());
    }

    @Test
    public void shouldSaveAdmissionLocation(){
        Context.authenticate(superUser, superUserPassword);
        Context.addProxyPrivilege("Get Location Attribute Types");

        Location location = Context.getLocationService().getLocationByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");
        AdmissionLocation admissionLocation = Context.getService(BedManagementService.class).getAdmissionLocationByLocation(location);
        admissionLocation.setWard(location);
        admissionLocation.getWard().setName("Test ward");
        admissionLocation.getWard().setDescription("For test");
        Context.getService(BedManagementService.class).saveAdmissionLocation(admissionLocation);

        Assert.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", admissionLocation.getWard().getUuid());
        Assert.assertEquals(6, admissionLocation.getTotalBeds());
        Assert.assertNotNull(admissionLocation.getBedLayouts());
    }
}