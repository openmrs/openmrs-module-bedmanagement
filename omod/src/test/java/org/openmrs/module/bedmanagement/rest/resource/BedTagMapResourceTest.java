package org.openmrs.module.bedmanagement.rest.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.webservices.rest.SimpleObject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BedTagMapResourceTest extends MainResourceControllerTest {
	
	@BeforeEach
	public void init() throws Exception {
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Override
	public String getURI() {
		return "bedTagMap";
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
	public void shouldAssociateATagToBedIfItIsNotAssignedToBed() throws Exception {
		String json = "{\"bed\":{\"id\": \"11\"}, \"bedTag\": {\"id\": \"3\"}}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		SimpleObject bedTagMap = deserialize(handle(newPostRequest(getURI(), post)));
		
		assertNotNull(bedTagMap);
		assertNotNull(bedTagMap.get("uuid"));
	}
	
	@Test
	public void shouldThrowAnExceptionIfTheTagWeAreTryingToAssociateIsAlreadyPresent() throws Exception {
		String json = "{\"bed\":{\"id\": \"11\"}, \"bedTag\": {\"id\": \"3\"}}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		SimpleObject bedTagMap = deserialize(handle(newPostRequest(getURI(), post)));
		assertNotNull(bedTagMap);
		assertNotNull(bedTagMap.get("uuid"));
		RuntimeException exception = assertThrows(RuntimeException.class,
		    () -> deserialize(handle(newPostRequest(getURI(), post))));
		assertThat(exception.getMessage(), containsString("Tag Already Present For Bed"));
	}
}
