package org.openmrs.module.bedmanagement.rest.resource;


import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
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
        LinkedHashMap<String, Object> orthopaedicWard = (LinkedHashMap<String, Object>) ((List) object.get("results")).get(1);
        LinkedHashMap<String, Object> cardioWardOnFirstFloorInfo = (LinkedHashMap<String, Object>) cardioWardOnFirstFloor.get("ward");
        LinkedHashMap<String, Object> orthopaedicWardInfo = (LinkedHashMap<String, Object>) orthopaedicWard.get("ward");

        assertEquals(2, results.size());
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
}
