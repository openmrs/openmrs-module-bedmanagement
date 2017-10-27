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
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.dao.AdmissionLocationDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.pojo.AdmissionLocation;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.bedmanagement.service.impl.BedManagementServiceImpl;
import org.openmrs.module.webservices.rest.SimpleObject;
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

        AdmissionLocationDao admissionLocationDao = mock(AdmissionLocationDao.class);
        when(admissionLocationDao.getAdmissionLocationsByLocationTagName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION)).thenReturn(expectedWards);

        BedManagementServiceImpl bedManagementService = new BedManagementServiceImpl();
        bedManagementService.setAdmissionLocationDao(admissionLocationDao);

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
        assertNotNull(bedManagementService.unassignPatientFromBed(patient));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveAssignBedsAndEditAdmissionLocationsPrivileges() {
        Context.authenticate(normalUser, normalUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        bedManagementService.assignPatientToBed(patient, encounter, bedNumber);
        bedManagementService.unassignPatientFromBed(patient);
    }

    @Test
    public void shouldPassIfUserHasGetBedsAndEditAdmissionLocationsPrivileges() {
        Context.authenticate(superUser, superUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        assertNotNull(bedManagementService.getBedAssignmentDetailsByPatient(patient));
        assertNotNull(bedManagementService.getBedDetailsByBedId("12"));
        assertNotNull(bedManagementService.getBedDetailsByBedUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb"));
        assertNotNull(bedManagementService.getBedPatientAssignmentByUuid("7819d653-393b-4118-9c83-a3715b82d4dd"));
        assertNotNull(bedManagementService.getLatestBedDetailsByVisitUuid("8cfda6ae-6b78-11e0-93c3-18a905e044dc"));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetBedsAndEditAdmissionLocationsPrivileges() {
        Context.authenticate(normalUser, normalUserPassword);

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        bedManagementService.getBedAssignmentDetailsByPatient(patient);
        bedManagementService.getBedDetailsByBedId("13");
        bedManagementService.getBedDetailsByBedUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        bedManagementService.getBedPatientAssignmentByUuid("7819d653-393b-4118-9c83-a3715b82d4dd");
        bedManagementService.getLatestBedDetailsByVisitUuid("8cfda6ae-6b78-11e0-93c3-18a905e044dc");
    }

    @Test
    public void shouldGetBedIfUserHasEditBedsPrivileges() {
        Context.authenticate(superUser, superUserPassword);
        Bed bed = Context.getService(BedManagementService.class).getBedById(1);

        Assert.assertTrue(bed.getId().equals(1));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetBedsPrivileges() {
        Context.authenticate(normalUser, normalUserPassword);
        Bed bed = Context.getService(BedManagementService.class).getBedById(1);
    }

    @Test
    public void shouldSaveBedIfUserHasEditBedsPrivileges() throws Exception {
        Context.authenticate(superUser, superUserPassword);

        String jsonStrig = "{\n" +
                "  \"bedNumber\": \"304-b\",\n" +
                "  \"bedType\": \"deluxe\",\n" +
                "  \"row\" : 4,\n" +
                "  \"column\" : 1,\n" +
                "  \"locationUuid\" : \"98bc9b32-9d1a-11e2-8137-0800271c1b75\"\n" +
                "}";
        SimpleObject properties = SimpleObject.parseJson(jsonStrig);
        Bed bed = Context.getService(BedManagementService.class).saveBed(null, properties);

        Assert.assertNotNull(bed);
        Assert.assertEquals("304-b", bed.getBedNumber());
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveEditBedsPrivileges() throws Exception {
        Context.authenticate(normalUser, normalUserPassword);

        String jsonStrig = "{\n" +
                "  \"bedNumber\": \"304-b\",\n" +
                "  \"bedType\": \"deluxe\",\n" +
                "  \"row\" : 4,\n" +
                "  \"column\" : 1,\n" +
                "  \"locationUuid\" : \"98bc9b32-9d1a-11e2-8137-0800271c1b75\"\n" +
                "}";
        SimpleObject properties = SimpleObject.parseJson(jsonStrig);
        Context.getService(BedManagementService.class).saveBed(null, properties);
    }

    @Test
    public void shouldSaveBedLoationMappingIfUserHasEditBedsPrivileges() throws Exception {
        Context.authenticate(superUser, superUserPassword);

        Bed bed = Context.getService(BedManagementService.class).getBedById(1);
        BedLocationMapping bedLocationMapping = Context.getService(BedManagementService.class).saveBedLocationMapping("98bc9b32-9d1a-11e2-8137-0800271c1b75", 4, 1, bed);

        Assert.assertNotNull(bedLocationMapping);
        Assert.assertEquals(4, bedLocationMapping.getRow());
        Assert.assertEquals(1, bedLocationMapping.getColumn());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
    }

    @Test
    public void shouldThrowSaveBedLoationMappingIfUserHasEditBedsPrivileges() throws Exception {
        Context.authenticate(superUser, superUserPassword);

        Bed bed = Context.getService(BedManagementService.class).getBedById(1);
        BedLocationMapping bedLocationMapping = Context.getService(BedManagementService.class).saveBedLocationMapping("98bc9b32-9d1a-11e2-8137-0800271c1b75", 1, 1, bed);

        Assert.assertNotNull(bedLocationMapping);
        Assert.assertEquals(1, bedLocationMapping.getRow());
        Assert.assertEquals(1, bedLocationMapping.getColumn());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
    }

    @Test
    public void shouldSoftDeleteBedIfUserHasEditBedsPrivileges() throws Exception {
        Context.authenticate(superUser, superUserPassword);

        Bed bed = Context.getService(BedManagementService.class).getBedById(1);
        Context.getService(BedManagementService.class).deleteBed(bed, "remove bed form location");
        Assert.assertTrue(bed.getVoided());
        Assert.assertEquals("remove bed form location", bed.getVoidReason());
    }


    @Test
    public void shouldGetAllWardsIfUserHasEditBedsPrivileges() throws Exception {
        Context.authenticate(superUser, superUserPassword);
        List<Location> wards = Context.getService(BedManagementService.class).getAllWards();

        Assert.assertEquals(3, wards.size());
        Assert.assertEquals("Orthopaedic ward", wards.get(2).getName());
    }

    @Test
    public void shouldReturnWardByUuid() throws Exception {
        Context.authenticate(superUser, superUserPassword);
        Location ward = Context.getService(BedManagementService.class).getWardByUuid("7779d653-393b-4118-9c83-a3715b82d4ac");

        Assert.assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac", ward.getUuid());
    }

    @Test
    public void shouldSaveNewWardIfUserHasEditBedsPrivileges() throws Exception {
        Context.authenticate(superUser, superUserPassword);
        Context.addProxyPrivilege("Get Location Attribute Types");

        String jsonStrig = "{\n" +
                "\t\"name\":\"VIPs Ward\",\n" +
                "\t\"description\" : \"ward for vip person\",\n" +
                "\t\"room\" : {\n" +
                "\t\t\"name\" : \"vip-100\",\n" +
                "\t\t\"description\" : \"for test\"\n" +
                "\t}\n" +
                "}";
        SimpleObject properties = SimpleObject.parseJson(jsonStrig);
        Location ward = Context.getService(BedManagementService.class).saveWard(null, properties);

        Assert.assertEquals("VIPs Ward", ward.getName());
        Assert.assertEquals("vip-100", ward.getChildLocations().iterator().next().getName());
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotManageLocationPrivileges() throws Exception {
        Context.authenticate(normalUser, normalUserPassword);
        Context.addProxyPrivilege("Get Location Attribute Types");

        String jsonStrig = "{\n" +
                "\t\"name\":\"VIPs Ward\",\n" +
                "\t\"description\" : \"ward for vip person\",\n" +
                "\t\"room\" : {\n" +
                "\t\t\"name\" : \"vip-100\",\n" +
                "\t\t\"description\" : \"for test\"\n" +
                "\t}\n" +
                "}";
        SimpleObject properties = SimpleObject.parseJson(jsonStrig);
        Context.getService(BedManagementService.class).saveWard(null, properties);
    }
}
