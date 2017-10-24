package org.openmrs.module.bedmanagement.rest.resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class WardResourceTest extends MainResourceControllerTest {

    @Before
    public void init() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Override
    public String getURI() {
        return "ward";
    }

    @Override
    public String getUuid() {
        return "7779d653-393b-4118-9c83-a3715b82d4ac";
    }

    @Override
    public long getAllCount() {
        return 0;
    }

    @Test
    public void shouldGelAllWardList() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        SimpleObject object = deserialize(handle(request));
        List results = (ArrayList) object.get("results");
        LinkedHashMap ward1 = (LinkedHashMap) results.get(0);
        LinkedHashMap ward2 = (LinkedHashMap) results.get(1);
        List ward1Rooms = (ArrayList) ward1.get("rooms");
        List ward2Rooms = (ArrayList) ward2.get("rooms");
        LinkedHashMap room1 = (LinkedHashMap) ward1Rooms.get(0);

        Assert.assertEquals(3, results.size());

        Assert.assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac", ward1.get("uuid"));
        Assert.assertEquals(1, ward1Rooms.size());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", room1.get("uuid"));
        Assert.assertEquals(10, room1.get("totalBed"));

        Assert.assertEquals("e26cea2c-1b9f-666e-6511-f3ef6c88af6f", ward2.get("uuid"));
        Assert.assertTrue(ward2Rooms.isEmpty());
    }

    @Test
    public void shouldGetWardByUuid() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
        SimpleObject ward = deserialize(handle(request));
        List wardRooms = (ArrayList) ward.get("rooms");
        LinkedHashMap room = (LinkedHashMap) wardRooms.get(0);

        Assert.assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac", ward.get("uuid"));
        Assert.assertEquals(1, wardRooms.size());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", room.get("uuid"));
        Assert.assertEquals(10, room.get("totalBed"));
    }

    @Test
    public void shouldSearchWardByName() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter("name", "Orthopaedic ward");
        SimpleObject object = deserialize(handle(request));
        List results = (ArrayList) object.get("results");

        LinkedHashMap ward = (LinkedHashMap) results.get(0);
        List wardRooms = (ArrayList) ward.get("rooms");
        LinkedHashMap room = (LinkedHashMap) wardRooms.get(0);

        Assert.assertEquals(1, results.size());
        Assert.assertEquals("Orthopaedic ward", ward.get("name"));
        Assert.assertEquals(1, wardRooms.size());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", room.get("uuid"));
        Assert.assertEquals(6, room.get("totalBed"));
    }

    @Test
    public void shouldAddNewWard() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI());
        SimpleObject postParameters = new SimpleObject();
        postParameters.put("name", "VIPs Ward");
        postParameters.put("description", "ward for vip person");
        SimpleObject postRoomParameter = new SimpleObject();
        postRoomParameter.put("name", "vip-100");
        postRoomParameter.put("description", "Vip ward room number 100");
        postParameters.put("room", postRoomParameter);
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        SimpleObject ward = deserialize(handle(request));

        List wardRooms = (ArrayList) ward.get("childLocations");
        List tags = (ArrayList) ward.get("tags");
        HashMap tag = (LinkedHashMap) tags.get(0);
        HashMap room = (LinkedHashMap) wardRooms.get(0);

        Assert.assertEquals("VIPs Ward", ward.get("name"));
        Assert.assertEquals("Admission Location", tag.get("name"));
        Assert.assertEquals(1, wardRooms.size());
        Assert.assertEquals("vip-100", room.get("name"));
    }

    @Test
    public void shouldUpdateWard() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
        SimpleObject postParameters = new SimpleObject();
        postParameters.put("name", "VIPs Ward");
        SimpleObject postRoomParameter = new SimpleObject();
        postRoomParameter.put("uuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
        postRoomParameter.put("name", "vip-100");
        postRoomParameter.put("description", "Vip ward room number 100");
        postParameters.put("room", postRoomParameter);
        String json = new ObjectMapper().writeValueAsString(postParameters);
        request.setContent(json.getBytes());
        SimpleObject ward = deserialize(handle(request));

        List wardRooms = (ArrayList) ward.get("childLocations");
        LinkedHashMap room = (LinkedHashMap) wardRooms.get(0);

        Assert.assertEquals("VIPs Ward", ward.get("name"));
        Assert.assertEquals(1, wardRooms.size());
        Assert.assertEquals("vip-100", room.get("name"));
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", room.get("uuid"));
    }

    @Test(expected = ObjectNotFoundException.class)
    public void shouldSoftDeleteWard() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
        request.addParameter("reason", "This is reason fir delete ward");
        handle(request);

        MockHttpServletRequest request2 = request(RequestMethod.GET, getURI() + "/" + getUuid());
        handle(request2);
    }

}
