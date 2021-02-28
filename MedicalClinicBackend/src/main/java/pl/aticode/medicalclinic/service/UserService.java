package pl.aticode.medicalclinic.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

@Service
public class UserService {

	private final static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private PasswordService passwordService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private OwnerRepository ownerRepository;
	@Autowired
	private PatientRepository patientRepository;
	@Autowired
	private DoctorRepository doctorRepository;
	@Autowired
	private WorkingWeekRepository workingWeekRepository;
	@Autowired
	private ActivationService activationService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private JwtService jwtService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private MedicalByteFileRepository medicalByteFileRepository;
    @Autowired
    private HibernateSearchService hibernateSearchService;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private AlertVisitDateQuartzJobService alertVisitDateQuartzJobService;
	
    /**
     * Method is in use during registration or updating user process. Checking whether
     * entered login/username already exist in databases.
     *
     * @return If not (login is unique) return true. If login exist in
     * database return false (login is not distinct).
     */
    public boolean checkUniqueLoginOnRegister(String login) {
        User user = userRepository.findByUsername(login);
        if (user == null) {
            return true;
        }
        return false;
    }

    /**
     * Method is in use during self edit data user process. Then user can change
     * your login/username. Checking whether entered login/username already
     * exist in databases.
     *
     * @return If not (login is distinct) return true. If login exist in
     * database return false (login is not distinct).
     */
    public boolean checkUniqueLoginOnUpdate(Object objectOfRole) {
    	if(objectOfRole instanceof Patient) {
    		Patient patient = (Patient) objectOfRole;
    		Optional<User> updatingUser = userRepository.findById(patient.getUser().getId());
//          If username/login is not change
      		if (updatingUser.get().getUsername().equals(patient.getUser().getUsername())) {
      			return true;
      		} else {
      			User findUser = userRepository.findByUsername(patient.getUser().getUsername());
      			if (findUser == null) {
      				return true;
      			}
      		}
    	} else if(objectOfRole instanceof Employee) {
    		Employee employee = (Employee) objectOfRole;
    		Optional<User> updatingUser = userRepository.findById(employee.getUser().getId());
//          If username/login is not change
      		if (updatingUser.get().getUsername().equals(employee.getUser().getUsername())) {
      			return true;
      		} else {
      			User findUser = userRepository.findByUsername(employee.getUser().getUsername());
      			if (findUser == null) {
      				return true;
      			}
      		}
    	} else if(objectOfRole instanceof Doctor) {
    		Doctor doctor = (Doctor) objectOfRole;
    		Optional<User> updatingUser = userRepository.findById(doctor.getUser().getId());
//          If username/login is not change
      		if (updatingUser.get().getUsername().equals(doctor.getUser().getUsername())) {
      			return true;
      		} else {
      			User findUser = userRepository.findByUsername(doctor.getUser().getUsername());
      			if (findUser == null) {
      				return true;
      			}
      		}
    	} else if(objectOfRole instanceof Owner) {
    		Owner owner = (Owner) objectOfRole;
    		Optional<User> updatingUser = userRepository.findById(owner.getUser().getId());
//        If username/login is not change
    		if (updatingUser.get().getUsername().equals(owner.getUser().getUsername())) {
    			return true;
    		} else {
    			User findUser = userRepository.findByUsername(owner.getUser().getUsername());
    			if (findUser == null) {
    				return true;
    			}
    		}
    	} 
        return false;
    }
	
    /**
     * Get user
     * @param id
     * @return Optional<User> 
     */
	public User getUser(Long id) {
		Optional<User> user = userRepository.findById(id);
		return user.get();
	}
	
	/**
	 * Decode username from token and find user in database.
	 * @return User
	 */
	public User getUserFromRequest() {
        final String requestTokenHeader = request.getHeader("Authorization");
        final String token = requestTokenHeader.substring(7);
        final String username = jwtService.getUsername(token);
        return userRepository.findByUsername(username);
	}

    
    /**
     * Adding new employee. Method send email to employee with confirmation.
     * Employee has only one role: EMPLOYEE with empty role (select). 
     * Password is random generating and send by email.
     * @param employee
     * @throws Exception
     */
	@Transactional
	@CacheEvict(cacheNames = {"allEmployeesCache"}, allEntries = true)
	public void addEmployee(Employee employee) throws Exception {
		String password = passwordService.createPassword();
		String passwordEncode = passwordEncoder.encode(password);
		User user = employee.getUser();
		user.setEnabled(true);
		user.setPassword(passwordEncode);
		Optional<Role> role1 = roleRepository.findById(1L);
		Optional<Role> role4 = roleRepository.findById(4L);
        List<Role> roles = new ArrayList<>();
        roles.add(role1.get());
        roles.add(role4.get());
        user.setRoles(roles);
        employee.setRegisterDateTime(LocalDateTime.now());
        final User userFromRequest = getUserFromRequest();
        employee.setUserRegister(userFromRequest);
		employeeRepository.save(employee);
		sendEmailForAddAdminEmployee(employee, password);
	}
	
	/**
	 * Return list of all employees include administrators.
	 * @return ArrayList
	 * @throws Exception
	 */
	@Cacheable(cacheNames = "allEmployeesCache")
	public List<Employee> getAllEmployees() throws Exception {
		Iterable<Employee> iterable = employeeRepository.findAll();
	    List<Employee> result = new ArrayList<>();
	    iterable.forEach(result::add);
		return result;
	}

	/**
	 * Returns employee.
	 * @param username
	 * @return Employee
	 * @throws Exception
	 */
	public Employee getEmployee(String username) throws Exception {
		User user = userRepository.findByUsername(username);
		return employeeRepository.findByUser(user);
	}

	/**
	 * Adding new patient by administrator. Password is generating automatically.
	 * Patient is enabled and can login.
	 * Method send email with confirmation including login and password. 
	 * @param patient
	 * @throws Exception
	 */
	@Transactional
	public void addPatientByAdminOrEmployee(Patient patient) throws Exception {
		String password = passwordService.createPassword();
		String passwordEncode = passwordEncoder.encode(password);
		User user = patient.getUser();
		user.setEnabled(true);
		user.setPassword(passwordEncode);
		Optional<Role> role = roleRepository.findById(2L);
        List<Role> roles = new ArrayList<>();
        roles.add(role.get());
        user.setRoles(roles);
        patient.setRegisterDateTime(LocalDateTime.now());
        final User userFromRequest = getUserFromRequest();
        patient.setUserRegister(userFromRequest);
		patientRepository.save(patient);
        String emailSubject = messageSource.getMessage("patient.add.email.subject", null, new Locale(patient.getUser().getLanguage()));
        String emailContent = messageSource.getMessage("patient.add.email.content.admin",
                new String[]{patient.getUser().getFirstName(), patient.getUser().getLastName(), patient.getUser().getUsername(), 
                		password, emailService.getMailFrom()},
                new Locale(patient.getUser().getLanguage()));
        emailService.sendEmail(patient.getUser().getEmail(), emailSubject, emailContent);
	}
	
	/**
	 * Remove patient account with all yours data.
	 * @param patientId
	 * @throws Exception
	 */
	@Transactional
    public void removePatient(Long patientId) throws Exception {
		Optional<Patient> foundPatient = patientRepository.findById(patientId);
		User user = foundPatient.get().getUser();
		List<MedicalDocument> medicalDocumentList = foundPatient.get().getMedicalDocumentList();
		for (MedicalDocument medicalDocument : medicalDocumentList) {
			medicalByteFileRepository.deleteByMedicalDocumentId(medicalDocument.getId());
		}
		List<Visit> patientVisitList = hibernateSearchService.searchVisitToRemoveByPatientUsername(user.getUsername());
		for (Visit visit : patientVisitList) {
			visitRepository.delete(visit);
			alertVisitDateQuartzJobService.removeAlertDateOfVisitJob(visit);
		}
		patientRepository.delete(foundPatient.get());
		User userFromRequest;
		try {
			userFromRequest = getUserFromRequest();
			logger.info("REMOVE account patient {} {} by admin {} {}", user.getFirstName(), user.getLastName(), userFromRequest.getFirstName(), userFromRequest.getLastName());
		} catch (IllegalStateException e) {
			logger.info("REMOVE account patient {} {} by patient self.", user.getFirstName(), user.getLastName());
		}
        String emailSubject = messageSource.getMessage("patient.remove.email.subject", null, new Locale(user.getLanguage()));
        String emailContent = messageSource.getMessage("patient.remove.email.content",
                new String[]{user.getFirstName(), user.getLastName(), user.getUsername(), emailService.getMailFrom()},
                new Locale(user.getLanguage()));
        emailService.sendEmail(user.getEmail(), emailSubject, emailContent);
    }
	
	/**
	 * Update doctor working week.
	 * @param workingWeek
	 * @throws Exception
	 */
	@Transactional
	@CacheEvict(cacheNames = "allDoctorsCache", allEntries = true)
	public void updatetDoctorWorkingWeek(WorkingWeek workingWeek) throws Exception {
		workingWeek.setEditDateTime(LocalDateTime.now());
		User userFromRequest = getUserFromRequest();
		workingWeek.setUserEdit(userFromRequest);
		List<WorkingDayMap> workingWeekMap = workingWeek.getWorkingWeekMap();
		for (int i = 0; i < workingWeekMap.size(); i++) {
			boolean workingDay = false;
			for (int j = 0; j < workingWeekMap.get(i).getWorkingHourMapList().size(); j++) {
				if(workingWeekMap.get(i).getWorkingHourMapList().get(j).isWorking()) {
					workingDay = true;
					break;
				}
			}
			workingWeekMap.get(i).setWorking(workingDay);				
		}
		workingWeek.setWorkingWeekMap(workingWeekMap);
		workingWeekRepository.save(workingWeek);
	}
	
	/**
	 * Add or update patient medical documentation.
	 * @param medicalDocumentList
	 * @throws Exception
	 */
	@Transactional
	public void addOrUpdateMedicalDocumentation(List<MedicalDocument> medicalDocumentList) throws Exception {
		User userFromRequest = getUserFromRequest();
		Patient patient = patientRepository.findByUser(userFromRequest);
		patient.setMedicalDocumentList(medicalDocumentList);
		for (MedicalDocument medicalDocument : medicalDocumentList) {
			medicalDocument.setPatient(patient);
			if(medicalDocument.getMedicalByteFile() != null) {
				MedicalByteFile medicalByteFile = new MedicalByteFile();
				medicalDocument.getMedicalByteFile().setMedicalDocument(medicalDocument);
				byte[] byteFile = Base64.getMimeDecoder().decode(medicalDocument.getMedicalByteFile().getFileBase64().split(",")[1]);
				medicalByteFile.setFile(byteFile);
				medicalByteFile.setMedicalDocument(medicalDocument);
				medicalByteFileRepository.save(medicalByteFile);
			}
        	medicalDocument.setRegisterDateTime(LocalDateTime.now());
		}
		patientRepository.save(patient);
		logger.info("Add or update medical documentation for patient: {} {}", userFromRequest.getFirstName(), userFromRequest.getLastName());
	}
	
	/**
	 * Remove patient medical document.
	 * @param medicalDocId
	 * @throws Exception
	 */
	@Transactional
	public void removeMedicalDocument(Long medicalDocId) throws Exception {
		User userFromRequest = getUserFromRequest();
		medicalByteFileRepository.deleteByMedicalDocumentId(medicalDocId);
		logger.info("Removed medical document by user: {} {}", userFromRequest.getFirstName(), userFromRequest.getLastName());
	}
	
	/**
	 * Get medical document file.
	 * @param medicalDocId
	 * @return byte array
	 */
	public byte [] getMedicalByteFile(Long medicalDocId) {
		return medicalByteFileRepository.findByMedicalDocumentId(medicalDocId).get().getFile();
	}
	
	/**
	 * Remove user image by set null photo.
	 * @param login
	 * @throws Exception
	 */
	@Transactional
    public void removeUserPhoto(String login) throws Exception {
        User user = userRepository.findByUsername(login);
        user.setPhoto(null);
        userRepository.save(user);
        logger.info("Removed photo user: {} {}", user.getFirstName(), user.getLastName());
    }
    
	
	//PRIVATE METHODS
	/**
	 * Send email with confirmation at adding admin or employee.
	 * @param employee
	 * @param password
	 */
	private void sendEmailForAddAdminEmployee(Employee employee, String password) {
        String emailSubject = messageSource.getMessage("employee.add.email.subject", null, new Locale(employee.getUser().getLanguage()));
        String emailContent = messageSource.getMessage("employee.add.email.content",
                new String[]{employee.getUser().getFirstName(), employee.getUser().getLastName(), employee.getUser().getUsername(), password, emailService.getMailFrom()},
                new Locale(employee.getUser().getLanguage()));
        emailService.sendEmail(employee.getUser().getEmail(), emailSubject, emailContent);
	}
}
