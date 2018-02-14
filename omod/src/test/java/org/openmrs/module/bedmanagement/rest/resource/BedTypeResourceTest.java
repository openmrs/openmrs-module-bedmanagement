package org.openmrs.module.bedmanagement.rest.resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

public class BedTypeResourceTest extends MainResourceControllerTest {
	
	@Before
	public void init() throws Exception {
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Override
	public String getURI() {
		return "bedtype";
	}
	
	@Override
	public String getUuid() {
		return "6f9faf08-0fd5-11e8-adb7-080027b38971";
	}
	
	@Override
	public long getAllCount() {
		return 0;
	}
	
	@Test
	public void shouldReturnAllBedTypes() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		SimpleObject object = deserialize(handle(request));
		List results = (ArrayList) object.get("results");
		
		Assert.assertEquals(3, results.size());
		Assert.assertEquals("deluxe", PropertyUtils.getProperty(results.get(0), "name"));
		Assert.assertEquals("luxury", PropertyUtils.getProperty(results.get(1), "name"));
		Assert.assertEquals("normal", PropertyUtils.getProperty(results.get(2), "name"));
	}
	
	@Test
	public void shouldReturnBedTypeById() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject bedType = deserialize(handle(request));
		
		Assert.assertEquals("6f9faf08-0fd5-11e8-adb7-080027b38971", bedType.get("uuid"));
		Assert.assertEquals("deluxe", bedType.get("name"));
	}
	
	@Test
	public void shouldSearchBedTypeByName() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("name", "luxury");
		SimpleObject object = deserialize(handle(request));
		List results = (ArrayList) object.get("results");
		
		Assert.assertEquals(1, results.size());
		Assert.assertEquals("6f9fb240-0fd5-11e8-adb7-080027b38971", PropertyUtils.getProperty(results.get(0), "uuid"));
		Assert.assertEquals("luxury", PropertyUtils.getProperty(results.get(0), "name"));
	}
	
	@Test
	public void shouldAddNewBedType() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI());
		SimpleObject postParameters = new SimpleObject();
		postParameters.put("name", "Large Bed");
		postParameters.put("displayName", "LB");
		postParameters.put("description", "large size bed");
		String json = new ObjectMapper().writeValueAsString(postParameters);
		request.setContent(json.getBytes());
		SimpleObject bedType = deserialize(handle(request));
		
		Assert.assertNotNull(bedType.get("uuid"));
		Assert.assertEquals("Large Bed", bedType.get("name"));
	}
	
	@Test
	public void shouldUpdateBedTypeById() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		SimpleObject postParameters = new SimpleObject();
		postParameters.put("name", "Vip Bed");
		postParameters.put("displayName", "VIP");
		postParameters.put("description", "Vip beds of vip ward");
		String json = new ObjectMapper().writeValueAsString(postParameters);
		request.setContent(json.getBytes());
		SimpleObject bedType = deserialize(handle(request));
		
		Assert.assertEquals("6f9faf08-0fd5-11e8-adb7-080027b38971", bedType.get("uuid"));
		Assert.assertEquals("Vip Bed", bedType.get("name"));
		Assert.assertEquals("VIP", bedType.get("displayName"));
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void onDeleteBedTypeByIdShouldThrowException() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/6f9fb240-0fd5-11e8-adb7-080027b38971");
		handle(request);
	}
	
	@Test(expected = ObjectNotFoundException.class)
	public void shouldDeleteNewBedType() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI());
		SimpleObject postParameters = new SimpleObject();
		postParameters.put("name", "Large Bed");
		postParameters.put("displayName", "LB");
		postParameters.put("description", "large size bed");
		String json = new ObjectMapper().writeValueAsString(postParameters);
		request.setContent(json.getBytes());
		SimpleObject bedType = deserialize(handle(request));
		Integer bedTypeId = bedType.get("id");
		
		MockHttpServletRequest deleteRequest = request(RequestMethod.DELETE, getURI() + "/" + bedTypeId);
		handle(deleteRequest);
		
		MockHttpServletRequest getRequest = request(RequestMethod.GET, getURI() + "/" + bedTypeId);
		handle(getRequest);
	}
}
