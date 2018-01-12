package org.openmrs.module.bedmanagement.rest.resource;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BedDetailsResourceSearchHandlerTest extends MainResourceControllerTest {
	
	@Before
	public void init() throws Exception {
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Override
	public String getURI() {
		return "beds";
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
	public void shouldReturnBedDetailsByVisitUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("visitUuid", "8cfda6ae-6b78-11e0-93c3-18a905e044dc");
		
		SimpleObject object = deserialize(handle(request));
		List list = (List) object.get("results");
		LinkedHashMap<String, String> bedDetails = (LinkedHashMap<String, String>) list.get(0);
		
		assertEquals(12, bedDetails.get("bedId"));
		assertEquals("307-b", bedDetails.get("bedNumber"));
	}
	
	@Test
	public void shouldReturnNothingWhenVisitHasNoBedsAssigned() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("visitUuid", "8cfda6ae-6b78-11e0-93c3-a905e044dc");
		
		SimpleObject object = deserialize(handle(request));
		List list = (List) object.get("results");
		
		assertEquals(0, list.size());
	}
}
