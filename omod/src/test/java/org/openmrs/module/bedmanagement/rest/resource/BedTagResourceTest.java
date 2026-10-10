package org.openmrs.module.bedmanagement.rest.resource;

import org.apache.commons.beanutils.PropertyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BedTagResourceTest extends MainResourceControllerTest {
	
	@BeforeEach
	public void init() throws Exception {
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Override
	public String getURI() {
		return "bedTag";
	}
	
	@Override
	public String getUuid() {
		return "73e846d6-ed5f-11e6-a3c9-0800274a5156";
	}
	
	@Override
	public long getAllCount() {
		return 0;
	}
	
	@Test
	public void shouldGelAllNonVoidedBedTags() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		SimpleObject object = deserialize(handle(request));
		
		List results = (List) object.get("results");
		LinkedHashMap brokenBedTg = (LinkedHashMap) results.get(0);
		LinkedHashMap isolationBedTag = (LinkedHashMap) results.get(2);
		
		assertEquals(4, results.size());
		
		assertEquals(1, brokenBedTg.get("id"));
		assertEquals("Broken", brokenBedTg.get("name"));
		assertEquals("73e846d6-ed5f-11e6-a3c9-0800274a5156", brokenBedTg.get("uuid"));
		
		assertEquals(3, isolationBedTag.get("id"));
		assertEquals("Isolation", isolationBedTag.get("name"));
		assertEquals("73e846d6-ed5f-33e6-a3c9-0800274a5156", isolationBedTag.get("uuid"));
	}
	
	@Test
	public void shouldGetBedTagsByUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject bedTag = deserialize(handle(request));
		
		Assertions.assertEquals("73e846d6-ed5f-11e6-a3c9-0800274a5156", bedTag.get("uuid"));
		Assertions.assertEquals("Broken", bedTag.get("name"));
	}
	
	@Test
	public void shouldSearchBedTagByName() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("name", "Isolation");
		SimpleObject object = deserialize(handle(request));
		List results = (ArrayList) object.get("results");
		
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals("73e846d6-ed5f-33e6-a3c9-0800274a5156", PropertyUtils.getProperty(results.get(0), "uuid"));
		Assertions.assertEquals("Isolation", PropertyUtils.getProperty(results.get(0), "name"));
	}
	
	@Test
	public void shouldAddNewBedTag() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI());
		SimpleObject postParameters = new SimpleObject();
		postParameters.put("name", "Reserved");
		String json = new ObjectMapper().writeValueAsString(postParameters);
		request.setContent(json.getBytes());
		SimpleObject bedTag = deserialize(handle(request));
		
		Assertions.assertNotNull(bedTag.get("id"));
		Assertions.assertEquals("Reserved", bedTag.get("name"));
	}
	
	@Test
	public void shouldUpdateBedTag() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		SimpleObject postParameters = new SimpleObject();
		postParameters.put("name", "Blocked");
		String json = new ObjectMapper().writeValueAsString(postParameters);
		request.setContent(json.getBytes());
		SimpleObject bedTag = deserialize(handle(request));
		
		Assertions.assertEquals("Blocked", bedTag.get("name"));
	}
	
	@Test
	public void shouldDeleteBedTag() throws Exception {
		assertThrows(ObjectNotFoundException.class, () -> {
			MockHttpServletRequest deleteRequest = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
			deleteRequest.setParameter("reason", "not needed");
			handle(deleteRequest);
			
			MockHttpServletRequest getRequest = request(RequestMethod.GET, getURI() + "/" + getUuid());
			deserialize(handle(getRequest));
		});
	}
}
