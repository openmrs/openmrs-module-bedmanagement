package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BedManagementServiceImplTest {

    BedManagementServiceImpl bedManagementService;
    @Mock
    BedManagementDAO bedManagementDAO;

    @Before
    public void setup() {
        bedManagementService = new BedManagementServiceImpl();
        bedManagementService.setDao(bedManagementDAO);
    }

    @Test
    public void should_get_layouts_for_ward() {
        String wardId = "123";

        Location location = new Location();
        location.setUuid(wardId);

        bedManagementService.getLayoutForWard(location);

        verify(bedManagementDAO).getLayoutForWard(location);
    }

    @Test
    public void should_be_able_to_assign_patient_to_bed() {
        Patient patient = new Patient();
        Bed bed = new Bed();
        int bedId = 9;
        when(bedManagementDAO.getBedById(bedId)).thenReturn(bed);

        bedManagementService.assignPatientToBed(patient, String.valueOf(bedId));

        verify(bedManagementDAO).assignPatientToBed(patient, bed);
    }

    @Test
    public void shouldGetBedDetailsWithPatientInformationById() {
        int bedId = 5;
        Location ward = new Location();
        PatientIdentifier identifier = new PatientIdentifier("GAN123", new PatientIdentifierType(), new Location());
        identifier.setPreferred(true);
        PersonName personName = new PersonName("given", "middle", "last");
        personName.setPreferred(true);
        Patient patient = new Patient();
        patient.setUuid("uuid");
        patient.setGender("M");
        patient.setIdentifiers(new HashSet<PatientIdentifier>(Arrays.asList(identifier)));
        patient.setNames(new HashSet<PersonName>(Arrays.asList(personName)));
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
        bed.setBedPatientAssignment(new HashSet<BedPatientAssignment>(Arrays.asList(currentAssignment, previousAssignment)));

        when(bedManagementDAO.getBedById(bedId)).thenReturn(bed);
        when(bedManagementDAO.getWardForBed(bed)).thenReturn(ward);

        BedDetails bedDetails = bedManagementService.getBedDetailsById(String.valueOf(bedId));

        assertEquals(identifier.getIdentifier(), bedDetails.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals(personName.getFullName(), bedDetails.getPatient().getPersonName().getFullName());
        assertEquals(patient.getGender(), bedDetails.getPatient().getGender());
    }
}
