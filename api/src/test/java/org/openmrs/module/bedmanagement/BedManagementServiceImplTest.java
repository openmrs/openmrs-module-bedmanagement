package org.openmrs.module.bedmanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.service.impl.BedManagementServiceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BedManagementServiceImplTest {

	BedManagementServiceImpl bedManagementService;

	@Mock
	BedManagementDao bedManagementDao;

	@BeforeEach
	public void setup() {
		bedManagementService = new BedManagementServiceImpl();
		bedManagementService.setDao(bedManagementDao);
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
	public void shouldCallDeleteBedLocationMappingWhenLocationMappedBedIsDeleted() {
		try (MockedStatic<Context> mockedContext = Mockito.mockStatic(Context.class)) {
			Bed bed = new Bed();
			bed.setBedNumber("bedNumber");
			bed.setId(1);
			bed.setStatus(BedStatus.AVAILABLE.name());
			BedLocationMapping bedLocationMapping = mock(BedLocationMapping.class);
			User user = mock(User.class);

			when(bedManagementDao.getBedLocationMappingByBed(bed)).thenReturn(bedLocationMapping);
			doNothing().when(bedManagementDao).deleteBedLocationMapping(bedLocationMapping);
			mockedContext.when(Context::getAuthenticatedUser).thenReturn(user);

			bedManagementService.deleteBed(bed, "test");

			verify(bedManagementDao, times(1)).deleteBedLocationMapping(bedLocationMapping);
		}
	}

	@Test
	public void shouldNotCallDeleteBedLocationMappingWhenDeletedBedIsNotMappedToAnyLocation() {
		try (MockedStatic<Context> mockedContext = Mockito.mockStatic(Context.class)) {

			Bed bed = new Bed();
			bed.setBedNumber("bedNumber");
			bed.setId(1);
			bed.setStatus(BedStatus.AVAILABLE.name());
			User user = mock(User.class);

			when(bedManagementDao.getBedLocationMappingByBed(bed)).thenReturn(null);
			mockedContext.when(Context::getAuthenticatedUser).thenReturn(user);

			bedManagementService.deleteBed(bed, "test");

			verify(bedManagementDao, times(0)).deleteBedLocationMapping(any(BedLocationMapping.class));
		}
	}

	@Test
	public void shouldRetireBedType() {
		try (MockedStatic<Context> mockedContext = Mockito.mockStatic(Context.class)) {
			BedType bedType = new BedType();
			bedType.setName("Large");
			bedType.setDisplayName("L");
			bedType.setDescription("Large bed");

			User authUser = mock(User.class);
			mockedContext.when(Context::getAuthenticatedUser).thenReturn(authUser);

			when(bedManagementDao.saveBedType(any(BedType.class))).thenAnswer(inv -> inv.getArgument(0));

			BedType retired = bedManagementService.retireBedType(bedType, "Duplicate entry");

			assertEquals(Boolean.TRUE, retired.getRetired());
			assertEquals("Duplicate entry", retired.getRetireReason());
			assertEquals(authUser, retired.getRetiredBy());
			assertNotNull(retired.getDateRetired());

			verify(bedManagementDao, times(1)).saveBedType(retired);
		}
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
