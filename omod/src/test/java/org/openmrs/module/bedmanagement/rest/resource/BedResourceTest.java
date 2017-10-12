package org.openmrs.module.bedmanagement.rest.resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class BedResourceTest extends MainResourceControllerTest {

    @Before
    public void init() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Override
    public String getURI() {
        return "bed";
    }

    @Override
    public String getUuid() {
        return "bb12c454-d225-11e4-9c67-080027b662ec";
    }

    @Override
    public long getAllCount() {
        return 0;
    }

    @Test
    public void shouldReturnAllBeds() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        SimpleObject object = deserialize(handle(request));
        List results = (ArrayList) object.get("results");
        HashMap bed1 = (LinkedHashMap) results.get(0);
        HashMap bed2 = (LinkedHashMap) results.get(10);

        Assert.assertEquals(16, results.size());
        Assert.assertEquals("304-a", bed1.get("bedNumber"));
        Assert.assertEquals(1, bed1.get("row"));
        Assert.assertEquals(1, bed1.get("column"));
        Assert.assertEquals("307-a", bed2.get("bedNumber"));
    }

    @Test
    public void shouldReturnBedByUuid() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
        SimpleObject bed = deserialize(handle(request));

        Assert.assertEquals("bb12c454-d225-11e4-9c67-080027b662ec", bed.get("uuid"));
        Assert.assertEquals("307-a", bed.get("bedNumber"));
        Assert.assertEquals(1, bed.get("row"));
        Assert.assertEquals(1, bed.get("column"));
    }

    @Test
    public void shouldSearchBedByTypeAndStatus() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.addParameter("status", "AVAILABLE");
        request.addParameter("bedType", "deluxe");
        SimpleObject object = deserialize(handle(request));
        List results = (ArrayList) object.get("results");
        HashMap bed1 = (LinkedHashMap) results.get(0);
        HashMap bedType1 = (LinkedHashMap) bed1.get("bedType");

        Assert.assertEquals(1, results.size());
        Assert.assertEquals("304-d", bed1.get("bedNumber"));
        Assert.assertEquals("AVAILABLE", bed1.get("status"));
        Assert.assertEquals("deluxe", bedType1.get("name"));


        MockHttpServletRequest request2 = request(RequestMethod.GET, getURI());
        request2.addParameter("status", "OCCUPIED");
        request2.addParameter("bedType", "deluxe");
        SimpleObject object2 = deserialize(handle(request2));
        List results2 = (ArrayList) object2.get("results");

        Assert.assertEquals(2, results2.size());
    }

    @Test
    public void shouldSearchBedByTypeAndLocationUuid() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.addParameter("status", "AVAILABLE");
        request.addParameter("locationUuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
        SimpleObject object = deserialize(handle(request));
        List results = (ArrayList) object.get("results");
        HashMap bed = (LinkedHashMap) results.get(0);

        Assert.assertEquals(9, results.size());
        Assert.assertEquals("304-b", bed.get("bedNumber"));
        Assert.assertEquals("AVAILABLE", bed.get("status"));
    }

    @Test
    public void shouldAddNewBed() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI());
        SimpleObject postParameters = new SimpleObject();
        postParameters.put("bedNumber", "110-a");
        postParameters.put("bedType", "luxury");
        postParameters.put("row", 4);
        postParameters.put("column", 1);
        postParameters.put("locationUuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        SimpleObject bed = deserialize(handle(request));
        HashMap bedType = (LinkedHashMap) bed.get("bedType");

        Assert.assertNotNull(bed.get("id"));
        Assert.assertEquals("110-a", bed.get("bedNumber"));
        Assert.assertEquals(4, bed.get("row"));
        Assert.assertEquals(1, bed.get("column"));
        Assert.assertEquals("luxury", bedType.get("name"));
    }

    @Test
    public void shouldAssignNewBedAtBedLocationMappingWhichHaveNoBedAssigned() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI());
        SimpleObject postParameters = new SimpleObject();
        postParameters.put("bedNumber", "110-a");
        postParameters.put("bedType", "luxury");
        postParameters.put("row", 2);
        postParameters.put("column", 3);
        postParameters.put("locationUuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        SimpleObject bed = deserialize(handle(request));
        HashMap bedType = (LinkedHashMap) bed.get("bedType");

        Assert.assertNotNull(bed.get("id"));
        Assert.assertEquals("110-a", bed.get("bedNumber"));
        Assert.assertEquals(2, bed.get("row"));
        Assert.assertEquals(3, bed.get("column"));
        Assert.assertEquals("luxury", bedType.get("name"));
    }

    @Test(expected = IllegalPropertyException.class)
    public void shouldThrowExceptionOnAlreadyAssignedBedPosition() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI());
        SimpleObject postParameters = new SimpleObject();
        postParameters.put("bedNumber", "110-a");
        postParameters.put("bedType", "luxury");
        postParameters.put("row", 1);
        postParameters.put("column", 1);
        postParameters.put("locationUuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        deserialize(handle(request));
    }

    @Test
    public void shouldUpdateBed() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
        SimpleObject postParameters = new SimpleObject();
        postParameters.put("bedNumber", "307-ab");
        postParameters.put("bedType", "luxury");
        postParameters.put("row", 2);
        postParameters.put("column", 3);
        postParameters.put("locationUuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        SimpleObject bed = deserialize(handle(request));
        HashMap bedType = (LinkedHashMap) bed.get("bedType");


        Assert.assertEquals("307-ab", bed.get("bedNumber"));
        Assert.assertEquals(2, bed.get("row"));
        Assert.assertEquals(3, bed.get("column"));
        Assert.assertEquals("luxury", bedType.get("name"));
    }

    @Test(expected = ObjectNotFoundException.class)
    public void shouldDeleteBed() throws Exception {
        MockHttpServletRequest deleteRequest = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
        deleteRequest.setParameter("reason", "not needed");
        handle(deleteRequest);

        MockHttpServletRequest getRequest = request(RequestMethod.GET, getURI() + "/" + getUuid());
        handle(getRequest);
    }
}
