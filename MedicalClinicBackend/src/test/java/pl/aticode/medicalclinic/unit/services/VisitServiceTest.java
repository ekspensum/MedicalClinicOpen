package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;

import pl.aticode.medicalclinic.dao.user.DoctorRepository;
import pl.aticode.medicalclinic.dao.visit.ClinicFreeDayRepository;
import pl.aticode.medicalclinic.dao.visit.DoctorFreeDayRepository;
import pl.aticode.medicalclinic.dao.visit.VisitRepository;
import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.Employee;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.entity.user.WorkingWeek;
import pl.aticode.medicalclinic.entity.visit.ClinicFreeDay;
import pl.aticode.medicalclinic.entity.visit.Diagnosis;
import pl.aticode.medicalclinic.entity.visit.DoctorFreeDay;
import pl.aticode.medicalclinic.entity.visit.Referral;
import pl.aticode.medicalclinic.entity.visit.Visit;
import pl.aticode.medicalclinic.entity.visit.VisitStatus;
import pl.aticode.medicalclinic.model.VisitData;
import pl.aticode.medicalclinic.model.WorkingDayMap;
import pl.aticode.medicalclinic.model.WorkingHourMap;
import pl.aticode.medicalclinic.service.AlertVisitDateQuartzJobService;
import pl.aticode.medicalclinic.service.EmailService;
import pl.aticode.medicalclinic.service.HibernateSearchService;
import pl.aticode.medicalclinic.service.UserService;
import pl.aticode.medicalclinic.service.VisitService;

class VisitServiceTest {

	private WorkingWeek workingWeek;
	private LocalDateTime localDateTimeNow;
	private DoctorFreeDay doctorFreeDay;
	private ClinicFreeDay clinicFreeDay;
	
	@InjectMocks
	private VisitService visitService;
	@Mock
	private VisitRepository visitRepository;
	@Mock
	private UserService userService;
	@Mock
	private HibernateSearchService hibernateSearchService;
	@Mock
	private DoctorFreeDayRepository doctorFreeDayRepository;
	@Mock
	private ClinicFreeDayRepository clinicFreeDayRepository;
	@Mock
	private DoctorRepository doctorRepository;
	@Mock
    private EmailService emailService;
	@Mock
    private MessageSource messageSource;
	@Mock
    private AlertVisitDateQuartzJobService alertVisitDateQuartzJobService;
	@Captor
	private ArgumentCaptor<Visit> argumentCaptorVisit;
	@Captor
	private ArgumentCaptor<ClinicFreeDay> argumentCaptorClinicFreeDay;
	@Captor
	private ArgumentCaptor<Long> argumentCaptorLong;
	@Captor
	private ArgumentCaptor<DoctorFreeDay> argumentCaptorDoctorFreeDay;
	
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		WorkingHourMap workingHourMap = new WorkingHourMap();
		workingHourMap.setWorkingTime(LocalDateTime.now().toLocalTime().minusHours(3));
		List<WorkingHourMap> workingHourMapList = new ArrayList<>();
		workingHourMapList.add(workingHourMap);
		WorkingDayMap workingDayMap = new WorkingDayMap();
		workingDayMap.setVisitDate(LocalDate.now());
		workingDayMap.setWorkingHourMapList(workingHourMapList);
		List<WorkingDayMap> workingWeekMap = new ArrayList<>();
		workingWeekMap.add(workingDayMap);
		workingWeek = new WorkingWeek();
		workingWeek.setWorkingWeekMap(workingWeekMap);
		localDateTimeNow = LocalDateTime.now();
		doctorFreeDay = new DoctorFreeDay();
		doctorFreeDay.setFreeDay(LocalDate.now());
		clinicFreeDay = new ClinicFreeDay();
		clinicFreeDay.setFreeDay(LocalDate.now());
		List<ClinicFreeDay> clinicFreeDayList = new ArrayList<>();
		clinicFreeDayList.add(clinicFreeDay);
		when(clinicFreeDayRepository.findByFreeDayBetween(
				localDateTimeNow.plusDays(0).toLocalDate(),	
				localDateTimeNow.plusDays(7).toLocalDate())).thenReturn(clinicFreeDayList);
	}

	@Test
	void testGetAllDoctorsForScheduleVisit() throws Exception {
		Doctor doctor1 = new Doctor();
		doctor1.setId(33L);
		doctor1.setWorkingWeek(workingWeek);
		Doctor doctor2 = new Doctor();
		doctor2.setId(33L);
		doctor2.setWorkingWeek(workingWeek);
		List<Doctor> doctorList = new ArrayList<>();
		doctorList.add(doctor1);
		doctorList.add(doctor2);
		when(userService.getAllDoctors()).thenReturn(doctorList);
		
		doctorFreeDay.setDoctor(doctor1);
		List<DoctorFreeDay> doctorFreeDayList = new ArrayList<>();
		doctorFreeDayList.add(doctorFreeDay);
		when(doctorFreeDayRepository.findByDoctorAndFreeDayBetween(doctor1, 
				localDateTimeNow.plusDays(0).toLocalDate(), 
				localDateTimeNow.plusDays(7).toLocalDate())).thenReturn(doctorFreeDayList);
		
		assertEquals(2, visitService.getAllDoctorsForScheduleVisit(0, 7).size());
	}

	@Test
	void testGetAllDoctorsForAgendaPage() throws Exception {
		User user1 = new User();
		user1.setEnabled(true);
		Doctor doctor1 = new Doctor();
		doctor1.setId(33L);
		doctor1.setUser(user1);
		doctor1.setWorkingWeek(workingWeek);
		User user2 = new User();
		user2.setEnabled(false);
		Doctor doctor2 = new Doctor();
		doctor2.setId(33L);
		doctor2.setUser(user2);
		doctor2.setWorkingWeek(workingWeek);
		Iterable<Doctor> iterableDoctor = IterableUtil.iterable(doctor1, doctor2);
		when(doctorRepository.findAll()).thenReturn(iterableDoctor);

		doctorFreeDay.setDoctor(doctor1);
		List<DoctorFreeDay> doctorFreeDayList = new ArrayList<>();
		doctorFreeDayList.add(doctorFreeDay);
		when(doctorFreeDayRepository.findByDoctorAndFreeDayBetween(doctor1, 
				localDateTimeNow.plusDays(0).toLocalDate(), 
				localDateTimeNow.plusDays(7).toLocalDate())).thenReturn(doctorFreeDayList);
		
		assertEquals(1, visitService.getAllDoctorsForAgendaPage(0, 7).size());
	}

	@Test
	void testGetDoctorReservedAndFreeTimeMap() throws Exception {
		Doctor doctor = new Doctor();
		doctor.setWorkingWeek(workingWeek);
		
		doctorFreeDay.setDoctor(doctor);
		List<DoctorFreeDay> doctorFreeDayList = new ArrayList<>();
		doctorFreeDayList.add(doctorFreeDay);
		when(doctorFreeDayRepository.findByDoctorAndFreeDayBetween(doctor, 
				localDateTimeNow.plusDays(0).toLocalDate(), 
				localDateTimeNow.plusDays(7).toLocalDate())).thenReturn(doctorFreeDayList);
		
		assertEquals(workingWeek, visitService.getDoctorReservedAndFreeTimeMap(doctor, 0, 7));
	}

	@Test
	void testCheckVisitTerm() {
		Visit visit = new Visit();
		VisitData visitData = new VisitData();
		visitData.setVisitDate(localDateTimeNow.toLocalDate());
		visitData.setVisitTime(localDateTimeNow.toLocalTime());
		
		when(hibernateSearchService.searchVisitByVisitDateTimeKeywordQuery(LocalDateTime.of(visitData.getVisitDate(), 
				visitData.getVisitTime()))).thenReturn(visit);
		assertTrue(visitService.checkVisitTerm(visitData));
		
		visit = null;
		when(hibernateSearchService.searchVisitByVisitDateTimeKeywordQuery(LocalDateTime.of(visitData.getVisitDate(), 
				visitData.getVisitTime()))).thenReturn(visit);
		assertFalse(visitService.checkVisitTerm(visitData));
	}

	@Test
	void testScheduleVisitByPatient() throws Exception {
		User user = new User();
		user.setLanguage("pl");
		Patient patient = new Patient();
		patient.setCity("city");
		patient.setUser(user);
		Doctor doctor = new Doctor();
		doctor.setUser(user);
		VisitData visitData = new VisitData();
		visitData.setVisitDate(localDateTimeNow.toLocalDate());
		visitData.setVisitTime(localDateTimeNow.toLocalTime());
		visitData.setDoctorUsername("doctorUsername");
		visitData.setPatientUsername("patientUsername");
		
		when(userService.getPatient(visitData.getPatientUsername())).thenReturn(patient);
		when(userService.getDoctor(visitData.getDoctorUsername())).thenReturn(doctor);
		
		visitService.scheduleVisitByPatient(visitData);
		verify(visitRepository).save(argumentCaptorVisit.capture());
		assertEquals("city", argumentCaptorVisit.getValue().getPatient().getCity());
		assertEquals(VisitStatus.PLANNED, argumentCaptorVisit.getValue().getVisitStatus());
	}

	@Test
	void testScheduleVisitByEmployee() throws Exception {
		User user = new User();
		user.setLanguage("pl");
		Patient patient = new Patient();
		patient.setCity("city");
		patient.setUser(user);
		Doctor doctor = new Doctor();
		doctor.setUser(user);
		Employee employee = new Employee();
		employee.setUser(user);
		VisitData visitData = new VisitData();
		visitData.setVisitDate(localDateTimeNow.toLocalDate());
		visitData.setVisitTime(localDateTimeNow.toLocalTime());
		visitData.setDoctorUsername("doctorUsername");
		visitData.setPatientUsername("patientUsername");
		visitData.setEmployeeUsername("employeeUsername");
		
		when(userService.getPatient(visitData.getPatientUsername())).thenReturn(patient);
		when(userService.getDoctor(visitData.getDoctorUsername())).thenReturn(doctor);
		when(userService.getEmployee(visitData.getEmployeeUsername())).thenReturn(employee);
		
		visitService.scheduleVisitByEmployee(visitData);
		verify(visitRepository).save(argumentCaptorVisit.capture());
		assertEquals("city", argumentCaptorVisit.getValue().getPatient().getCity());
		assertEquals(VisitStatus.PLANNED, argumentCaptorVisit.getValue().getVisitStatus());
		assertNotNull(argumentCaptorVisit.getValue().getEmployee());
	}

	@Test
	void testCheckClinicFreeDay() {
		List<ClinicFreeDay> clinicFreeDayList = new ArrayList<>();
		clinicFreeDayList.add(new ClinicFreeDay());
		
		when(clinicFreeDayRepository.findByFreeDay(localDateTimeNow.toLocalDate())).thenReturn(clinicFreeDayList);
		assertTrue(visitService.checkClinicFreeDay(localDateTimeNow.toLocalDate()));
		clinicFreeDayList.clear();
		when(clinicFreeDayRepository.findByFreeDay(localDateTimeNow.toLocalDate())).thenReturn(clinicFreeDayList);
		assertFalse(visitService.checkClinicFreeDay(localDateTimeNow.toLocalDate()));
	}

	@Test
	void testAddClinicFreeDay() throws Exception {
		when(userService.getUserFromRequest()).thenReturn(new User());
		visitService.addClinicFreeDay(LocalDate.now());
		verify(clinicFreeDayRepository).save(argumentCaptorClinicFreeDay.capture());
		assertNotNull(argumentCaptorClinicFreeDay.getValue().getUserRegister());
		assertEquals(LocalDate.now(), argumentCaptorClinicFreeDay.getValue().getFreeDay());
	}

	@Test
	void testGetClinicFreeDayList() {
		Iterable<ClinicFreeDay> iterableClinicFreeDay = IterableUtil.iterable(new ClinicFreeDay(), new ClinicFreeDay());
		when(clinicFreeDayRepository.findAll(Sort.by("freeDay").descending())).thenReturn(iterableClinicFreeDay);
		assertEquals(2, visitService.getClinicFreeDayList().size());
	}

	@Test
	void testRemoveClinicFreeDay() throws Exception {
		visitService.removeClinicFreeDay(77L);
		verify(clinicFreeDayRepository).deleteById(argumentCaptorLong.capture());
		assertEquals(77, argumentCaptorLong.getValue());
	}

	@Test
	void testCheckDoctorFreeDay() {
		List<DoctorFreeDay> doctorFreeDayList = new ArrayList<>();
		doctorFreeDayList.add(new DoctorFreeDay());
		Doctor doctor = new Doctor();
		String doctorUsername = "doctorUsername";
		when(doctorRepository.findByUserUsername(doctorUsername)).thenReturn(doctor);
		
		when(doctorFreeDayRepository.findByDoctorAndFreeDay(doctor, localDateTimeNow.toLocalDate())).thenReturn(doctorFreeDayList);
		assertTrue(visitService.checkDoctorFreeDay(doctorUsername, localDateTimeNow.toLocalDate()));
		doctorFreeDayList.clear();
		when(doctorFreeDayRepository.findByDoctorAndFreeDay(doctor, localDateTimeNow.toLocalDate())).thenReturn(doctorFreeDayList);
		assertFalse(visitService.checkDoctorFreeDay(doctorUsername, localDateTimeNow.toLocalDate()));
	}

	@Test
	void testAddDoctorFreeDay() throws Exception {
		Doctor doctor = new Doctor();
		doctor.setId(44L);
		String doctorUsername = "doctorUsername";
		when(doctorRepository.findByUserUsername(doctorUsername)).thenReturn(doctor);
		when(userService.getUserFromRequest()).thenReturn(new User());
		visitService.addDoctorFreeDay(doctorUsername, LocalDate.now());
		verify(doctorFreeDayRepository).save(argumentCaptorDoctorFreeDay.capture());
		assertNotNull(argumentCaptorDoctorFreeDay.getValue().getUserRegister());
		assertEquals(LocalDate.now(), argumentCaptorDoctorFreeDay.getValue().getFreeDay());
		assertEquals(44, argumentCaptorDoctorFreeDay.getValue().getDoctor().getId());
	}

	@Test
	void testGetDoctorFreeDayListByAdmin() {
		Iterable<DoctorFreeDay> iterableDoctorFreeDay = IterableUtil.iterable(new DoctorFreeDay(), new DoctorFreeDay());
		when(doctorFreeDayRepository.findAll(Sort.by("freeDay").descending())).thenReturn(iterableDoctorFreeDay);
		assertEquals(2, visitService.getDoctorFreeDayListByAdmin().size());
	}

	@Test
	void testGetDoctorFreeDayListByDoctor() {
		String doctorUsername = "doctorUsername";
		DoctorFreeDay doctorFreeDay1 = new DoctorFreeDay();
		doctorFreeDay1.setFreeDay(LocalDate.now());
		DoctorFreeDay doctorFreeDay2 = new DoctorFreeDay();
		doctorFreeDay2.setFreeDay(LocalDate.now().plusDays(1));
		List<DoctorFreeDay> doctorFreeDayList = Arrays.asList(doctorFreeDay1, doctorFreeDay2);
		when(doctorFreeDayRepository.findByDoctorUsername(doctorUsername)).thenReturn(doctorFreeDayList);
		assertEquals(2, visitService.getDoctorFreeDayListByDoctor(doctorUsername).size());
	}

	@Test
	void testRemoveDoctorFreeDay() throws Exception {
		visitService.removeDoctorFreeDay(77L);
		verify(doctorFreeDayRepository).deleteById(argumentCaptorLong.capture());
		assertEquals(77, argumentCaptorLong.getValue());
	}

	@Test
	void testGetPatientVisits() {
		VisitData visitData = new VisitData();
		visitData.setPatientUsername("patientUsername");
		visitData.setVisitDateFrom(LocalDate.parse("2021-01-01"));
		visitData.setVisitDateTo(LocalDate.parse("2021-01-31"));
		
		List<Visit> visitList = new ArrayList<>();
		visitList.add(new Visit());
		when(hibernateSearchService.searchVisitByPatientVisitDateTimeRange(visitData.getPatientUsername(), 
				visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59))).thenReturn(visitList);
		
		assertEquals(1, visitService.getPatientVisits(visitData).size());
	}

	@Test
	void testDeleteVisit() throws Exception {
		User user = new User();
		user.setLanguage("pl");
		Patient patient = new Patient();
		patient.setUser(user);
		Doctor doctor = new Doctor();
		doctor.setUser(user);
		Visit visit = new Visit();
		visit.setId(88L);
		visit.setPatient(patient);
		visit.setDoctor(doctor);
		visit.setVisitDateTime(localDateTimeNow.plusDays(1));
		visit.setVisitStatus(VisitStatus.PLANNED);
		Optional<Visit> visitOptional = Optional.of(visit);
		when(visitRepository.findById(visit.getId())).thenReturn(visitOptional);
		when(userService.getUserFromRequest()).thenReturn(user);
		
		visitService.deleteVisit(88L);
		
		visit.setVisitStatus(VisitStatus.COMPLETED);
		assertThrows(IllegalStateException.class, () -> {
			visitService.deleteVisit(88L);
		});
	}

	@Test
	void testGetVisit() {
		Visit visit = new Visit();
		visit.setId(66L);
		Optional<Visit> visitOptional = Optional.of(visit);
		when(visitRepository.findById(visit.getId())).thenReturn(visitOptional);
		
		assertEquals(visit, visitService.getVisit(visit.getId()));
	}

	@Test
	void testGetVisitsByStatusPlanned() {
		VisitData visitData = new VisitData();
		visitData.setVisitDateFrom(LocalDate.parse("2021-01-01"));
		visitData.setVisitDateTo(LocalDate.parse("2021-01-31"));
		
		List<Visit> visitList = Arrays.asList(new Visit(), new Visit());
		when(hibernateSearchService.searchVisitByVisitStatusPlannedAndDateTimeRange(
				visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59))).thenReturn(visitList);
		
		assertEquals(2, visitService.getVisitsByStatusPlanned(visitData).size());
	}

	@Test
	void testConfirmationVisit() throws Exception {
		Visit visit = new Visit();
		visit.setId(77L);
		Optional<Visit> visitOptional = Optional.of(visit);
		when(visitRepository.findById(visit.getId())).thenReturn(visitOptional);
		when(userService.getUserFromRequest()).thenReturn(new User());
		visitService.confirmationVisit(visit.getId());
		verify(visitRepository).save(argumentCaptorVisit.capture());
		assertEquals(VisitStatus.CONFIRMED, argumentCaptorVisit.getValue().getVisitStatus());
	}

	@Test
	void testGetDoctorPlannedVisitsByDate() {
		VisitData visitData = new VisitData();
		visitData.setDoctorUsername("doctorUsername");
		visitData.setVisitDateFrom(LocalDate.parse("2021-01-01"));
		visitData.setVisitDateTo(LocalDate.parse("2021-01-31"));
		
		List<Visit> visitList = new ArrayList<>();
		visitList.add(new Visit());
		when(hibernateSearchService.searchVisitByDoctorVisitDateTimeRangeAndPlanned(visitData.getDoctorUsername(), 
				visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59))).thenReturn(visitList);
		
		assertEquals(1, visitService.getDoctorPlannedVisitsByDate(visitData).size());
	}

	@Test
	void testGetDoctorVisitsByDateAndStatus() {
		VisitData visitData = new VisitData();
		visitData.setDoctorUsername("doctorUsername");
		visitData.setVisitDateFrom(LocalDate.parse("2021-01-01"));
		visitData.setVisitDateTo(LocalDate.parse("2021-01-31"));
		visitData.setVisitStatus(VisitStatus.PLANNED);
		
		List<Visit> visitList = Arrays.asList(new Visit(), new Visit());
		when(hibernateSearchService.searchVisitByDoctorVisitDateTimeRangeAndStatus(visitData.getDoctorUsername(), 
				visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59),
				visitData.getVisitStatus())).thenReturn(visitList);
		
		assertEquals(2, visitService.getDoctorVisitsByDateAndStatus(visitData).size());
	}

	@Test
	void testMakeDiagnosisAndUpdateVisit() throws Exception {
		List<Referral> referralList = Arrays.asList(new Referral());
		Diagnosis diagnosis = new Diagnosis();
		diagnosis.setReferralList(referralList);
		Visit visit = new Visit();
		visit.setDiagnosis(diagnosis);
		
		visitService.makeDiagnosisAndUpdateVisit(visit);
		verify(visitRepository).save(argumentCaptorVisit.capture());
		assertEquals(VisitStatus.COMPLETED, argumentCaptorVisit.getValue().getVisitStatus());
	}

}
