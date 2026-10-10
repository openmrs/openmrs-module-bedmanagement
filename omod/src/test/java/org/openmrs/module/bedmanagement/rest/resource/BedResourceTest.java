package org.openmrs.module.bedmanagement.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BedResourceTest extends MainResourceControllerTest {
	
	private static final String AVAILABLE_BED_UUID = "bb1331bc-d225-11e4-9c67-080027b662ec";
	
	@BeforeEach
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
		
		Assertions.assertEquals(16, results.size());
		Assertions.assertEquals("304-a", PropertyUtils.getProperty(results.get(0), "bedNumber"));
		Assertions.assertEquals(1, PropertyUtils.getProperty(results.get(0), "row"));
		Assertions.assertEquals(1, PropertyUtils.getProperty(results.get(0), "column"));
		Assertions.assertEquals("307-a", PropertyUtils.getProperty(results.get(10), "bedNumber"), "307-a");
	}
	
	@Test
	public void shouldReturnBedByUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject bed = deserialize(handle(request));
		
		Assertions.assertEquals("bb12c454-d225-11e4-9c67-080027b662ec", bed.get("uuid"));
		Assertions.assertEquals("307-a", bed.get("bedNumber"));
		Assertions.assertEquals(Integer.valueOf(1), bed.get("row"));
		Assertions.assertEquals(Integer.valueOf(1), bed.get("column"));
	}
	
	@Test
	public void shouldSearchBedByTypeAndStatus() throws Exception {
		MockHttpServletRequest request1 = request(RequestMethod.GET, getURI());
		request1.addParameter("status", "AVAILABLE");
		request1.addParameter("bedType", "deluxe");
		SimpleObject response1 = deserialize(handle(request1));
		List results = (ArrayList) response1.get("results");
		Object bedType = PropertyUtils.getProperty(results.get(0), "bedType");
		
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals("304-d", PropertyUtils.getProperty(results.get(0), "bedNumber"));
		Assertions.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(0), "status"));
		Assertions.assertEquals("deluxe", PropertyUtils.getProperty(bedType, "name"));
		
		MockHttpServletRequest request2 = request(RequestMethod.GET, getURI());
		request2.addParameter("status", "OCCUPIED");
		request2.addParameter("bedType", "deluxe");
		SimpleObject response2 = deserialize(handle(request2));
		List results2 = (ArrayList) response2.get("results");
		
		Assertions.assertEquals(2, results2.size());
		Assertions.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ec", PropertyUtils.getProperty(results2.get(0), "uuid"));
		Assertions.assertEquals("OCCUPIED", PropertyUtils.getProperty(results2.get(0), "status"));
		Assertions.assertEquals("bb12c454-d225-11e4-9c67-080027b662ec", PropertyUtils.getProperty(results2.get(1), "uuid"));
		Assertions.assertEquals("OCCUPIED", PropertyUtils.getProperty(results2.get(1), "status"));
	}
	
	@Test
	public void shouldSearchBedByStatusAndLocationUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("status", "AVAILABLE");
		request.addParameter("locationUuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
		SimpleObject object = deserialize(handle(request));
		List results = (ArrayList) object.get("results");
		
		Assertions.assertEquals(9, results.size());
		Assertions.assertEquals("304-b", PropertyUtils.getProperty(results.get(0), "bedNumber"));
		Assertions.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(0), "status"));
		Assertions.assertEquals("305-c", PropertyUtils.getProperty(results.get(5), "bedNumber"));
		Assertions.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(5), "status"));
		Assertions.assertEquals("306-b", PropertyUtils.getProperty(results.get(8), "bedNumber"));
		Assertions.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(8), "status"));
	}
	
	@Test
	public void shouldSearchBedByBedTypeAndLocationUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("bedType", "deluxe");
		request.addParameter("locationUuid", "98bc9b32-9d1a-11e2-8137-0800271c1b75");
		SimpleObject object = deserialize(handle(request));
		List results = (ArrayList) object.get("results");
		
		Assertions.assertEquals(2, results.size());
		Assertions.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ec", PropertyUtils.getProperty(results.get(0), "uuid"));
		Object bedType = PropertyUtils.getProperty(results.get(0), "bedType");
		Assertions.assertEquals("deluxe", PropertyUtils.getProperty(bedType, "name"));
		Assertions.assertEquals("bb094d57-d225-11e4-9c67-080027b662ec", PropertyUtils.getProperty(results.get(1), "uuid"));
		Assertions.assertEquals("AVAILABLE", PropertyUtils.getProperty(results.get(1), "status"));
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
		
		Assertions.assertNotNull(bed.get("id"));
		Assertions.assertEquals("110-a", bed.get("bedNumber"));
		Assertions.assertEquals(Integer.valueOf(4), bed.get("row"));
		Assertions.assertEquals(Integer.valueOf(1), bed.get("column"));
		Assertions.assertEquals("luxury", PropertyUtils.getProperty(bed.get("bedType"), "name"));
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
		
		Assertions.assertNotNull(bed.get("id"));
		Assertions.assertEquals("110-a", bed.get("bedNumber"));
		Assertions.assertEquals(Integer.valueOf(2), bed.get("row"));
		Assertions.assertEquals(Integer.valueOf(3), bed.get("column"));
		Assertions.assertEquals("luxury", PropertyUtils.getProperty(bed.get("bedType"), "name"));
	}
	
	@Test
	public void shouldThrowExceptionOnAlreadyAssignedBedPosition() throws Exception {
		assertThrows(IllegalPropertyException.class, () -> {
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
		});
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
		
		Assertions.assertEquals("307-ab", bed.get("bedNumber"));
		Assertions.assertEquals(Integer.valueOf(2), bed.get("row"));
		Assertions.assertEquals(Integer.valueOf(3), bed.get("column"));
		Assertions.assertEquals("luxury", PropertyUtils.getProperty(bed.get("bedType"), "name"));
	}
	
	@Test
	public void shouldFailToDeleteOccupiedBed() throws Exception {
		assertThrows(IllegalPropertyException.class, () -> {
			MockHttpServletRequest deleteRequest = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
			deleteRequest.setParameter("reason", "not needed");
			handle(deleteRequest);
		});
	}
	
	@Test
	public void shouldDeleteBed() throws Exception {
		assertThrows(ObjectNotFoundException.class, () -> {
			MockHttpServletRequest deleteRequest = request(RequestMethod.DELETE, getURI() + "/" + AVAILABLE_BED_UUID);
			deleteRequest.setParameter("reason", "not needed");
			handle(deleteRequest);
			
			MockHttpServletRequest getRequest = request(RequestMethod.GET, getURI() + "/" + AVAILABLE_BED_UUID);
			System.out.println(deserialize(handle(getRequest)));
		});
	}
}
