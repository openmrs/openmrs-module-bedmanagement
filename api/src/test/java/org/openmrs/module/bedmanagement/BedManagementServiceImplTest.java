package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.dao.impl.BedManagementDaoImpl;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.service.impl.BedManagementServiceImpl;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class BedManagementServiceImplTest {
	
	private BedManagementServiceImpl bedManagementService;
	
	@Mock
	private BedManagementDao bedManagementDao;
	
	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private Session session;
	
	private BedManagementDaoImpl realBedManagementDao;
	
	private Bed dummyBed;
	
	private BedPatientAssignment dummyAssignment;
	
	@Before
	public void setup() {
		bedManagementService = new BedManagementServiceImpl();
		bedManagementService.setDao(bedManagementDao);
		
		realBedManagementDao = spy(new BedManagementDaoImpl());
		realBedManagementDao.setSessionFactory(sessionFactory);
		
		when(sessionFactory.getCurrentSession()).thenReturn(session);
		
		dummyBed = new Bed();
		dummyBed.setId(1);
		dummyBed.setBedNumber("B101");
		
		dummyAssignment = new BedPatientAssignment();
		dummyAssignment.setBed(dummyBed);
		dummyAssignment.setPatient(new Patient());
		
		Query<BedPatientAssignment> assignmentQuery = mock(Query.class);
		when(session.createQuery(contains("FROM BedPatientAssignment"), eq(BedPatientAssignment.class)))
		        .thenReturn(assignmentQuery);
		when(assignmentQuery.setParameter(anyString(), any())).thenReturn(assignmentQuery);
		when(assignmentQuery.setParameter(anyInt(), any())).thenReturn(assignmentQuery);
		when(assignmentQuery.list()).thenReturn(Collections.singletonList(dummyAssignment));
		when(assignmentQuery.uniqueResult()).thenReturn(dummyAssignment);
		
		Query<Bed> bedQuery = mock(Query.class);
		when(session.createQuery(contains("FROM Bed"), eq(Bed.class))).thenReturn(bedQuery);
		when(bedQuery.setParameter(anyString(), any())).thenReturn(bedQuery);
		when(bedQuery.setParameter(anyInt(), any())).thenReturn(bedQuery);
		when(bedQuery.uniqueResult()).thenReturn(dummyBed);
		
		when(bedManagementDao.getBedById(any(Integer.class))).thenReturn(new Bed());
	}
	
	@Test
	public void shouldCallGetBedByPatient() {
		Patient patient = new Patient();
		patient.setUuid(UUID.randomUUID().toString());
		
		doCallRealMethod().when(realBedManagementDao).getBedByPatient(any(Patient.class));
		realBedManagementDao.getBedByPatient(patient);
		
		verify(sessionFactory).getCurrentSession();
		verify(session, atLeastOnce()).createQuery(any(String.class));
	}
	
	@Test
	public void shouldCallGetBedPatientAssignmentByUuid() {
		String uuid = UUID.randomUUID().toString();
		
		doCallRealMethod().when(realBedManagementDao).getBedPatientAssignmentByUuid(any(String.class));
		realBedManagementDao.getBedPatientAssignmentByUuid(uuid);
		
		verify(sessionFactory).getCurrentSession();
		verify(session, atLeastOnce()).createQuery(any(String.class));
	}
	
	@Test
	public void shouldCallGetCurrentAssignmentsByBed() {
		Bed bed = new Bed();
		bed.setId(1);
		
		doCallRealMethod().when(realBedManagementDao).getCurrentAssignmentsByBed(any(Bed.class));
		realBedManagementDao.getCurrentAssignmentsByBed(bed);
		
		verify(sessionFactory).getCurrentSession();
		verify(session, atLeastOnce()).createQuery(any(String.class));
	}
	
	@Test
	public void shouldCallGetLatestBedByVisit() {
		String visitUuid = UUID.randomUUID().toString();
		
		doCallRealMethod().when(realBedManagementDao).getLatestBedByVisit(any(String.class));
		realBedManagementDao.getLatestBedByVisit(visitUuid);
		
		verify(sessionFactory).getCurrentSession();
		verify(session, atLeastOnce()).createQuery(any(String.class));
	}
	
	@Test
	public void should_get_layouts_for_ward() {
		String wardId = "123";
		
		Location location = new Location();
		location.setUuid(wardId);
		
		bedManagementService.getAdmissionLocationByLocation(location);
		
		verify(bedManagementDao).getAdmissionLocationForLocation(location);
	}
	
	@Test
	public void shouldGetBedDetailsWithPatientInformationById() {
		int bedId = 5;
		Location ward = new Location();
		Patient patient = createPatient("GAN123", "uuid", "given", "middle", "last");
		Bed bed = new Bed();
		bed.setBedNumber("bedNumber");
		bed.setId(1);
		bed.setStatus(BedStatus.OCCUPIED.name());
		bed.setBedNumber("bedNumber");
		
		BedPatientAssignment currentAssignment = new BedPatientAssignment();
		currentAssignment.setBed(bed);
		currentAssignment.setPatient(patient);
		currentAssignment.setStartDatetime(new Date());
		currentAssignment.setEndDatetime(null);
		BedPatientAssignment previousAssignment = new BedPatientAssignment();
		previousAssignment.setBed(bed);
		previousAssignment.setPatient(patient);
		previousAssignment.setStartDatetime(new Date());
		previousAssignment.setEndDatetime(new Date());
		
		when(bedManagementDao.getBedById(bedId)).thenReturn(bed);
		when(bedManagementDao.getWardForBed(bed)).thenReturn(ward);
		when(bedManagementDao.getCurrentAssignmentsByBed(bed)).thenReturn(Arrays.asList(currentAssignment));
		
		BedDetails bedDetails = bedManagementService.getBedDetailsById(String.valueOf(bedId));
		
		Patient patient1 = bedDetails.getPatients().get(0);
		assertEquals("GAN123", patient1.getPatientIdentifier().getIdentifier());
		assertEquals("given middle last", patient1.getPersonName().getFullName());
		assertEquals(patient.getGender(), patient1.getGender());
	}
	
	@Test
	public void shouldGetBedDetailsWithMultiplePatientInformationsById() {
		int bedId = 5;
		Location ward = new Location();
		Patient patient1 = createPatient("GAN123", "uuid1", "first1", "middle1", "last1");
		Patient patient2 = createPatient("GAN456", "uuid2", "first2", "middle2", "last2");
		Bed bed = new Bed();
		bed.setBedNumber("bedNumber");
		bed.setId(1);
		bed.setStatus(BedStatus.OCCUPIED.name());
		bed.setBedNumber("bedNumber");
		
		BedPatientAssignment currentAssignment1 = new BedPatientAssignment();
		currentAssignment1.setBed(bed);
		currentAssignment1.setPatient(patient1);
		currentAssignment1.setStartDatetime(new Date());
		currentAssignment1.setEndDatetime(null);
		
		BedPatientAssignment currentAssignment2 = new BedPatientAssignment();
		currentAssignment2.setBed(bed);
		currentAssignment2.setPatient(patient2);
		currentAssignment2.setStartDatetime(new Date());
		currentAssignment2.setEndDatetime(null);
		
		BedPatientAssignment stoppedBedAssignment = new BedPatientAssignment();
		stoppedBedAssignment.setBed(bed);
		stoppedBedAssignment.setPatient(patient1);
		stoppedBedAssignment.setStartDatetime(new Date());
		stoppedBedAssignment.setEndDatetime(new Date());
		
		when(bedManagementDao.getBedById(bedId)).thenReturn(bed);
		when(bedManagementDao.getWardForBed(bed)).thenReturn(ward);
		when(bedManagementDao.getCurrentAssignmentsByBed(bed))
		        .thenReturn(Arrays.asList(currentAssignment1, currentAssignment2));
		
		BedDetails bedDetails = bedManagementService.getBedDetailsById(String.valueOf(bedId));
		
		assertEquals(2, bedDetails.getPatients().size());
		Patient actualPatient1 = bedDetails.getPatients().get(0);
		assertEquals("GAN123", actualPatient1.getPatientIdentifier().getIdentifier());
		assertEquals("first1 middle1 last1", actualPatient1.getPersonName().getFullName());
		assertEquals("M", actualPatient1.getGender());
		
		Patient actualPatient2 = bedDetails.getPatients().get(1);
		assertEquals("GAN456", actualPatient2.getPatientIdentifier().getIdentifier());
		assertEquals("first2 middle2 last2", actualPatient2.getPersonName().getFullName());
		assertEquals("M", actualPatient2.getGender());
	}
	
	@Test
	public void shouldReturnCurrentBedAssignmentWhenVoidedIsFalse() {
		Bed bed = new Bed();
		bed.setId(10);
		bed.setBedNumber("B201");
		
		BedPatientAssignment assignment = new BedPatientAssignment();
		assignment.setBed(bed);
		assignment.setVoided(false);
		
		when(bedManagementDao.getCurrentAssignmentsByBed(any(Bed.class))).thenReturn(Collections.singletonList(assignment));
		
		BedDetails bedDetails = bedManagementService.getBedDetailsById(String.valueOf(bed.getId()));
		
		assertNotNull(bedDetails);
		assertTrue(bedDetails.getPatients().size() >= 0);
	}
	
	@Test
	public void shouldNotReturnBedAssignmentWhenVoidedIsTrue() {
		Bed bed = new Bed();
		bed.setId(11);
		bed.setBedNumber("B202");
		
		when(bedManagementDao.getCurrentAssignmentsByBed(any(Bed.class))).thenReturn(Collections.emptyList());
		
		BedDetails bedDetails = bedManagementService.getBedDetailsById(String.valueOf(bed.getId()));
		
		assertNotNull(bedDetails);
		assertTrue(bedDetails.getPatients().isEmpty());
	}
	
	@Test
	public void shouldCallDeleteBedLocationMappingWhenLocationMappedBedIsDeleted() {
		Bed bed = new Bed();
		bed.setBedNumber("bedNumber");
		bed.setId(1);
		bed.setStatus(BedStatus.AVAILABLE.name());
		BedLocationMapping bedLocationMapping = mock(BedLocationMapping.class);
		mockStatic(Context.class);
		User user = mock(User.class);
		
		when(bedManagementDao.getBedLocationMappingByBed(bed)).thenReturn(bedLocationMapping);
		doNothing().when(bedManagementDao).deleteBedLocationMapping(bedLocationMapping);
		when(Context.getAuthenticatedUser()).thenReturn(user);
		
		bedManagementService.deleteBed(bed, "test");
		
		verify(bedManagementDao, times(1)).deleteBedLocationMapping(bedLocationMapping);
	}
	
	@Test
	public void shouldNotCallDeleteBedLocationMappingWhenDeletedBedIsNotMappedToAnyLocation() {
		Bed bed = new Bed();
		bed.setBedNumber("bedNumber");
		bed.setId(1);
		bed.setStatus(BedStatus.AVAILABLE.name());
		mockStatic(Context.class);
		User user = mock(User.class);
		
		when(bedManagementDao.getBedLocationMappingByBed(bed)).thenReturn(null);
		when(Context.getAuthenticatedUser()).thenReturn(user);
		
		bedManagementService.deleteBed(bed, "test");
		
		verify(bedManagementDao, times(0)).deleteBedLocationMapping(any(BedLocationMapping.class));
	}
	
	@Test
	public void shouldRetireBedType() {
		BedType bedType = new BedType();
		bedType.setName("Large");
		bedType.setDisplayName("L");
		bedType.setDescription("Large bed");
		
		mockStatic(Context.class);
		User authUser = mock(User.class);
		when(Context.getAuthenticatedUser()).thenReturn(authUser);
		
		when(bedManagementDao.saveBedType(any(BedType.class))).thenAnswer(inv -> inv.getArgument(0));
		
		BedType retired = bedManagementService.retireBedType(bedType, "Duplicate entry");
		
		assertEquals(Boolean.TRUE, retired.getRetired());
		assertEquals("Duplicate entry", retired.getRetireReason());
		assertEquals(authUser, retired.getRetiredBy());
		assertNotNull(retired.getDateRetired());
		
		verify(bedManagementDao, times(1)).saveBedType(retired);
		
	}
	
	@Test
	public void shouldThrowExceptionWhenRetireReasonIsBlank() {
		BedType bt = new BedType();
		assertThrows(APIException.class, () -> bedManagementService.retireBedType(bt, "  "));
		verifyNoInteractions(bedManagementDao);
	}
	
	private Patient createPatient(String identifierString, String patientUuid, String firstName, String middleName,
	        String lastName) {
		PatientIdentifier identifier = new PatientIdentifier(identifierString, new PatientIdentifierType(), new Location());
		identifier.setPreferred(true);
		PersonName personName = new PersonName(firstName, middleName, lastName);
		personName.setPreferred(true);
		Patient patient = new Patient();
		patient.setUuid(patientUuid);
		patient.setGender("M");
		patient.setIdentifiers(new HashSet<PatientIdentifier>(Arrays.asList(identifier)));
		patient.setNames(new HashSet<PersonName>(Arrays.asList(personName)));
		return patient;
	}
	
}
