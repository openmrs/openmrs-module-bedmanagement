package org.openmrs.module.bedmanagement.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class BedResourceTest extends MainResourceControllerTest {
	
	private static final String AVAILABLE_BED_UUID = "bb1331bc-d225-11e4-9c67-080027b662ec";
	
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
		
		Assert.assertEquals(16, results.size());
		Assert.assertEquals("304-a", PropertyUtils.getProperty(results.get(0), "bedNumber"));
		Assert.assertEquals(1, PropertyUtils.getProperty(results.get(0), "row"));
		Assert.assertEquals(1, PropertyUtils.getProperty(results.get(0), "column"));
		Assert.assertEquals("307-a", "307-a", PropertyUtils.getProperty(results.get(10), "bedNumber"));
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
		MockHttpServletRequest request1 = request(RequestMethod.GET, getURI());
		request1.addParameter("status", "AVAILABLE");
		request1.addParameter("bedType", "deluxe");
		SimpleObject response1 = deserialize(handle(request1));
		List results = (ArrayList) response1.get("results");
		Object bedType = PropertyUtils.getProperty(results.get(0), "bedType");
		
		Assert.assertEquals(1, results.size());
		Assert.assertEquals("304-d", PropertyUtils.getProperty(results.get(0), "bedNumber"));
		Assert.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(0), "status"));
		Assert.assertEquals("deluxe", PropertyUtils.getProperty(bedType, "name"));
		
		MockHttpServletRequest request2 = request(RequestMethod.GET, getURI());
		request2.addParameter("status", "OCCUPIED");
		request2.addParameter("bedType", "deluxe");
		SimpleObject response2 = deserialize(handle(request2));
		List results2 = (ArrayList) response2.get("results");
		
		Assert.assertEquals(2, results2.size());
		Assert.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ec", PropertyUtils.getProperty(results2.get(0), "uuid"));
		Assert.assertEquals("OCCUPIED", PropertyUtils.getProperty(results2.get(0), "status"));
		Assert.assertEquals("bb12c454-d225-11e4-9c67-080027b662ec", PropertyUtils.getProperty(results2.get(1), "uuid"));
		Assert.assertEquals("OCCUPIED", PropertyUtils.getProperty(results2.get(1), "status"));
	}
	
	@Test
	public void shouldSearchBedByStatusAndLocationUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("status", "AVAILABLE");
		request.addParameter("locationUuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
		SimpleObject object = deserialize(handle(request));
		List results = (ArrayList) object.get("results");
		
		Assert.assertEquals(9, results.size());
		Assert.assertEquals("304-b", PropertyUtils.getProperty(results.get(0), "bedNumber"));
		Assert.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(0), "status"));
		Assert.assertEquals("305-c", PropertyUtils.getProperty(results.get(5), "bedNumber"));
		Assert.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(5), "status"));
		Assert.assertEquals("306-b", PropertyUtils.getProperty(results.get(8), "bedNumber"));
		Assert.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(8), "status"));
	}
	
	@Test
	public void shouldSearchBedByBedTypeAndLocationUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("bedType", "deluxe");
		request.addParameter("locationUuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
		SimpleObject object = deserialize(handle(request));
		List results = (ArrayList) object.get("results");
		
		Assert.assertEquals(2, results.size());
		Assert.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ec", PropertyUtils.getProperty(results.get(0), "uuid"));
		Object bedType = PropertyUtils.getProperty(results.get(0), "bedType");
		Assert.assertEquals("deluxe", PropertyUtils.getProperty(bedType, "name"));
		Assert.assertEquals("bb094d57-d225-11e4-9c67-080027b662ec", PropertyUtils.getProperty(results.get(1), "uuid"));
		Assert.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(1), "status"));
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
		
		Assert.assertNotNull(bed.get("id"));
		Assert.assertEquals("110-a", bed.get("bedNumber"));
		Assert.assertEquals(4, bed.get("row"));
		Assert.assertEquals(1, bed.get("column"));
		Assert.assertEquals("luxury", PropertyUtils.getProperty(bed.get("bedType"), "name"));
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
		
		Assert.assertNotNull(bed.get("id"));
		Assert.assertEquals("110-a", bed.get("bedNumber"));
		Assert.assertEquals(2, bed.get("row"));
		Assert.assertEquals(3, bed.get("column"));
		Assert.assertEquals("luxury", PropertyUtils.getProperty(bed.get("bedType"), "name"));
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
		
		Assert.assertEquals("307-ab", bed.get("bedNumber"));
		Assert.assertEquals(2, bed.get("row"));
		Assert.assertEquals(3, bed.get("column"));
		Assert.assertEquals("luxury", PropertyUtils.getProperty(bed.get("bedType"), "name"));
	}
	
	@Test(expected = IllegalPropertyException.class)
	public void shouldFailToDeleteOccupiedBed() throws Exception {
		MockHttpServletRequest deleteRequest = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		deleteRequest.setParameter("reason", "not needed");
		handle(deleteRequest);
	}
	
	@Test(expected = ObjectNotFoundException.class)
	public void shouldDeleteBed() throws Exception {
		MockHttpServletRequest deleteRequest = request(RequestMethod.DELETE, getURI() + "/" + AVAILABLE_BED_UUID);
		deleteRequest.setParameter("reason", "not needed");
		handle(deleteRequest);
		
		MockHttpServletRequest getRequest = request(RequestMethod.GET, getURI() + "/" + AVAILABLE_BED_UUID);
		System.out.println(deserialize(handle(getRequest)));
	}
}
