package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import pl.aticode.medicalclinic.dao.user.DoctorRepository;
import pl.aticode.medicalclinic.dao.user.EmployeeRepository;
import pl.aticode.medicalclinic.dao.user.MedicalByteFileRepository;
import pl.aticode.medicalclinic.dao.user.OwnerRepository;
import pl.aticode.medicalclinic.dao.user.PatientRepository;
import pl.aticode.medicalclinic.dao.user.RoleRepository;
import pl.aticode.medicalclinic.dao.user.UserRepository;
import pl.aticode.medicalclinic.dao.user.WorkingWeekRepository;
import pl.aticode.medicalclinic.dao.visit.VisitRepository;
import pl.aticode.medicalclinic.entity.user.Company;
import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.Employee;
import pl.aticode.medicalclinic.entity.user.MedicalByteFile;
import pl.aticode.medicalclinic.entity.user.MedicalDocument;
import pl.aticode.medicalclinic.entity.user.Owner;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.Role;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.entity.user.WorkingWeek;
import pl.aticode.medicalclinic.entity.visit.Visit;
import pl.aticode.medicalclinic.model.WorkingDayMap;
import pl.aticode.medicalclinic.model.WorkingHourMap;
import pl.aticode.medicalclinic.security.JwtService;
import pl.aticode.medicalclinic.service.ActivationService;
import pl.aticode.medicalclinic.service.AlertVisitDateQuartzJobService;
import pl.aticode.medicalclinic.service.EmailService;
import pl.aticode.medicalclinic.service.HibernateSearchService;
import pl.aticode.medicalclinic.service.PasswordService;
import pl.aticode.medicalclinic.service.UserService;


class UserServiceTest {

    @InjectMocks
    private UserService userService;
	@Mock
	private PasswordService passwordService;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private EmployeeRepository employeeRepository;
	@Mock
	private OwnerRepository ownerRepository;
	@Mock
	private PatientRepository patientRepository;
	@Mock
	private DoctorRepository doctorRepository;
	@Mock
	private ActivationService activationService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private JwtService jwtService;
	@Mock
    private EmailService emailService;
	@Mock
    private MessageSource messageSource;
	@Mock
	private MedicalByteFileRepository medicalByteFileRepository;
	@Mock
	private HibernateSearchService hibernateSearchService;
	@Mock
	private VisitRepository visitRepository;
	@Mock
	private WorkingWeekRepository workingWeekRepository;
	@Mock
	private AlertVisitDateQuartzJobService alertVisitDateQuartzJobService;
	@Captor
	private ArgumentCaptor<Employee> argumentCaptorEmployee;
	@Captor
	private ArgumentCaptor<Owner> argumentCaptorOwner;
	@Captor
	private ArgumentCaptor<Patient> argumentCaptorPatient;
	@Captor
	private ArgumentCaptor<Doctor> argumentCaptorDoctor;
	@Captor
	private ArgumentCaptor<WorkingWeek> argumentCaptorWorkingWeek;
	@Captor
	private ArgumentCaptor<Long> argumentCaptorLong; 
	@Captor
	private ArgumentCaptor<User> argumentCaptorUser;
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
	@Test
	void testCheckUniqueLoginOnRegister() {
		when(userRepository.findByUsername("username")).thenReturn(null);
		assertTrue(userService.checkUniqueLoginOnRegister("username"));
		User user = new User();
		when(userRepository.findByUsername("username")).thenReturn(user);
		assertFalse(userService.checkUniqueLoginOnRegister("username"));
	}

	@Test
	void testCheckUniqueLoginOnUpdate() {
		User user = new User();
		user.setId(11L);
		user.setUsername("username");
		Patient patient = new Patient();
		patient.setUser(user);
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findById(user.getId())).thenReturn(optionalUser);
		assertTrue(userService.checkUniqueLoginOnUpdate(patient));
		
		when(userRepository.findByUsername("username2")).thenReturn(null);
		User user2 = new User();
		user2.setUsername("username2");
		Optional<User> optionalUser2 = Optional.of(user2);
		when(userRepository.findById(user.getId())).thenReturn(optionalUser2);
		assertTrue(userService.checkUniqueLoginOnUpdate(patient));
		
		Employee employee = new Employee();
		employee.setUser(user);
		when(userRepository.findById(user.getId())).thenReturn(optionalUser);
		assertTrue(userService.checkUniqueLoginOnUpdate(employee));
		when(userRepository.findById(user.getId())).thenReturn(optionalUser2);
		assertTrue(userService.checkUniqueLoginOnUpdate(employee));

		Doctor doctor = new Doctor();
		doctor.setUser(user);
		when(userRepository.findById(user.getId())).thenReturn(optionalUser);
		assertTrue(userService.checkUniqueLoginOnUpdate(doctor));
		when(userRepository.findById(user.getId())).thenReturn(optionalUser2);
		assertTrue(userService.checkUniqueLoginOnUpdate(doctor));
		
		Owner owner = new Owner();
		owner.setUser(user);
		when(userRepository.findById(user.getId())).thenReturn(optionalUser);
		assertTrue(userService.checkUniqueLoginOnUpdate(owner));
		when(userRepository.findById(user.getId())).thenReturn(optionalUser2);
		assertTrue(userService.checkUniqueLoginOnUpdate(owner));
	}

	@Test
	void testGetUser() {
		User userExpect = new User();
		userExpect.setId(11L);
		Optional<User> optionalUser = Optional.of(userExpect);
		when(userRepository.findById(userExpect.getId())).thenReturn(optionalUser);
		assertEquals(userExpect, userRepository.findById(userExpect.getId()).get());
	}

	@Test
	void testGetUserFromRequest() {
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		String token = requestTokenHeader.substring(7);
		String username = "username";
		when(jwtService.getUsername(token)).thenReturn(username);
		when(userRepository.findByUsername(username)).thenReturn(new User());
		assertNotNull(userService.getUserFromRequest());
	}
	
	@Test
	void testAddAdmin() throws Exception {
		User user = new User();
		user.setLanguage("pl");
		user.setEmail("email33");
		Employee employee = new Employee();
		employee.setUser(user);
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);

		userService.addAdmin(employee);
        verify(employeeRepository).save(argumentCaptorEmployee.capture());
        assertEquals(employee, argumentCaptorEmployee.getValue());
        assertNotNull(employee.getRegisterDateTime());
	}

	@Test
	void testUpdateAdminOrEmployee() throws Exception {
		User user = new User();
		user.setId(11L);
		user.setUsername("username");
		Employee employee = new Employee();
		employee.setUser(user);
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findById(user.getId())).thenReturn(optionalUser);
		
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.updateAdminOrEmployee(employee);
        verify(employeeRepository).save(argumentCaptorEmployee.capture());
        assertEquals(employee, argumentCaptorEmployee.getValue());
		assertNotNull(employee.getEditDateTime());
	}

	@Test
	void testAddEmployee() throws Exception {
		User user = new User();
		user.setId(11L);
		user.setLanguage("pl");
		user.setEmail("email33");
		user.setUsername("username");
		Employee employee = new Employee();
		employee.setUser(user);
		Role role1 = new Role();
		role1.setId(1L);
		Optional<Role> optionalRole1 = Optional.of(role1);
		when(roleRepository.findById(role1.getId())).thenReturn(optionalRole1);
		Role role4 = new Role();
		role4.setId(4L);
		Optional<Role> optionalRole4 = Optional.of(role4);
		when(roleRepository.findById(role4.getId())).thenReturn(optionalRole4);
		
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.addEmployee(employee);
        verify(employeeRepository).save(argumentCaptorEmployee.capture());
        assertEquals(employee, argumentCaptorEmployee.getValue());
		assertNotNull(employee.getRegisterDateTime());
	}

	@Test
	void testGetAllEmployees() throws Exception {
		Iterable<Employee> iterableEmployee = IterableUtil.iterable(new Employee(), new Employee());
		when(employeeRepository.findAll()).thenReturn(iterableEmployee);
		assertEquals(2, userService.getAllEmployees().size());
	}

	@Test
	void testGetEmployee() throws Exception {
		User user = new User();
		when(userRepository.findByUsername("username")).thenReturn(user);
		when(employeeRepository.findByUser(user)).thenReturn(new Employee());
		assertNotNull(userService.getEmployee("username"));
	}

	@Test
	void testSelfUpdateEmployee() throws Exception {
		User user = new User();
		user.setUsername("username");
		Employee employee = new Employee();
		employee.setId(22L);
		employee.setUser(user);
		employee.setUserRegister(user);
		Optional<Employee> optionalUser = Optional.of(employee);
		when(employeeRepository.findById(employee.getId())).thenReturn(optionalUser);
		
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.selfUpdateEmployee(employee);
        verify(employeeRepository).save(argumentCaptorEmployee.capture());
        assertEquals(employee, argumentCaptorEmployee.getValue());
		assertNotNull(employee.getEditDateTime());
	}

	@Test
	void testGetOwner() throws Exception {
		User user = new User();
		when(userRepository.findByUsername("username")).thenReturn(user);
		when(ownerRepository.findByUser(user)).thenReturn(new Owner());
		assertNotNull(userService.getOwner("username"));
	}

	@Test
	void testSelfUpdateOwner() throws Exception {
		User user = new User();
		user.setPasswordField("passwordField");
		Owner owner = new Owner();
		owner.setUser(user);
		userService.selfUpdateOwner(owner);
        verify(ownerRepository).save(argumentCaptorOwner.capture());
        assertEquals(owner, argumentCaptorOwner.getValue());
		assertNotNull(owner.getEditDateTime());
	}

	@Test
	void testGetCompanyData() {
		Owner owner = new Owner();
		owner.setCompany(new Company());
		Optional<Owner> optionalOwner = Optional.of(owner);
		when(ownerRepository.findById(1L)).thenReturn(optionalOwner);
		assertNotNull(userService.getCompanyData());
	}

	@Test
	void testAddPatientByRegistrationPage() throws Exception {
		User user = new User();
		user.setUsername("username");
		user.setEmail("email");
		user.setLanguage("pl");
		Patient patient = new Patient();
		patient.setUser(user);
		Role role2 = new Role();
		role2.setId(2L);
		Optional<Role> optionalRole2 = Optional.of(role2);
		when(roleRepository.findById(role2.getId())).thenReturn(optionalRole2);
		userService.addPatientByRegistrationPage(patient);
        verify(patientRepository).save(argumentCaptorPatient.capture());
        assertEquals(patient, argumentCaptorPatient.getValue());
		assertNotNull(patient.getRegisterDateTime());
	}

	@Test
	void testAddPatientByAdminOrEmployee() throws Exception {
		User user = new User();
		user.setUsername("username");
		user.setEmail("email");
		user.setLanguage("pl");
		Patient patient = new Patient();
		patient.setUser(user);
		Role role2 = new Role();
		role2.setId(2L);
		Optional<Role> optionalRole2 = Optional.of(role2);
		when(roleRepository.findById(role2.getId())).thenReturn(optionalRole2);
		
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.addPatientByAdminOrEmployee(patient);
        verify(patientRepository).save(argumentCaptorPatient.capture());
        assertEquals(patient, argumentCaptorPatient.getValue());
		assertNotNull(patient.getRegisterDateTime());
	}

	@Test
	void testUpdatePatient() throws Exception {
		User user = new User();
		user.setId(11L);
		user.setPasswordField("passwordField");
		Patient patient = new Patient();
		patient.setUser(user);
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findById(user.getId())).thenReturn(optionalUser);

		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.updatePatient(patient);
        verify(patientRepository).save(argumentCaptorPatient.capture());
        assertEquals(patient, argumentCaptorPatient.getValue());
		assertNotNull(patient.getEditDateTime());
	}

	@Test
	void testSelfUpdatePatient() throws Exception {
		User user = new User();
		Patient patient = new Patient();
		patient.setId(11L);
		patient.setUser(user);
		patient.setUserRegister(user);
		Optional<Patient> optionalUser = Optional.of(patient);
		when(patientRepository.findById(patient.getId())).thenReturn(optionalUser);

		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.selfUpdatePatient(patient);
        verify(patientRepository).save(argumentCaptorPatient.capture());
        assertEquals(patient, argumentCaptorPatient.getValue());
		assertNotNull(patient.getEditDateTime());
	}

	@Test
	void testRemovePatient() throws Exception {
		User user = new User();
		user.setLanguage("pl");
		Patient patient = new Patient();
		patient.setId(11L);
		patient.setUser(user);
		patient.setUserRegister(user);
		Optional<Patient> optionalPatient = Optional.of(patient);
		MedicalDocument medicalDocument = new MedicalDocument();
		medicalDocument.setId(12L);
		List<MedicalDocument> medicalDocumentList = new ArrayList<>();
		medicalDocumentList.add(medicalDocument);
		optionalPatient.get().setMedicalDocumentList(medicalDocumentList);
		when(patientRepository.findById(patient.getId())).thenReturn(optionalPatient);
		
		Visit visit = new Visit();
		List<Visit> visitList = new ArrayList<>();
		visitList.add(visit);
		when(hibernateSearchService.searchVisitToRemoveByPatientUsername(user.getUsername())).thenReturn(visitList);
		
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.removePatient(patient.getId());
        verify(patientRepository).delete(argumentCaptorPatient.capture());
        assertEquals(patient, argumentCaptorPatient.getValue());	
        
        doThrow(new IllegalStateException("TEST")).when(userRepository).findByUsername("username");
		userService.removePatient(patient.getId());
        assertEquals(patient, argumentCaptorPatient.getValue());
	}

	@Test
	void testGetPatient() throws Exception {
		User user = new User();
		when(userRepository.findByUsername("username")).thenReturn(user);
		when(patientRepository.findByUser(user)).thenReturn(new Patient());
		assertNotNull(userService.getPatient("username"));
	}

	@Test
	void testAddSocialUser() throws Exception {
		when(userRepository.findByUsername("username")).thenReturn(null);
		assertTrue(userService.checkUniqueLoginOnRegister("username"));
		User user = new User();
		user.setLanguage("pl");
		when(userRepository.findByUsername("username")).thenReturn(user);
		assertFalse(userService.checkUniqueLoginOnRegister("username"));
		
		Patient patient = new Patient();
		patient.setId(11L);
		patient.setUser(user);	
		Role role2 = new Role();
		role2.setId(2L);
		Optional<Role> optionalRole2 = Optional.of(role2);
		when(roleRepository.findById(role2.getId())).thenReturn(optionalRole2);
		
		userService.addSocialUser(patient);
        verify(patientRepository).save(argumentCaptorPatient.capture());
        assertEquals(patient, argumentCaptorPatient.getValue());
		assertNotNull(patient.getRegisterDateTime());
	}

	@Test
	void testUpdatePatientFromSocialMedia() throws Exception {
		User userFromFrontend = new User();
		userFromFrontend.setLanguage("pl");
		Patient patientFromFrontend = new Patient();
		patientFromFrontend.setUser(userFromFrontend);
		User userFromDatabase = new User();
		Patient patientFromDatabase = new Patient();
		patientFromDatabase.setUser(userFromDatabase);
		
		userService.updatePatientFromSocialMedia(patientFromFrontend, patientFromDatabase);
        verify(patientRepository).save(argumentCaptorPatient.capture());
        assertEquals(patientFromFrontend, argumentCaptorPatient.getValue());
		assertNotNull(patientFromFrontend.getEditDateTime());
	}

	@Test
	void testAddDoctor() throws Exception {
		User user = new User();
		user.setUsername("username");
		user.setEmail("email");
		user.setLanguage("pl");
		Doctor doctor = new Doctor();
		doctor.setUser(user);
		Role role3 = new Role();
		role3.setId(3L);
		Optional<Role> optionalRole3 = Optional.of(role3);
		when(roleRepository.findById(role3.getId())).thenReturn(optionalRole3);
		
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.addDoctor(doctor);
        verify(doctorRepository).save(argumentCaptorDoctor.capture());
        assertEquals(doctor, argumentCaptorDoctor.getValue());
		assertNotNull(doctor.getRegisterDateTime());
	}

	@Test
	void testGetAllDoctors() throws Exception {
		Iterable<Doctor> iterableDoctor = IterableUtil.iterable(new Doctor(), new Doctor());
		when(doctorRepository.findAll()).thenReturn(iterableDoctor);
		assertEquals(2, userService.getAllDoctors().size());
	}

	@Test
	void testGetDoctor() throws Exception {
		User user = new User();
		when(userRepository.findByUsername("username")).thenReturn(user);
		when(doctorRepository.findByUser(user)).thenReturn(new Doctor());
		assertNotNull(userService.getDoctor("username"));
	}

	@Test
	void testUpdateDoctor() throws Exception {
		User user = new User();
		user.setPassword("passwordField");
		Doctor doctor = new Doctor();
		doctor.setId(11L);
		doctor.setUser(user);
		Optional<Doctor> optionalDoctor = Optional.of(doctor);
		when(doctorRepository.findById(doctor.getId())).thenReturn(optionalDoctor);

		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.updateDoctor(doctor);
        verify(doctorRepository).save(argumentCaptorDoctor.capture());
        assertEquals(doctor, argumentCaptorDoctor.getValue());
		assertNotNull(doctor.getEditDateTime());
	}

	@Test
	void testSelfUpdateDoctor() throws Exception {
		User user = new User();
		Doctor doctor = new Doctor();
		doctor.setId(11L);
		doctor.setUser(user);
		doctor.setUserRegister(user);
		Optional<Doctor> optionalDoctor = Optional.of(doctor);
		when(doctorRepository.findById(doctor.getId())).thenReturn(optionalDoctor);

		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(user);
		
		userService.selfUpdateDoctor(doctor);
        verify(doctorRepository).save(argumentCaptorDoctor.capture());
        assertEquals(doctor, argumentCaptorDoctor.getValue());
		assertNotNull(doctor.getEditDateTime());
	}

	@Test
	void testUpdatetDoctorWorkingWeek() throws Exception {
		WorkingHourMap workingHourMap = new WorkingHourMap();
		workingHourMap.setWorkingTime(LocalDateTime.now().toLocalTime().minusHours(3));
		workingHourMap.setWorking(true);
		List<WorkingHourMap> workingHourMapList = new ArrayList<>();
		workingHourMapList.add(workingHourMap);
		WorkingDayMap workingDayMap = new WorkingDayMap();
		workingDayMap.setVisitDate(LocalDate.now());
		workingDayMap.setWorkingHourMapList(workingHourMapList);
		List<WorkingDayMap> workingWeekMap = new ArrayList<>();
		workingWeekMap.add(workingDayMap);
		WorkingWeek workingWeek = new WorkingWeek();
		workingWeek.setWorkingWeekMap(workingWeekMap);
		
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		String token = requestTokenHeader.substring(7);
		String username = "username";
		when(jwtService.getUsername(token)).thenReturn(username);
		User user = new User();
		user.setId(99L);
		when(userRepository.findByUsername(username)).thenReturn(user);
		
		userService.updatetDoctorWorkingWeek(workingWeek);
		verify(workingWeekRepository).save(argumentCaptorWorkingWeek.capture());
		assertEquals(99, argumentCaptorWorkingWeek.getValue().getUserEdit().getId());
		assertTrue(argumentCaptorWorkingWeek.getValue().getWorkingWeekMap().get(0).isWorking());
	}

	@Test
	void testAddOrUpdateMedicalDocumentation() throws Exception {
		User userFromRequest = new User();
		userFromRequest.setId(99L);
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(userFromRequest);
		when(patientRepository.findByUser(userFromRequest)).thenReturn(new Patient());
		
		MedicalDocument medicalDocument1 = new MedicalDocument();
		medicalDocument1.setId(11L);
		MedicalByteFile medicalByteFile1 = new MedicalByteFile();
		medicalByteFile1.setFileBase64("file,Base64-1");
		medicalDocument1.setMedicalByteFile(medicalByteFile1);
		MedicalDocument medicalDocument2 = new MedicalDocument();
		medicalDocument2.setId(22L);
		MedicalByteFile medicalByteFile2 = new MedicalByteFile();
		medicalByteFile2.setFileBase64("file,Base64-2");
		medicalDocument2.setMedicalByteFile(medicalByteFile2);
		List<MedicalDocument> medicalDocumentList = Arrays.asList(medicalDocument1, medicalDocument2);
		
		userService.addOrUpdateMedicalDocumentation(medicalDocumentList);
		verify(patientRepository).save(argumentCaptorPatient.capture());
		assertEquals("file,Base64-2", argumentCaptorPatient.getValue().getMedicalDocumentList().get(1).getMedicalByteFile().getFileBase64());
	}

	@Test
	void testRemoveMedicalDocument() {
		User userFromRequest = new User();
		userFromRequest.setId(99L);
		String requestTokenHeader = "requestTokenHeader";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		when(jwtService.getUsername(requestTokenHeader.substring(7))).thenReturn("username");
		when(userRepository.findByUsername("username")).thenReturn(userFromRequest);
		
		Long medicalDocId = 33L;
		medicalByteFileRepository.deleteByMedicalDocumentId(medicalDocId);
		verify(medicalByteFileRepository).deleteByMedicalDocumentId(argumentCaptorLong.capture());
		assertEquals(33, argumentCaptorLong.getValue());
	}

	@Test
	void testGetMedicalByteFile() {
		Long medicalDocId = 44L;
		byte [] fileByte = "file".getBytes();
		MedicalByteFile medicalByteFile = new MedicalByteFile();
		medicalByteFile.setFile(fileByte);
		Optional<MedicalByteFile> optionalMedicalByteFile = Optional.of(medicalByteFile);
		when(medicalByteFileRepository.findByMedicalDocumentId(medicalDocId)).thenReturn(optionalMedicalByteFile);
		
		assertEquals(fileByte, userService.getMedicalByteFile(medicalDocId));
	}

	@Test
	void testRemoveUserPhoto() throws Exception {
		User user = new User();
		user.setUsername("username");
		user.setPhoto("photo".getBytes());
		when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
		
		userService.removeUserPhoto(user.getUsername());
		verify(userRepository).save(argumentCaptorUser.capture());
		assertNull(argumentCaptorUser.getValue().getPhoto());
	}
}
