package org.openmrs.module.bedmanagement.rest.resource;


import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AdmissionLocationResourceTest extends MainResourceControllerTest {

    @Before
    public void init() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Override
    public String getURI() {
        return "admissionLocation";
    }

    @Override
    public String getUuid() {
        return "19e023e8-20ee-4237-ade6-9e68f897b7a9";
    }

    @Override
    public long getAllCount() {
        return 0;
    }


    @Test
    public void shouldGetPatientInfoAlongWithLayoutIfTheBedInTheLayoutIsOccupiedWhenTheRepresentionIsFull() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
        request.setParameter("v", "full");
        SimpleObject object = deserialize(handle(request));

        List bedLayouts = (List) object.get("bedLayouts");
        LinkedHashMap<String, Object> occupiedBed = (LinkedHashMap<String, Object>) ((List) object.get("bedLayouts")).get(0);
        LinkedHashMap<String, Object> unOccupiedBed = (LinkedHashMap<String, Object>) ((List) object.get("bedLayouts")).get(2);
        LinkedHashMap<String, Object> patient = (LinkedHashMap<String, Object>) occupiedBed.get("patient");

        assertEquals(6, bedLayouts.size());
        assertEquals("OCCUPIED", occupiedBed.get("status"));
        assertEquals("307-a", occupiedBed.get("bedNumber"));
        assertNotNull(patient);
        assertEquals("2b597be0-83c7-4f1d-b3d2-1d61ab128762", patient.get("uuid"));
        List<Object> bedTagMapsForOccupiedBed = (List<Object>) occupiedBed.get("bedTagMaps");
        assertEquals(2, bedTagMapsForOccupiedBed.size());
        assertEquals("AVAILABLE", unOccupiedBed.get("status"));
        assertEquals("307-c", unOccupiedBed.get("bedNumber"));
        assertNull(unOccupiedBed.get("patient"));
        List<Object> bedTagMapsForUnOccupiedBed = (List<Object>) unOccupiedBed.get("bedTagMaps");
        assertEquals(1, bedTagMapsForUnOccupiedBed.size());
    }

    @Test
    public void shouldGetWardAlongWithTotalBedsAndOccupiedBedsWhenTheRepresentionIsDefault() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        SimpleObject object = deserialize(handle(request));

        List results = (List) object.get("results");
        LinkedHashMap<String, Object> cardioWardOnFirstFloor = (LinkedHashMap<String, Object>) ((List) object.get("results")).get(0);
        LinkedHashMap<String, Object> orthopaedicWard = (LinkedHashMap<String, Object>) ((List) object.get("results")).get(2);
        LinkedHashMap<String, Object> cardioWardOnFirstFloorInfo = (LinkedHashMap<String, Object>) cardioWardOnFirstFloor.get("ward");
        LinkedHashMap<String, Object> orthopaedicWardInfo = (LinkedHashMap<String, Object>) orthopaedicWard.get("ward");

        assertEquals(3, results.size());
        assertNotNull(cardioWardOnFirstFloor);
        assertNotNull(cardioWardOnFirstFloorInfo);
        assertEquals("Cardio ward on first floor", cardioWardOnFirstFloorInfo.get("name"));
        assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac", cardioWardOnFirstFloorInfo.get("uuid"));
        assertEquals(10, cardioWardOnFirstFloor.get("totalBeds"));
        assertEquals(1, cardioWardOnFirstFloor.get("occupiedBeds"));

        assertNotNull(orthopaedicWard);
        assertNotNull(orthopaedicWardInfo);
        assertEquals("Orthopaedic ward", orthopaedicWardInfo.get("name"));
        assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", orthopaedicWardInfo.get("uuid"));
        assertEquals(6, orthopaedicWard.get("totalBeds"));
        assertEquals(2, orthopaedicWard.get("occupiedBeds"));
    }

    @Test
    public void shouldReturnAllAdmissionLocations() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        SimpleObject object = deserialize(handle(request));
        List results = (ArrayList) object.get("results");
        LinkedHashMap location1 = (LinkedHashMap) results.get(0);
        LinkedHashMap ward1 = (LinkedHashMap) location1.get("ward");
        LinkedHashMap location2 = (LinkedHashMap) results.get(1);
        LinkedHashMap ward2 = (LinkedHashMap) location2.get("ward");
        LinkedHashMap location3 = (LinkedHashMap) results.get(2);
        LinkedHashMap ward3 = (LinkedHashMap) location3.get("ward");

        Assert.assertEquals(3, results.size());
        Assert.assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac", ward1.get("uuid"));
        Assert.assertTrue(location1.containsKey("ward"));
        Assert.assertTrue(location1.containsKey("totalBeds"));
        Assert.assertTrue(location1.containsKey("occupiedBeds"));
        Assert.assertEquals("e26cea2c-1b9f-666e-6511-f3ef6c88af6f", ward2.get("uuid"));
        Assert.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", ward3.get("uuid"));
    }

    @Test
    public void shouldReturnAdmissionLocationByUuidWithFullRepresentation() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
        request.setParameter("v", "full");
        SimpleObject location = deserialize(handle(request));
        LinkedHashMap ward = location.get("ward");

        Assert.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", ward.get("uuid"));
        Assert.assertEquals(6, location.get("totalBeds"));
        Assert.assertEquals(2, location.get("occupiedBeds"));
        Assert.assertTrue(location.containsKey("bedLayouts"));
        List bedLayouts = (ArrayList) location.get("bedLayouts");
        Assert.assertEquals(6, bedLayouts.size());

        MockHttpServletRequest request2 = request(RequestMethod.GET, getURI() + "/98bc9b32-9d1a-11e2-8137-0800271c1b75");
        SimpleObject location2 = deserialize(handle(request2));
        LinkedHashMap wardRoom = location2.get("ward");

        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", wardRoom.get("uuid"));
        Assert.assertEquals(10, location2.get("totalBeds"));
        Assert.assertEquals(1, location2.get("occupiedBeds"));
        Assert.assertFalse(location2.containsKey("bedLayouts"));
    }

    @Test
    public void shouldReturnAdmissionLocationLayoutByUuidWithLayoutRepresentation() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/98bc9b32-9d1a-11e2-8137-0800271c1b75");
        request.setParameter("v", "layout");
        SimpleObject admissionLocation = deserialize(handle(request));

        Assert.assertTrue(admissionLocation.containsKey("ward"));
        Assert.assertTrue(admissionLocation.containsKey("bedLocationMappings"));
        LinkedHashMap ward = admissionLocation.get("ward");
        List bedLocationMappings =  (ArrayList) admissionLocation.get("bedLocationMappings");
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", ward.get("uuid"));
        Assert.assertEquals(18, bedLocationMappings.size());
    }

    @Test
    public void shouldSaveNewAdmissionLocation() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI());
        SimpleObject postParameters = new SimpleObject();
        postParameters.put("name", "VIPs Ward");
        postParameters.put("description", "ward for vip person");
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        SimpleObject admissionLocation = deserialize(handle(request));
        LinkedHashMap ward = admissionLocation.get("ward");
        List tags = (ArrayList) ward.get("tags");
        HashMap tag = (LinkedHashMap) tags.get(0);

        Assert.assertEquals("VIPs Ward", ward.get("name"));
        Assert.assertEquals("Admission Location", tag.get("display"));
        Assert.assertNull(ward.get("childLocations"));
        Assert.assertEquals(0, admissionLocation.get("totalBeds"));
        Assert.assertEquals(0, admissionLocation.get("occupiedBeds"));
    }

    @Test
    public void shouldAddChildAdmissionLocation() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI());
        SimpleObject postParameters = new SimpleObject();
        postParameters.put("name", "VIPs Ward");
        postParameters.put("description", "ward for vip person");
        postParameters.put("parentLocationUuid", "7779d653-393b-4118-9c83-a3715b82d4ac");
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        SimpleObject admissionLocation = deserialize(handle(request));
        LinkedHashMap ward = admissionLocation.get("ward");
        List tags = (ArrayList) ward.get("tags");
        HashMap tag = (LinkedHashMap) tags.get(0);

        Assert.assertEquals("VIPs Ward", ward.get("name"));
        Assert.assertEquals("Admission Location", tag.get("display"));
        Assert.assertNotNull(ward.get("parentLocation"));
        HashMap parentAdmissionLocation = (LinkedHashMap) ward.get("parentLocation");
        Assert.assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac", parentAdmissionLocation.get("uuid"));
        Assert.assertEquals(0, admissionLocation.get("totalBeds"));
        Assert.assertEquals(0, admissionLocation.get("occupiedBeds"));
    }

    @Test
    public void shouldUpdateAdmissionLocation() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
        SimpleObject postParameters = new SimpleObject();
        postParameters.put("name", "VIPs Ward");
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        SimpleObject admissionLocation = deserialize(handle(request));
        LinkedHashMap ward = admissionLocation.get("ward");

        Assert.assertEquals("VIPs Ward", ward.get("name"));
    }

    @Test
    public void shouldSetAdmissionLocationLayout() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/e26cea2c-1b9f-666e-6511-f3ef6c88af6f");
        request.setParameter("v", "layout");
        SimpleObject postParameters = new SimpleObject();
        SimpleObject layoutInfo = new SimpleObject();
        layoutInfo.put("row", 2);
        layoutInfo.put("column", 3);
        postParameters.put("bedLayout", layoutInfo);
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        SimpleObject admissionLocation = deserialize(handle(request));

        LinkedHashMap ward = admissionLocation.get("ward");
        List bedLocationMappings =  (ArrayList) admissionLocation.get("bedLocationMappings");
        Assert.assertEquals("e26cea2c-1b9f-666e-6511-f3ef6c88af6f", ward.get("uuid"));
        Assert.assertEquals(6, bedLocationMappings.size());
    }
}
