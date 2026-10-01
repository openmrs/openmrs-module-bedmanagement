package org.openmrs.module.bedmanagement.rest.resource;

import org.apache.commons.beanutils.PropertyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BedTypeResourceTest extends MainResourceControllerTest {
	
	@BeforeEach
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
		
		Assertions.assertEquals(3, results.size());
		Assertions.assertEquals("deluxe", PropertyUtils.getProperty(results.get(0), "name"));
		Assertions.assertEquals("luxury", PropertyUtils.getProperty(results.get(1), "name"));
		Assertions.assertEquals("normal", PropertyUtils.getProperty(results.get(2), "name"));
	}
	
	@Test
	public void shouldReturnBedTypeById() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject bedType = deserialize(handle(request));
		
		Assertions.assertEquals("6f9faf08-0fd5-11e8-adb7-080027b38971", bedType.get("uuid"));
		Assertions.assertEquals("deluxe", bedType.get("name"));
	}
	
	@Test
	public void shouldSearchBedTypeByName() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("name", "luxury");
		SimpleObject object = deserialize(handle(request));
		List results = (ArrayList) object.get("results");
		
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals("6f9fb240-0fd5-11e8-adb7-080027b38973", PropertyUtils.getProperty(results.get(0), "uuid"));
		Assertions.assertEquals("luxury", PropertyUtils.getProperty(results.get(0), "name"));
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
		
		Assertions.assertNotNull(bedType.get("uuid"));
		assertEquals("Large Bed", bedType.get("name"));
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
		
		Assertions.assertEquals("6f9faf08-0fd5-11e8-adb7-080027b38971", bedType.get("uuid"));
		Assertions.assertEquals("Vip Bed", bedType.get("name"));
		Assertions.assertEquals("VIP", bedType.get("displayName"));
	}
	
	@Test
	public void onDeleteBedTypeByIdShouldThrowException() {
		try {
			MockHttpServletRequest request = request(RequestMethod.DELETE,
			    getURI() + "/6f9fb240-0fd5-11e8-adb7-080027b38973");
			handle(request);
		}
		catch (Exception e) {
			Throwable t = e.getCause();
			assertTrue(t instanceof ConstraintViolationException);
		}
	}
	
	@Test
	public void shouldDeleteNewBedType() throws Exception {
		assertThrows(ObjectNotFoundException.class, () -> {
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
		});
	}
	
	@Test
	public void shouldRetireBedType() throws Exception {
		String uuid = "6f9fb341-0fd5-11e8-adb7-080027b38972";
		
		MockHttpServletRequest retireRequest = request(RequestMethod.POST, getURI() + "/" + uuid);
		SimpleObject retireParameters = new SimpleObject();
		retireParameters.put("retired", "true");
		retireParameters.put("retiredReason", "Retired Reason");
		String retireJson = new ObjectMapper().writeValueAsString(retireParameters);
		retireRequest.setContent(retireJson.getBytes());
		handle(retireRequest);
		
		MockHttpServletRequest getRequest = request(RequestMethod.GET, getURI());
		SimpleObject object = deserialize(handle(getRequest));
		List results = object.get("results");
		assertEquals(results.size(), 2);
	}
	
	@Test
	public void shouldThrowExceptionWhenRetireReasonIsBlank() throws Exception {
		assertThrows(APIException.class, () -> {
			String uuid = "6f9fb341-0fd5-11e8-adb7-080027b38972";
			
			MockHttpServletRequest retireRequest = request(RequestMethod.POST, getURI() + "/" + uuid);
			SimpleObject retireParameters = new SimpleObject();
			retireParameters.put("retired", "true");
			String retireJson = new ObjectMapper().writeValueAsString(retireParameters);
			retireRequest.setContent(retireJson.getBytes());
			handle(retireRequest);
		});
	}
}
