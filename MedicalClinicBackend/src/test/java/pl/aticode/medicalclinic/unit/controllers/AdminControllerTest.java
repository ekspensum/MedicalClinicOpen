package pl.aticode.medicalclinic.unit.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

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
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import pl.aticode.medicalclinic.controller.AdminController;
import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.Employee;
import pl.aticode.medicalclinic.entity.user.MedicalByteFile;
import pl.aticode.medicalclinic.entity.user.MedicalDocument;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.entity.user.WorkingWeek;
import pl.aticode.medicalclinic.service.HibernateSearchService;
import pl.aticode.medicalclinic.service.PasswordService;
import pl.aticode.medicalclinic.service.UserService;

class AdminControllerTest {

	private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @InjectMocks
    private AdminController adminController;
    @Mock
	private UserService userService;
    @Mock
	private HibernateSearchService hibernateSearchService;
    @Mock
	private PasswordService passwordService;
	@Captor
	private ArgumentCaptor<Employee> argumentCaptorEmployee;
	@Captor
	private ArgumentCaptor<Patient> argumentCaptorPatient;
	@Captor
	private ArgumentCaptor<Doctor> argumentCaptorDoctor;
	
    private User user;
    private Employee employee;
    private String jsonEmployee;
    private Patient patient;
    private String jsonPatient;
    private Doctor doctor;
    private String jsonDoctor;
	
	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
		user = new User();
		user.setUsername("username");
		employee = new Employee();
		employee.setId(33L);
		employee.setUser(user);
		jsonEmployee = objectMapper.writeValueAsString(employee);
		patient = new Patient();
		patient.setId(22L);
		patient.setPesel("72050783671");
		patient.setUser(user);
		jsonPatient = objectMapper.writeValueAsString(patient);
		doctor = new Doctor();
		doctor.setId(44L);
		doctor.setUser(user);
		WorkingWeek workingWeek = new WorkingWeek();
		workingWeek.setWorkingWeekMapByte("workingWeekMapByte".getBytes());
		doctor.setWorkingWeek(workingWeek);
		jsonDoctor = objectMapper.writeValueAsString(doctor);
	}

	@Test
	void testAddEmployee() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addEmployee").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("NOTUNIQUE"));
        
        when(userService.checkUniqueLoginOnRegister(employee.getUser().getUsername())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addEmployee").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        verify(userService).addEmployee(argumentCaptorEmployee.capture());
        assertEquals(employee.getId(), argumentCaptorEmployee.getValue().getId());
       
        doThrow(new ConstraintViolationException(null)).when(userService).addEmployee(any(Employee.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addEmployee").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("INVALID"));
        
        doThrow(new Exception("TEST")).when(userService).addEmployee(any(Employee.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addEmployee").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testUpdateEmployee() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateEmployee").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        verify(userService).updateAdminOrEmployee(argumentCaptorEmployee.capture());
        assertEquals(employee.getId(), argumentCaptorEmployee.getValue().getId());
        
        doThrow(new Exception("TEST")).when(userService).updateAdminOrEmployee(any(Employee.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateEmployee").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testAddPatient() throws Exception {
		when(hibernateSearchService.searchPatientPeselByKeywordQuery(patient.getPesel())).thenReturn(patient);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByAdminOrEmployee")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("PATIEN_NOT_ENABLED_ADMIN"));
        
        patient.getUser().setEnabled(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByAdminOrEmployee")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("PATIEN_ACCOUNT_EXIST_ADMIN"));
		
        when(hibernateSearchService.searchPatientPeselByKeywordQuery(patient.getPesel())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByAdminOrEmployee")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("NOTUNIQUE"));
        
        when(userService.checkUniqueLoginOnRegister(patient.getUser().getUsername())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByAdminOrEmployee")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        verify(userService).addPatientByAdminOrEmployee(argumentCaptorPatient.capture());
        assertEquals(patient.getId(), argumentCaptorPatient.getValue().getId());
        
        doThrow(new ConstraintViolationException(null)).when(userService).addPatientByAdminOrEmployee(any(Patient.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByAdminOrEmployee")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("INVALID"));
        
        doThrow(new Exception("TEST")).when(userService).addPatientByAdminOrEmployee(any(Patient.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByAdminOrEmployee")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testSearchPatient() throws Exception {
		String searchStringPatient = "searchStringPatient";
		List<Patient> patientList = new ArrayList<>();
		
		Patient patient1 = new Patient();
		patient1.setId(66L);
		MedicalDocument medicalDocument1 = new MedicalDocument();
		medicalDocument1.setMedicalByteFile(new MedicalByteFile());
		List<MedicalDocument> medicalDocumentList1 = new ArrayList<>();
		medicalDocumentList1.add(medicalDocument1);
		patient1.setMedicalDocumentList(medicalDocumentList1);
		patientList.add(patient1);
		
		Patient patient2 = new Patient();
		patient2.setId(77L);
		MedicalDocument medicalDocument2 = new MedicalDocument();
		medicalDocument2.setMedicalByteFile(new MedicalByteFile());
		List<MedicalDocument> medicalDocumentList2 = new ArrayList<>();
		medicalDocumentList2.add(medicalDocument2);
		patient2.setMedicalDocumentList(medicalDocumentList2);
		patientList.add(patient2);
		
		SimpleBeanPropertyFilter patientFilter = SimpleBeanPropertyFilter.serializeAllExcept("medicalDocumentList");
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("patientFilter", patientFilter);
        objectMapper.setFilterProvider(filterProvider);
		String jsonPatientList = objectMapper.writeValueAsString(patientList);
		
		when(hibernateSearchService.searchPatientNamePeselStreetPhoneMailByKeywordQuery(searchStringPatient)).thenReturn(patientList);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/searchPatient")
				.param("searchString", searchStringPatient)
				.accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonPatientList));
		
		doThrow(new PersistenceException("TEST")).when(hibernateSearchService).searchPatientNamePeselStreetPhoneMailByKeywordQuery(searchStringPatient);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/searchPatient").param("searchString", searchStringPatient)
				.accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testUpdatePatient() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updatePatient").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        verify(userService).updatePatient(argumentCaptorPatient.capture());
        assertEquals(patient.getId(), argumentCaptorPatient.getValue().getId());
        
        doThrow(new Exception("TEST")).when(userService).updatePatient(any(Patient.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updatePatient").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testRemovePatient() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/removePatient/"+patient.getId()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("REMOVED"));
        
        doThrow(new Exception("TEST")).when(userService).removePatient(patient.getId());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/removePatient/"+patient.getId()))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testAddDoctor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctor").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctor))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("NOTUNIQUE"));
        
        when(userService.checkUniqueLoginOnRegister(doctor.getUser().getUsername())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctor").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctor))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        verify(userService).addDoctor(argumentCaptorDoctor.capture());
        assertEquals(doctor.getId(), argumentCaptorDoctor.getValue().getId());
       
        doThrow(new ConstraintViolationException(null)).when(userService).addDoctor(any(Doctor.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctor").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctor))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("INVALID"));
        
        doThrow(new Exception("TEST")).when(userService).addDoctor(any(Doctor.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctor").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctor))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testUpdateDoctor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateDoctor").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctor))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        verify(userService).updateDoctor(argumentCaptorDoctor.capture());
        assertEquals(doctor.getId(), argumentCaptorDoctor.getValue().getId());
        
        doThrow(new Exception("TEST")).when(userService).updateDoctor(any(Doctor.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateDoctor").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctor))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetAllDoctors() throws Exception {
		List<Doctor> doctorList = new ArrayList<>();
		doctorList.add(new Doctor());
		doctorList.add(new Doctor());
		String jsonDoctorList = objectMapper.writeValueAsString(doctorList);
		when(userService.getAllDoctors()).thenReturn(doctorList);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDoctors").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonDoctorList));
		
		doThrow(new PersistenceException("TEST")).when(userService).getAllDoctors();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDoctors").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testResetPasswordByAdminPage() throws Exception {
		String jsonLong = objectMapper.writeValueAsString(doctor.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/resetPasswordByAdminPage").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonLong))
        .andExpect(MockMvcResultMatchers.status().isOk());
        
        doThrow(new IllegalArgumentException("TEST")).when(userService).getUser(any(Long.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/resetPasswordByAdminPage").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonLong))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

}
