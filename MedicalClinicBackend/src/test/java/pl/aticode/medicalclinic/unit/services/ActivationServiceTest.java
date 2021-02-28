package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import pl.aticode.medicalclinic.dao.user.PatientRepository;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.service.ActivationService;
import pl.aticode.medicalclinic.service.CipherService;
import pl.aticode.medicalclinic.service.HibernateSearchService;

class ActivationServiceTest {
	
	@InjectMocks
	private ActivationService activationService;
    @Mock
    private HibernateSearchService hibernateSearchService;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private CipherService cipherService;
    
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testCreateActivationLink() {
		Patient patient = new Patient();
		String activationString = "activationString123";
		patient.setActivationString(activationString);
		Mockito.when(cipherService.encodeString(activationString)).thenReturn(activationString);
		assertTrue(activationService.createActivationLink(patient).contains(activationString));
		assertTrue(activationService.createActivationLink(patient).contains("ACTIVATION LINK"));
	}

	@Test
	void testSetActivePatient() {
		User user = new User();
		Patient patient = new Patient();
		patient.setUser(user);
		patient.setRegisterDateTime(LocalDateTime.now().minusHours(1));
		Mockito.when(hibernateSearchService.searchPatientToActivation("activationString")).thenReturn(patient);
		assertTrue(activationService.setActivePatient("activationString"));
		patient.setRegisterDateTime(LocalDateTime.now().minusHours(7));
		assertFalse(activationService.setActivePatient("activationString"));
	}

}
