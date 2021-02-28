package pl.aticode.medicalclinic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import pl.aticode.medicalclinic.controller.HomeController;
import pl.aticode.medicalclinic.dao.user.DoctorRepository;
import pl.aticode.medicalclinic.dao.user.EmployeeRepository;
import pl.aticode.medicalclinic.dao.user.MedicalByteFileRepository;
import pl.aticode.medicalclinic.dao.user.MedicalDocumentRepository;
import pl.aticode.medicalclinic.dao.user.PatientRepository;
import pl.aticode.medicalclinic.dao.visit.VisitRepository;
import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.Employee;
import pl.aticode.medicalclinic.entity.user.MedicalByteFile;
import pl.aticode.medicalclinic.entity.user.MedicalDocument;
import pl.aticode.medicalclinic.entity.user.Owner;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.Role;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.model.RecoverPasswordRequest;
import pl.aticode.medicalclinic.model.VisitData;
import pl.aticode.medicalclinic.security.AuthenticationRequest;
import pl.aticode.medicalclinic.security.AuthenticationResponse;
import pl.aticode.medicalclinic.service.ActivationService;
import pl.aticode.medicalclinic.service.CipherService;
import pl.aticode.medicalclinic.service.EmailService;
import pl.aticode.medicalclinic.service.HibernateSearchService;
import pl.aticode.medicalclinic.service.PasswordService;
import pl.aticode.medicalclinic.service.ReCaptchaService;
import pl.aticode.medicalclinic.service.UserService;
import pl.aticode.medicalclinic.service.RemovePatientQuartzJobService.RemovePatientStatus;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:applicationtest.properties")
@TestMethodOrder(OrderAnnotation.class)
class MedicalClinicApplicationTests {
	
	@Value(value = "${mail.from}")
	private String mailFrom;
	
	private ObjectMapper objectMapper;
	private HomeController homeController;
	
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
	@Autowired
	private HibernateSearchService hibernateSearchService;
	@Autowired
	private CipherService cipherService;
	@Autowired
	private ActivationService activationService;
    @Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private DoctorRepository doctorRepository;
	@Autowired
	private PatientRepository patientRepository;
	@Autowired
	private PasswordService passwordService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private VisitRepository visitRepository;
	@Autowired
	private MedicalDocumentRepository medicalDocumentRepository;
	@Autowired
	private MedicalByteFileRepository medicalByteFileRepository;
	
	@Mock
	private ReCaptchaService reCaptchaService;

    @BeforeEach
    void setUp() throws Exception {
    	objectMapper = new ObjectMapper();
    	objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
    	objectMapper.registerModule(new JavaTimeModule());
        MockitoAnnotations.initMocks(this);
    }

	@Test
	@Order(1)
	void testAddEditAdminAndSelfUpdateOwner() throws Exception {
		Role role4 = new Role();
		role4.setId(4L);
		Role role5 = new Role();
		role5.setId(5L);
		List<Role> roleList = new ArrayList<>();
		roleList.add(role4);
		roleList.add(role5);
		User user = new User();
		user.setRoles(roleList);
		user.setEnabled(true);
		user.setUsername("adminUsername");
		user.setFirstName("firstNameAdmin");
		user.setLastName("lastNameAdmin");
		user.setEmail("admin@email.com");
		user.setLanguage("pl");
		Employee admin = new Employee();
		admin.setUser(user);
		String jsonEmployee = objectMapper.writeValueAsString(admin);
		
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("xxxxxxx", "xxxxxxxxxxxxxx");
        String jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        String jsonAuthenticationResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateRegisterdUser")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        
        AuthenticationResponse authenticationResponse = objectMapper.readValue(jsonAuthenticationResponse, AuthenticationResponse.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addAdmin").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonEmployee)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        
        String jsonAllEmployees = mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllEmployees").accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        
		Employee[] employeeArray = objectMapper.readValue(jsonAllEmployees, Employee[].class);
		admin = employeeArray[0];
		admin.getUser().setEnabled(false);
		jsonEmployee = objectMapper.writeValueAsString(admin);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/updateAdmin").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonEmployee)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
		
		jsonAllEmployees = mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllEmployees").accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		employeeArray = objectMapper.readValue(jsonAllEmployees, Employee[].class);
		admin = employeeArray[0];
		assertFalse(admin.getUser().isEnabled());
		
        String jsonOwner = mockMvc.perform(MockMvcRequestBuilders.post("/api/getOwner").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content("owner")
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Owner owner = objectMapper.readValue(jsonOwner, Owner.class);
        
        owner.getUser().setFirstName("firstNameOwner");
        owner.getUser().setPasswordField("xxxxxxxxx");
        String jsonOwnerUpdated = objectMapper.writeValueAsString(owner);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateOwner").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonOwnerUpdated)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
        jsonOwner = mockMvc.perform(MockMvcRequestBuilders.post("/api/getOwner").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(owner.getUser().getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        owner = objectMapper.readValue(jsonOwner, Owner.class);
        assertEquals("firstNameOwner", owner.getUser().getFirstName());
        
//        for next login by admin/employee
        admin.getUser().setEnabled(true); 
		jsonEmployee = objectMapper.writeValueAsString(admin);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/updateAdmin").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonEmployee)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
	}
	
	@Test
	@Order(2)
	void testAddEditEmployeeDoctorPatientAndRemovePatientAndSelfUpdateAdmin() throws Exception {
//		sign in admin
		Employee admin = userService.getEmployee("adminUsername");
		String passwordAdmin = "passwordAdmin";
		User adminUser = admin.getUser();
		String passwordEncode = passwordEncoder.encode(passwordAdmin);
		adminUser.setPassword(passwordEncode);
		employeeRepository.save(admin);		
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("adminUsername", passwordAdmin);
        String jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        String jsonAuthenticationResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateRegisterdUser")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(jsonAuthenticationResponse, AuthenticationResponse.class);
        
//        add employee
		User employeeUser = new User();
		employeeUser.setUsername("employeeUsername");
		employeeUser.setFirstName("firstNameEmployee");
		employeeUser.setLastName("lastNameEmployee");
		employeeUser.setEmail("employee@email.com");
		employeeUser.setLanguage("en");
		Employee employee = new Employee();
		employee.setUser(employeeUser);
		String jsonEmployee = objectMapper.writeValueAsString(employee);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addEmployee").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonEmployee)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        
//        update employee - set enabled on false
        String jsonAllEmployees = mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllEmployees").accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Employee[] employeeArray = objectMapper.readValue(jsonAllEmployees, Employee[].class);
		employee = employeeArray[1];
		employee.getUser().setEnabled(false);
		jsonEmployee = objectMapper.writeValueAsString(employee);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateEmployee").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonEmployee)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
//        assert for update employee above
		jsonAllEmployees = mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllEmployees").accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		employeeArray = objectMapper.readValue(jsonAllEmployees, Employee[].class);
		employee = employeeArray[1];
		assertFalse(employee.getUser().isEnabled());
		
//        update employee - restore enabled on true for login to another test 
        jsonAllEmployees = mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllEmployees").accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		employeeArray = objectMapper.readValue(jsonAllEmployees, Employee[].class);
		employee = employeeArray[1];
		employee.getUser().setEnabled(true);
		jsonEmployee = objectMapper.writeValueAsString(employee);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateEmployee").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonEmployee)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
//      add doctor
		User doctorUser = new User();
		doctorUser.setUsername("doctorUsername");
		doctorUser.setFirstName("firstNameDoctor");
		doctorUser.setLastName("lastNameDoctor");
		doctorUser.setEmail("doctor@email.com");
		doctorUser.setLanguage("en");
		Doctor doctor = new Doctor();
		doctor.setExperience("Doctor experience");
		doctor.setPesel("72050783671");
		doctor.setCountry("Poland");
		doctor.setZipCode("00-001");
		doctor.setCity("city");
		doctor.setStreet("street");
		doctor.setStreetNo("11No");
		doctor.setPhone("48123456789");
		doctor.setUser(doctorUser);
		String jsonDoctor = objectMapper.writeValueAsString(doctor);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctor").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonDoctor).header("Authorization", "Bearer " + authenticationResponse.getJwt()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("ADDED"));

//      update doctor - set enabled on false
		String jsonAllDoctors = mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDoctors").accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer " + authenticationResponse.getJwt()))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Doctor[] doctorArray = objectMapper.readValue(jsonAllDoctors, Doctor[].class);
		doctor = doctorArray[0];
		doctor.getUser().setEnabled(false);
		jsonDoctor = objectMapper.writeValueAsString(doctor);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/updateDoctor").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonDoctor).header("Authorization", "Bearer " + authenticationResponse.getJwt()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("UPDATED"));

//      assert for update doctor above
		jsonAllDoctors = mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDoctors").accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer " + authenticationResponse.getJwt()))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		doctorArray = objectMapper.readValue(jsonAllDoctors, Doctor[].class);
		doctor = doctorArray[0];
		assertFalse(doctor.getUser().isEnabled());

//      update doctor - restore enabled on true for login to another test 
		jsonAllDoctors = mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDoctors").accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer " + authenticationResponse.getJwt()))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		doctorArray = objectMapper.readValue(jsonAllDoctors, Doctor[].class);
		doctor = doctorArray[0];
		doctor.getUser().setEnabled(true);
		jsonDoctor = objectMapper.writeValueAsString(doctor);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/updateDoctor").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonDoctor).header("Authorization", "Bearer " + authenticationResponse.getJwt()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
//        add patient
        User patientUser = new User();
        patientUser.setUsername("patientUsername");
        patientUser.setFirstName("firstNamePatient");
        patientUser.setLastName("lastNamePatient");
        patientUser.setEmail("patient@email.com");
        patientUser.setLanguage("en");
        Patient patient = new Patient();
		patient.setPesel("72050783671");
		patient.setGender("male");
		patient.setCountry("Poland");
		patient.setZipCode("00-001");
		patient.setCity("city");
		patient.setStreet("street");
		patient.setStreetNo("11No");
		patient.setPhone("48123456789");
		patient.setUser(patientUser);
		String jsonPatient = objectMapper.writeValueAsString(patient);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByAdminOrEmployee").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonPatient)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        
//        search patient to update
		String jsonPatientList = mockMvc.perform(MockMvcRequestBuilders.get("/api/searchPatient").param("searchString", patient.getPesel())
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Patient[] patientArray = objectMapper.readValue(jsonPatientList, Patient[].class);
		
//		update patient - set enabled on false
		patient = patientArray[0];
		patient.getUser().setEnabled(false);
		jsonPatient = objectMapper.writeValueAsString(patient);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updatePatient").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonPatient)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
//      assert for update patient above
        jsonPatientList = mockMvc.perform(MockMvcRequestBuilders.get("/api/searchPatient").param("searchString", patient.getPesel())
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		patientArray = objectMapper.readValue(jsonPatientList, Patient[].class);
		patient = patientArray[0];
		assertFalse(patient.getUser().isEnabled());
		
//      remove patient 		
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/removePatient/"+patient.getId())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("REMOVED"));
	}
	
	@Test
	@Order(3)
	void testSelfUpdateEmployee() throws Exception {
//		sign in employee
		Employee employee = userService.getEmployee("employeeUsername");
		String passwordEmployee = "passwordEmployee";
		User employeeUser = employee.getUser();
		String passwordEncode = passwordEncoder.encode(passwordEmployee);
		employeeUser.setPassword(passwordEncode);
		employeeRepository.save(employee);		
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("employeeUsername", passwordEmployee);
        String jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        String jsonAuthenticationResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateRegisterdUser")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(jsonAuthenticationResponse, AuthenticationResponse.class);
        
//      get employee
        String jsonEmployee = mockMvc.perform(MockMvcRequestBuilders.post("/api/getEmployee").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(employeeUser.getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        employee = objectMapper.readValue(jsonEmployee, Employee.class);
        
//      self update employee
        employee.setPhone("48987654321");
        employee.getUser().setPasswordField("passwordEmployee");
        jsonEmployee = objectMapper.writeValueAsString(employee);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateEmployee").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonEmployee)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
//        assert for update employee above
        jsonEmployee = mockMvc.perform(MockMvcRequestBuilders.post("/api/getEmployee").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(employeeUser.getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        employee = objectMapper.readValue(jsonEmployee, Employee.class);
        assertEquals("48987654321", employee.getPhone());
	}
	
	@Test
	@Order(4)
	void testSelfUpdateDoctor() throws Exception {
//		sign in doctor
		Doctor doctor = userService.getDoctor("doctorUsername");
		String passwordDoctor = "passwordDoctor";
		User doctorUser = doctor.getUser();
		String passwordEncode = passwordEncoder.encode(passwordDoctor);
		doctorUser.setPassword(passwordEncode);
		doctorRepository.save(doctor);		
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("doctorUsername", passwordDoctor);
        String jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        String jsonAuthenticationResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateRegisterdUser")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(jsonAuthenticationResponse, AuthenticationResponse.class);
        
//      get doctor
        String jsonDoctor = mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctor").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(doctorUser.getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        doctor = objectMapper.readValue(jsonDoctor, Doctor.class);
        
//      self update doctor
        doctor.setPhone("48741258963");
        doctor.getUser().setPasswordField("passwordDoctor");
        jsonDoctor = objectMapper.writeValueAsString(doctor);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateDoctor").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonDoctor)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
//        assert for update doctor above
        jsonDoctor = mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctor").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(doctorUser.getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        doctor = objectMapper.readValue(jsonDoctor, Doctor.class);
        assertEquals("48741258963", doctor.getPhone());
	}
	
	@Test
	@Order(5)
	void testRegisterAndActivatePatient() throws Exception {
//    self register patient
		User patientUser = new User();
		patientUser.setUsername("patientUsername");
		patientUser.setFirstName("firstNamePatient");
		patientUser.setLastName("lastNamePatient");
		patientUser.setEmail("patient@email.com");
		patientUser.setLanguage("en");
		Patient patient = new Patient();
		patient.setPesel("72050783671");
		patient.setGender("male");
		patient.setCountry("Poland");
		patient.setZipCode("00-001");
		patient.setCity("city");
		patient.setStreet("street");
		patient.setStreetNo("11No");
		patient.setPhone("48123456789");
		patient.setUser(patientUser);
		String jsonPatient = objectMapper.writeValueAsString(patient);

		homeController = new HomeController(mailFrom, userService, passwordService, cipherService, activationService, emailService, 
				reCaptchaService, hibernateSearchService, messageSource);
		mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
		String reCaptchHeader = "reCaptchHeader";
		when(reCaptchaService.verify(reCaptchHeader)).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByRegistrationPage").header("reCaptcha", reCaptchHeader)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("ADDED"));
		
//		activate patient
		Patient foundPatient = hibernateSearchService.searchPatientPeselByKeywordQuery("72050783671");
		String encodeString = cipherService.encodeString(foundPatient.getActivationString());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/activationPatient")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(encodeString))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ACTIVATED"));
	}
	
	@Test
	@Order(6)
	void testLoginSelfUpdateOrderRemoveAndCancelOrderRemovePatient() throws Exception {
//		sign in patient
		Patient patient = userService.getPatient("patientUsername");
		String passwordPatient = "passwordAdmin";
		User patientUser = patient.getUser();
		String passwordEncode = passwordEncoder.encode(passwordPatient);
		patientUser.setPassword(passwordEncode);
		patientRepository.save(patient);		
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("patientUsername", passwordPatient);
        String jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        String jsonAuthenticationResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateRegisterdUser")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(jsonAuthenticationResponse, AuthenticationResponse.class);
        
//        get patient
        String jsonPatient = mockMvc.perform(MockMvcRequestBuilders.post("/api/getPatient").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(patientUser.getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        patient = objectMapper.readValue(jsonPatient, Patient.class);
        
//        self update patient
        patient.setPhone("48963258741");
        patient.getUser().setPasswordField("passwordPatient");
        jsonPatient = objectMapper.writeValueAsString(patient);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdatePatient").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonPatient)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
//        assert for update patient above
        jsonPatient = mockMvc.perform(MockMvcRequestBuilders.post("/api/getPatient").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(patientUser.getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        patient = objectMapper.readValue(jsonPatient, Patient.class);
        assertEquals("48963258741", patient.getPhone());
        
//        order remove patient account and cancel this order remove
        mockMvc.perform(MockMvcRequestBuilders.put("/api/orderRemoveMyPatientAccount").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(patientUser.getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        		.andExpect(MockMvcResultMatchers.status().isOk())
        		.andExpect(MockMvcResultMatchers.content().string(RemovePatientStatus.ORDERED_REMOVE_PATIENT.name()));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/cancelOrderRemoveMyPatientAccount").contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(patientUser.getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        		.andExpect(MockMvcResultMatchers.status().isOk())
        		.andExpect(MockMvcResultMatchers.content().string(RemovePatientStatus.CANCELED_OREDER_REMOVE_PATIENT.name()));
	}
	
	@Test
	@Order(7)
	void testResetPasswordPatient() throws Exception {
		homeController = new HomeController(mailFrom, userService, passwordService, cipherService, activationService, emailService, 
				reCaptchaService, hibernateSearchService, messageSource);
		mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

		RecoverPasswordRequest recoverPasswordRequest = new RecoverPasswordRequest("patientUsername", "patient@email.com");
		String jsonRecoverPasswordRequest = objectMapper.writeValueAsString(recoverPasswordRequest);
        String reCaptchHeader = "reCaptchHeader";
		when(reCaptchaService.verify(reCaptchHeader)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/forgetPassword").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonRecoverPasswordRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("RECOVER_PASS_MAIL"));
		
        String userDataToEncrypt = 6 + ";" + LocalDateTime.now().withNano(0).plusHours(1);
        String resetPasswordString = cipherService.encodeString(userDataToEncrypt);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/resetPasswordByLink").contentType(MediaType.TEXT_PLAIN_VALUE).content(resetPasswordString))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("RESET"));
	}
	
	@Test
	@Order(8)
	void testLoginPatientAddMedicalDocSchedule2VisitsDeleteVisit1AndRemovePatientByAdmin() throws Exception {
//		sign in patient
		Patient patient = userService.getPatient("patientUsername");
		String passwordPatient = "passwordAdmin";
		User patientUser = patient.getUser();
		String passwordEncode = passwordEncoder.encode(passwordPatient);
		patientUser.setPassword(passwordEncode);
		patientRepository.save(patient);		
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("patientUsername", passwordPatient);
        String jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        String jsonAuthenticationResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateRegisterdUser")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(jsonAuthenticationResponse, AuthenticationResponse.class);
        
//        get patient
        String jsonPatient = mockMvc.perform(MockMvcRequestBuilders.post("/api/getPatient")
        		.contentType(MediaType.TEXT_PLAIN_VALUE)
        		.content(patientUser.getUsername())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        patient = objectMapper.readValue(jsonPatient, Patient.class);
        
//        add medical documentation
		MedicalDocument medicalDocument1 = new MedicalDocument();
		medicalDocument1.setDescription("description1");
		medicalDocument1.setFileName("fileName1");
		medicalDocument1.setEnabled(true);
		medicalDocument1.setFileSize(101);
		medicalDocument1.setFileType("fileType1");
		MedicalByteFile medicalByteFile1 = new MedicalByteFile();
		medicalByteFile1.setFileBase64("file,Base64-1");
		medicalDocument1.setMedicalByteFile(medicalByteFile1);
		MedicalDocument medicalDocument2 = new MedicalDocument();
		medicalDocument2.setDescription("description2");
		medicalDocument2.setFileName("fileName2");
		medicalDocument2.setEnabled(true);
		medicalDocument2.setFileSize(102);
		medicalDocument2.setFileType("fileType2");
		MedicalByteFile medicalByteFile2 = new MedicalByteFile();
		medicalByteFile2.setFileBase64("file,Base64-2");
		medicalDocument2.setMedicalByteFile(medicalByteFile2);
		List<MedicalDocument> medicalDocumentList = Arrays.asList(medicalDocument1, medicalDocument2);
		String jsonMedicalDocumentList = objectMapper.writeValueAsString(medicalDocumentList);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/addOrUpdateMedicalDocumentation")
        		.contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonMedicalDocumentList)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATE_MEDICAL_DOCUMENTATION"));
        
//        schedule visit 1
        Doctor doctor = userService.getDoctor("doctorUsername");
		VisitData visitData1 = new VisitData();
		visitData1.setDoctorUsername(doctor.getUser().getUsername());
		visitData1.setPatientUsername(patient.getUser().getUsername());
		visitData1.setVisitDate(LocalDate.now().plusDays(5));
		visitData1.setVisitTime(LocalTime.of(17, 30));
		String jsonvisitData1 = objectMapper.writeValueAsString(visitData1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/scheduleVisitByPatient")
        		.contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonvisitData1)
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("SCHEDULED_VISIT"));
        
//      schedule visit 2
		VisitData visitData2 = new VisitData();
		visitData2.setDoctorUsername(doctor.getUser().getUsername());
		visitData2.setPatientUsername(patient.getUser().getUsername());
		visitData2.setVisitDate(LocalDate.now().plusDays(7));
		visitData2.setVisitTime(LocalTime.of(9, 30));
		String jsonvisitData2 = objectMapper.writeValueAsString(visitData2);
	    mockMvc.perform(MockMvcRequestBuilders.post("/api/scheduleVisitByPatient")
	      		.contentType(MediaType.APPLICATION_JSON_VALUE)
	      		.content(jsonvisitData2)
	      		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
	   .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("SCHEDULED_VISIT"));
	    
//	    delete visit 2
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/deleteVisit/2").accept(MediaType.ALL_VALUE)
				.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("DELETED_VISIT"));
        
//		sign in admin
		Employee admin = userService.getEmployee("adminUsername");
		String passwordAdmin = "passwordAdmin";
		User adminUser = admin.getUser();
		passwordEncode = passwordEncoder.encode(passwordAdmin);
		adminUser.setPassword(passwordEncode);
		employeeRepository.save(admin);		
        authenticationRequest = new AuthenticationRequest("adminUsername", passwordAdmin);
        jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        jsonAuthenticationResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateRegisterdUser")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        authenticationResponse = objectMapper.readValue(jsonAuthenticationResponse, AuthenticationResponse.class);
        
//        remove patient
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/removePatient/"+patient.getId())
        		.header("Authorization", "Bearer "+authenticationResponse.getJwt()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("REMOVED"));
        
        assertNull(userService.getPatient("patientUsername"));
        assertFalse(visitRepository.findById(1L).isPresent());
        assertFalse(medicalDocumentRepository.findById(1L).isPresent());
        assertFalse(medicalByteFileRepository.findById(1L).isPresent());
	}
}
