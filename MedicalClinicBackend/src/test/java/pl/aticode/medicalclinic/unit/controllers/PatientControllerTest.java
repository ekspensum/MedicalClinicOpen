package pl.aticode.medicalclinic.unit.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import pl.aticode.medicalclinic.controller.PatientController;
import pl.aticode.medicalclinic.entity.user.MedicalDocument;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.service.RemovePatientQuartzJobService;
import pl.aticode.medicalclinic.service.RemovePatientQuartzJobService.RemovePatientStatus;
import pl.aticode.medicalclinic.service.UserService;

class PatientControllerTest {

	private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private User user;
    private Patient patient;
    private String jsonPatient;
    
    @InjectMocks
    private PatientController patientController;
    @Mock
	private UserService userService;
    @Mock
    private RemovePatientQuartzJobService removePatientJobService;
	@Captor
	private ArgumentCaptor<Patient> argumentCaptorPatient;
    
	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        user = new User();
        user.setUsername("username");
        patient = new Patient();
        patient.setId(22L);
        patient.setPesel("72050783671");
        patient.setUser(user);
        jsonPatient = objectMapper.writeValueAsString(patient);
	}

	@Test
	void testGetPatient() throws Exception {
		when(userService.getPatient(user.getUsername())).thenReturn(patient);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getPatient").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonPatient));
        
        doThrow(new Exception("TEST")).when(userService).getPatient(any(String.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getPatient").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testSelfUpdatePatient() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdatePatient").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("NOTUNIQUE"));
        
        when(userService.checkUniqueLoginOnUpdate(any(Patient.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdatePatient").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        verify(userService).selfUpdatePatient(argumentCaptorPatient.capture());
        assertEquals(patient.getId(), argumentCaptorPatient.getValue().getId());
        
        doThrow(new Exception("TEST")).when(userService).selfUpdatePatient(any(Patient.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdatePatient").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testOrderRemoveMyPatientAccount() throws Exception {
        when(removePatientJobService.runRemovePatientJob(any(String.class))).thenReturn(RemovePatientStatus.ORDERED_REMOVE_PATIENT);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/orderRemoveMyPatientAccount")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
        		.string(RemovePatientStatus.ORDERED_REMOVE_PATIENT.name()));
        
        doThrow(new Exception("TEST")).when(removePatientJobService).runRemovePatientJob(any(String.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/orderRemoveMyPatientAccount")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testCancelOrderRemoveMyPatientAccount() throws Exception {
        when(removePatientJobService.cancelRemovePatientJob(any(String.class))).thenReturn(RemovePatientStatus.CANCELED_OREDER_REMOVE_PATIENT);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/cancelOrderRemoveMyPatientAccount")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
        		.string(RemovePatientStatus.CANCELED_OREDER_REMOVE_PATIENT.name()));
        
        doThrow(new Exception("TEST")).when(removePatientJobService).cancelRemovePatientJob(any(String.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/cancelOrderRemoveMyPatientAccount")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@SuppressWarnings("unchecked")
	@Test
	void testAddOrUpdateMedicalDocumentation() throws Exception {
		List<MedicalDocument> medicalDocumentList = Arrays.asList(new MedicalDocument(), new MedicalDocument());
		String jsonMedicalDocumentList = objectMapper.writeValueAsString(medicalDocumentList);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/addOrUpdateMedicalDocumentation")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonMedicalDocumentList))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATE_MEDICAL_DOCUMENTATION"));
        
        doThrow(new Exception("TEST")).when(userService).addOrUpdateMedicalDocumentation(any(List.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/addOrUpdateMedicalDocumentation")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonMedicalDocumentList))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}
	
	@Test
	void testRemoveMedicalDocumentation() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/removeMedicalDocument/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATE_MEDICAL_DOCUMENTATION"));
		
		doThrow(new PersistenceException("TEST")).when(userService).removeMedicalDocument(7L);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/removeMedicalDocument/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}
	
	@Test
	void testGetMedicalByteFile() throws Exception {
		byte[] medicalByteFile = "medicalByteFile".getBytes();
		when(userService.getMedicalByteFile(7L)).thenReturn(medicalByteFile);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getMedicalByteFile/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("medicalByteFile"));
		
		doThrow(new PersistenceException("TEST")).when(userService).getMedicalByteFile(7L);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getMedicalByteFile/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}
	
	@Test
	void testRemoveUserPhoto() throws Exception {
		String username = "username";
        mockMvc.perform(MockMvcRequestBuilders.put("/api/removeUserPhoto")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(username))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
        doThrow(new Exception("TEST")).when(userService).removeUserPhoto(username);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/removeUserPhoto")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(username))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}
}
