package org.openmrs.module.bedmanagement.rest.resource;

import org.apache.commons.beanutils.PropertyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openmrs.module.bedmanagement.constants.BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION;

public class AdmissionLocationResourceTest extends MainResourceControllerTest {
	
	@Autowired
	private LocationService locationService;
	
	@BeforeEach
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
	public void shouldGetPatientInfoAlongWithLayoutIfTheBedInTheLayoutIsOccupiedWhenTheRepresentionIsFull()
	        throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		request.setParameter("v", "full");
		SimpleObject object = deserialize(handle(request));
		
		List bedLayouts = (List) object.get("bedLayouts");
		LinkedHashMap<String, Object> occupiedBed = (LinkedHashMap<String, Object>) ((List) object.get("bedLayouts")).get(0);
		LinkedHashMap<String, Object> unOccupiedBed = (LinkedHashMap<String, Object>) ((List) object.get("bedLayouts"))
		        .get(2);
		List<Map<String, Object>> patients = (List<Map<String, Object>>) occupiedBed.get("patients");
		
		assertEquals(6, bedLayouts.size());
		assertEquals("OCCUPIED", occupiedBed.get("status"));
		assertEquals("307-a", occupiedBed.get("bedNumber"));
		assertNotNull(patients);
		assertEquals(patients.size(), 1);
		{
			Map patient = patients.get(0);
			assertEquals("2b597be0-83c7-4f1d-b3d2-1d61ab128762", patient.get("uuid"));
			Map person = (Map) patient.get("person");
			assertNotNull(person);
			assertEquals("F", person.get("gender"));
			assertTrue(person.containsKey("age"));
			assertNull(person.get("age"));
			Map preferredName = (Map) person.get("preferredName");
			assertNotNull(preferredName);
			assertEquals("John", preferredName.get("givenName"));
			assertEquals("Doe", preferredName.get("familyName"));
			assertTrue(person.containsKey("preferredAddress"));
			List identifiers = (List) patient.get("identifiers");
			assertEquals(1, identifiers.size());
			Map identifier = (Map) identifiers.get(0);
			assertEquals("1234", identifier.get("identifier"));
		}
		
		List<Object> bedTagMapsForOccupiedBed = (List<Object>) occupiedBed.get("bedTagMaps");
		assertEquals(2, bedTagMapsForOccupiedBed.size());
		for (Object bedTagMap : bedTagMapsForOccupiedBed) {
			Map m = (Map) bedTagMap;
			String uuid = (String) m.get("uuid");
			String tagName = (String) ((Map) m.get("bedTag")).get("name");
			if (uuid.equals("73e846d6-ed5f-55e6-a3c9-0800274a1111")) {
				assertEquals("Broken", tagName);
			} else if (uuid.equals("73e846d6-ed5f-55e6-a3c9-0800274a2222")) {
				assertEquals("Oxygen", tagName);
			} else {
				Assertions.fail("Unexpected bedTagMap: " + bedTagMap);
			}
		}
		
		assertEquals("AVAILABLE", unOccupiedBed.get("status"));
		assertEquals("307-c", unOccupiedBed.get("bedNumber"));
		assertNotNull(unOccupiedBed.get("patients"));
		assertEquals(((List) unOccupiedBed.get("patients")).size(), 0);
		List<Object> bedTagMapsForUnOccupiedBed = (List<Object>) unOccupiedBed.get("bedTagMaps");
		assertEquals(1, bedTagMapsForUnOccupiedBed.size());
		{
			Map bedTagMap = (Map) bedTagMapsForUnOccupiedBed.get(0);
			assertEquals("73e846d6-ed5f-55e6-a3c9-0800274a3333", bedTagMap.get("uuid"));
			assertEquals("Oxygen", ((Map) bedTagMap.get("bedTag")).get("name"));
		}
	}
	
	@Test
	public void shouldGetWardAlongWithTotalBedsAndOccupiedBedsWhenTheRepresentionIsDefault() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		SimpleObject object = deserialize(handle(request));
		
		List results = (List) object.get("results");
		LinkedHashMap<String, Object> cardioWardOnFirstFloor = (LinkedHashMap<String, Object>) ((List) object.get("results"))
		        .get(0);
		LinkedHashMap<String, Object> orthopaedicWard = (LinkedHashMap<String, Object>) ((List) object.get("results"))
		        .get(2);
		LinkedHashMap<String, Object> cardioWardOnFirstFloorInfo = (LinkedHashMap<String, Object>) cardioWardOnFirstFloor
		        .get("ward");
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
		LinkedHashMap location2 = (LinkedHashMap) results.get(1);
		LinkedHashMap location3 = (LinkedHashMap) results.get(2);
		
		Assertions.assertEquals(3, results.size());
		Assertions.assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac",
		    PropertyUtils.getProperty(location1.get("ward"), "uuid"));
		Assertions.assertTrue(location1.containsKey("ward"));
		Assertions.assertTrue(location1.containsKey("totalBeds"));
		Assertions.assertTrue(location1.containsKey("occupiedBeds"));
		Assertions.assertEquals("e26cea2c-1b9f-666e-6511-f3ef6c88af6f",
		    PropertyUtils.getProperty(location2.get("ward"), "uuid"));
		Assertions.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9",
		    PropertyUtils.getProperty(location3.get("ward"), "uuid"));
	}
	
	@Test
	public void shouldReturnAdmissionLocationByUuidWithFullRepresentation() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		request.setParameter("v", "full");
		SimpleObject location = deserialize(handle(request));
		
		Assertions.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9",
		    PropertyUtils.getProperty(location.get("ward"), "uuid"));
		Assertions.assertEquals(Integer.valueOf(6), location.get("totalBeds"));
		Assertions.assertEquals(Integer.valueOf(2), location.get("occupiedBeds"));
		Assertions.assertTrue(location.containsKey("bedLayouts"));
		List bedLayouts = (ArrayList) location.get("bedLayouts");
		Assertions.assertEquals(6, bedLayouts.size());
		
		MockHttpServletRequest request2 = request(RequestMethod.GET, getURI() + "/98bc9b32-9d1a-11e2-8137-0800271c1b75");
		SimpleObject location2 = deserialize(handle(request2));
		
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75",
		    PropertyUtils.getProperty(location2.get("ward"), "uuid"));
		Assertions.assertEquals(Integer.valueOf(10), location2.get("totalBeds"));
		Assertions.assertEquals(Integer.valueOf(1), location2.get("occupiedBeds"));
		Assertions.assertFalse(location2.containsKey("bedLayouts"));
	}
	
	@Test
	public void shouldReturnAdmissionLocationLayoutByUuidWithLayoutRepresentation() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/98bc9b32-9d1a-11e2-8137-0800271c1b75");
		request.setParameter("v", "layout");
		SimpleObject admissionLocation = deserialize(handle(request));
		
		Assertions.assertTrue(admissionLocation.containsKey("ward"));
		Assertions.assertTrue(admissionLocation.containsKey("bedLocationMappings"));
		
		List bedLocationMappings = (ArrayList) admissionLocation.get("bedLocationMappings");
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75",
		    PropertyUtils.getProperty(admissionLocation.get("ward"), "uuid"));
		Assertions.assertEquals(18, bedLocationMappings.size());
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
		
		Assertions.assertEquals("VIPs Ward", PropertyUtils.getProperty(admissionLocation.get("ward"), "name"));
		Assertions.assertNull(PropertyUtils.getProperty(admissionLocation.get("ward"), "childLocations"));
		List tags = (ArrayList) PropertyUtils.getProperty(admissionLocation.get("ward"), "tags");
		Assertions.assertEquals("Admission Location", PropertyUtils.getProperty(tags.get(0), "display"));
		Assertions.assertEquals(Integer.valueOf(0), admissionLocation.get("totalBeds"));
		Assertions.assertEquals(Integer.valueOf(0), admissionLocation.get("occupiedBeds"));
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
		
		Assertions.assertEquals("VIPs Ward", PropertyUtils.getProperty(admissionLocation.get("ward"), "name"));
		List tags = (ArrayList) PropertyUtils.getProperty(admissionLocation.get("ward"), "tags");
		Assertions.assertEquals("Admission Location", PropertyUtils.getProperty(tags.get(0), "display"));
		Assertions.assertNotNull(PropertyUtils.getProperty(admissionLocation.get("ward"), "parentLocation"));
		HashMap parentAdmissionLocation = (LinkedHashMap) PropertyUtils.getProperty(admissionLocation.get("ward"),
		    "parentLocation");
		Assertions.assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac", parentAdmissionLocation.get("uuid"));
		Assertions.assertEquals(Integer.valueOf(0), admissionLocation.get("totalBeds"));
		Assertions.assertEquals(Integer.valueOf(0), admissionLocation.get("occupiedBeds"));
	}
	
	@Test
	public void shouldUpdateAdmissionLocation() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		SimpleObject postParameters = new SimpleObject();
		postParameters.put("name", "VIPs Ward");
		String json = new ObjectMapper().writeValueAsString(postParameters);
		request.setContent(json.getBytes());
		SimpleObject admissionLocation = deserialize(handle(request));
		
		Assertions.assertEquals("VIPs Ward", PropertyUtils.getProperty(admissionLocation.get("ward"), "name"));
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
		
		List bedLocationMappings = (ArrayList) admissionLocation.get("bedLocationMappings");
		Assertions.assertEquals("e26cea2c-1b9f-666e-6511-f3ef6c88af6f",
		    PropertyUtils.getProperty(admissionLocation.get("ward"), "uuid"));
		Assertions.assertEquals(6, bedLocationMappings.size());
	}
	
	@Test
	public void shouldGiveCorrectErrorWithNoAdmissionLocationTagDefined() throws Exception {
		assertThrows(IllegalStateException.class, () -> {
			LocationTag needToHide = locationService.getLocationTagByName(LOCATION_TAG_SUPPORTS_ADMISSION);
			needToHide.setName("A different tag");
			locationService.saveLocationTag(needToHide);
			
			MockHttpServletRequest request = request(RequestMethod.POST, getURI());
			SimpleObject postParameters = new SimpleObject();
			postParameters.put("name", "VIPs Ward");
			postParameters.put("description", "ward for vip person");
			String json = new ObjectMapper().writeValueAsString(postParameters);
			request.setContent(json.getBytes());
			
			// OpenMRS's REST framework just throws an exception here rather than actually
			// returning a response with the real error code. It's not worth modifying the
			// webservices.rest module just to test this case properly, so this test just
			// expects the exception. In real life this returns status 500 with body like:
			// {"error":{"message":"[Server must be configured with a Location Tag named
			// 'Admission Location'.]",
			// "code":"org.openmrs.module.bedmanagement.rest.resource.AdmissionLocationResource:179",
			// "detail":"java.lang.IllegalStateException: Server must be configured with a
			// Location Tag named
			// 'Admission Location'.\n\tat
			// org.openmrs.module.bedmanagement.rest.resource.AdmissionLocationResource.create(AdmissionLocationResource.java:179)\n\tat
			// org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController.create(MainResourceController.java:92)\n\tat
			// etc
			handle(request);
		});
	}
	
	@Test
	public void shouldSupportCustomRepresentation() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		request.setParameter("v", "custom:(ward:(name),bedLayouts:(bedNumber,bedTagMaps:(bedTag:(name))))");
		SimpleObject object = deserialize(handle(request));
		System.out.println(object);
		// each level of a custom representation also carries resourceVersion
		// (RESTWS-1012, REST 3.1.0+)
		Assertions.assertEquals(3, object.keySet().size());
		Map<String, Object> ward = object.get("ward");
		Assertions.assertNotNull(ward);
		Assertions.assertEquals(2, ward.keySet().size());
		Assertions.assertEquals("Orthopaedic ward", ward.get("name"));
		List<Map<String, Object>> bedLayouts = object.get("bedLayouts");
		Assertions.assertNotNull(bedLayouts);
		Assertions.assertEquals(6, bedLayouts.size());
		for (Map<String, Object> bedLayout : bedLayouts) {
			Assertions.assertEquals(3, bedLayout.size());
			String bedNumber = (String) bedLayout.get("bedNumber");
			Assertions.assertNotNull(bedNumber);
			if (bedNumber.equals("307-a")) {
				List<Map<String, Object>> bedTagMaps = (List<Map<String, Object>>) bedLayout.get("bedTagMaps");
				Assertions.assertEquals(2, bedTagMaps.size());
				for (Map<String, Object> bedTagMap : bedTagMaps) {
					Assertions.assertEquals(2, bedTagMap.size());
					Map<String, Object> bedTag = (Map<String, Object>) bedTagMap.get("bedTag");
					Assertions.assertNotNull(bedTag);
					Assertions.assertEquals(2, bedTag.size());
					String bedTagName = (String) bedTag.get("name");
					Assertions.assertNotNull(bedTagName);
					Assertions.assertTrue(bedTagName.equals("Oxygen") || bedTagName.equals("Broken"));
				}
			}
		}
	}
}
