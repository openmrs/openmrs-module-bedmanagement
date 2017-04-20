package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BedTagMapServiceTest extends BaseModuleWebContextSensitiveTest {
    private String privilegedUser;
    private String privilegedUserPassword;
    private String normalUser;
    private String normalUserPassword;
    private Bed bedFifteen;
    private BedTag isolationBedTag;
    private BedTagMap bedTagMap;
    private BedTagMapService bedTagMapService;

    @Before
    public void setUp() throws Exception {
        privilegedUser = "edit-tags-user";
        privilegedUserPassword = "normal-password";
        normalUser = "normal-user";
        normalUserPassword = "normal-password";
        executeDataSet("bedTagMapTestDataSet.xml");
        isolationBedTag = Context.getService(BedTagMapService.class).getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f3");
        bedFifteen = Context.getService(BedManagementService.class).getBedById(15);
        bedTagMap = new BedTagMap();
        bedTagMap.setBedTag(isolationBedTag);
        bedTagMap.setBed(bedFifteen);
        bedTagMapService = Context.getService(BedTagMapService.class);
    }

    @Test
    public void shouldAssignTheBedTagToBedIfTheUserHasTheGetTagsEditTagsAndGetBedsPrivileges() throws IllegalPropertyException {
        Context.authenticate(privilegedUser, privilegedUserPassword);
        BedTagMap savedBedTagMap = bedTagMapService.save(bedTagMap);

        assertNotNull(savedBedTagMap);
        assertNotNull(savedBedTagMap.getId());
        assertEquals(isolationBedTag, savedBedTagMap.getBedTag());
        assertEquals(bedFifteen, savedBedTagMap.getBed());
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsEditTagsAndGetBedsPrivileges() throws IllegalPropertyException {
        Context.authenticate(normalUser, normalUserPassword);
        bedTagMapService.save(bedTagMap);
    }

    @Test
    public void shouldUnAssignTheBedTagFromTheBedIfTheUserHasTheGetTagsEditTagsAndGetBedsPrivileges() {
        Context.authenticate(privilegedUser, privilegedUserPassword);
        bedTagMapService.delete(bedTagMap, "Need beds in general ward");
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsEditTagsAndGetBedsPrivilegesWhileDeletingTheBedTagMap() {
        Context.authenticate(normalUser, normalUserPassword);
        bedTagMapService.delete(bedTagMap, "Need beds in general ward");
    }

    @Test
    public void shouldGetBedTagMapByUuidIfTheUserHasTheGetTagsAndGetBedsPrivileges() {
        Context.authenticate(privilegedUser, privilegedUserPassword);
        BedTag oxygenBedTag = bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
        BedTagMap bedElevenWithOxygenTag = bedTagMapService.getBedTagMapByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f4");

        assertNotNull(bedElevenWithOxygenTag);
        assertNotNull(bedElevenWithOxygenTag.getId());
        assertEquals(oxygenBedTag, bedElevenWithOxygenTag.getBedTag());
        assertEquals(bedFifteen, bedElevenWithOxygenTag.getBed());
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsAndGetBedsPrivilegesWhileGettingTheBedTagMapUsingUuid() {
        Context.authenticate(normalUser, normalUserPassword);
        bedTagMapService.getBedTagMapByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f4");
    }

    @Test
    public void shouldGetBedTagMapWithBedAndTagIfTheUserHasTheGetTagsAndGetBedsPrivileges() {
        Context.authenticate(privilegedUser, privilegedUserPassword);
        BedTag oxygenBedTag = bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
        BedTagMap bedTagMapWithBedAndTag = bedTagMapService.getBedTagMapWithBedAndTag(bedFifteen, oxygenBedTag);

        assertNotNull(bedTagMapWithBedAndTag);
        assertNotNull(bedTagMapWithBedAndTag.getId());
        assertEquals(oxygenBedTag, bedTagMapWithBedAndTag.getBedTag());
        assertEquals(bedFifteen, bedTagMapWithBedAndTag.getBed());
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsAndGetBedsPrivilegesWhileGettingTheBedTagMapUsingBedAndBedTag() {
        Context.authenticate(normalUser, normalUserPassword);
        BedTag oxygenBedTag = bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
        bedTagMapService.getBedTagMapWithBedAndTag(bedFifteen, oxygenBedTag);
    }

    @Test
    public void shouldGetBedTagByUuidIfTheUserHasTheGetTagsAndGetBedsPrivileges() throws Exception {
        Context.authenticate(privilegedUser, privilegedUserPassword);
        BedTag oxygenBedTag = bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");

        assertNotNull(oxygenBedTag);
        assertEquals("Oxygen", oxygenBedTag.getName());
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsAndGetBedsPrivilegesWhileGettingBedTagByUuid() throws Exception {
        Context.authenticate(normalUser, normalUserPassword);
        bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
    }
}
