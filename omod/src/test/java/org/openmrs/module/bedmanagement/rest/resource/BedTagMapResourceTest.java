package org.openmrs.module.bedmanagement.rest.resource;


import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.module.webservices.rest.SimpleObject;

import static org.junit.Assert.assertNotNull;

public class BedTagMapResourceTest extends MainResourceControllerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void init() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Override
    public String getURI() {
        return "bedTagMap";
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public long getAllCount() {
        return 0;
    }

    @Test
    public void shouldAssociateATagToBedIfItIsNotAssignedToBed() throws Exception {
        String json = "{\"bed\":{\"id\": \"11\"}, \"bedTag\": {\"id\": \"3\"}}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        SimpleObject bedTagMap = deserialize(handle(newPostRequest(getURI(), post)));

        assertNotNull(bedTagMap);
        assertNotNull(bedTagMap.get("uuid"));
    }

    @Test
    public void shouldThrowAnExceptionIfTheTagWeAreTryingToAssociateIsAlreadyPresent() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Tag Already Present For Bed");

        String json = "{\"bed\":{\"id\": \"11\"}, \"bedTag\": {\"id\": \"3\"}}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
        SimpleObject bedTagMap = deserialize(handle(newPostRequest(getURI(), post)));
        assertNotNull(bedTagMap);
        assertNotNull(bedTagMap.get("uuid"));
        deserialize(handle(newPostRequest(getURI(), post)));
    }
}
