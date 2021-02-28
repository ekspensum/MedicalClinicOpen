package pl.aticode.medicalclinic.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

@Service
public class VisitService {
	
	private final static Logger logger = LoggerFactory.getLogger(VisitService.class);

	@Autowired
	private VisitRepository visitRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private HibernateSearchService searchService;
	@Autowired
	private DoctorFreeDayRepository doctorFreeDayRepository;
	@Autowired
	private ClinicFreeDayRepository clinicFreeDayRepository;
	@Autowired
	private DoctorRepository doctorRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AlertVisitDateQuartzJobService alertVisitDateQuartzJobService;

	
	public VisitService() {
	}
	
	public VisitService(UserService userService, HibernateSearchService searchService,
			DoctorFreeDayRepository doctorFreeDayRepository, DoctorRepository doctorRepository) {
		this.userService = userService;
		this.searchService = searchService;
		this.doctorFreeDayRepository = doctorFreeDayRepository;
		this.doctorRepository = doctorRepository;
	}

	public VisitService(VisitRepository visitRepository, UserService userService, HibernateSearchService searchService,
			DoctorFreeDayRepository doctorFreeDayRepository, ClinicFreeDayRepository clinicFreeDayRepository,
			DoctorRepository doctorRepository, EmailService emailService, MessageSource messageSource,
			AlertVisitDateQuartzJobService alertVisitDateQuartzJobService) {
		this.visitRepository = visitRepository;
		this.userService = userService;
		this.searchService = searchService;
		this.doctorFreeDayRepository = doctorFreeDayRepository;
		this.clinicFreeDayRepository = clinicFreeDayRepository;
		this.doctorRepository = doctorRepository;
		this.emailService = emailService;
		this.messageSource = messageSource;
		this.alertVisitDateQuartzJobService = alertVisitDateQuartzJobService;
	}


	/**
	 * Get all doctors include working week for agenda page.
	 * @param dayStart
	 * @param dayEnd
	 * @return doctors list
	 * @throws Exception
	 */
	public List<Doctor> getAllDoctorsForAgendaPage(long dayStart, long dayEnd) throws Exception {
		Iterable<Doctor> iterable = doctorRepository.findAll();
	    List<Doctor> doctorList = new ArrayList<>();
	    iterable.forEach(doctorList::add);
	    Iterator<Doctor> iterator = doctorList.iterator();
	    while(iterator.hasNext()) {
	    	Doctor doctor = iterator.next();
	    	if(!doctor.getUser().isEnabled()) {
	    		iterator.remove();
	    	}
	    }
		for (Doctor doctor : doctorList) {
			WorkingWeek doctorReservedAndFreeTimeMap = getDoctorReservedAndFreeTimeMap(doctor, dayStart, dayEnd);
			doctor.setWorkingWeek(doctorReservedAndFreeTimeMap);
		}
		return doctorList;
	}
	
	
	/**
	 * Check visit term.
	 * @param visitData
	 * @return true if visit term is taken, return false if visit term is free.
	 */
	public boolean checkVisitTerm(VisitData visitData) {
		Visit visit = searchService
				.searchVisitByVisitDateTimeKeywordQuery(LocalDateTime.of(visitData.getVisitDate(), visitData.getVisitTime()));
		if(visit != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Schedule visit by employee with send email confirmation.
	 * @param visitData
	 * @throws Exception
	 */
	@Transactional
	public void scheduleVisitByEmployee(VisitData visitData) throws Exception {
		Patient patient = userService.getPatient(visitData.getPatientUsername());
		Doctor doctor = userService.getDoctor(visitData.getDoctorUsername());
		Employee employee = userService.getEmployee(visitData.getEmployeeUsername());
		Visit visit = new Visit();
		visit.setPatient(patient);
		visit.setDoctor(doctor);
		visit.setEmployee(employee);
		visit.setVisitDateTime(LocalDateTime.of(visitData.getVisitDate(), visitData.getVisitTime()));
		visit.setPatientAilmentsInfo(visitData.getPatientAilmentsInfo());
		visit.setVisitStatus(VisitStatus.PLANNED);
		visit.setReservationDateTime(LocalDateTime.now());
		visitRepository.save(visit);
        String emailSubject = messageSource.getMessage("visit.add.email.subject", null, new Locale(patient.getUser().getLanguage()));
        String emailContent = messageSource.getMessage("visit.add.email.content",
                new String[]{patient.getUser().getFirstName(), patient.getUser().getLastName(), 
                		visit.getVisitDateTime().toLocalDate().toString(), visit.getVisitDateTime().toLocalTime().toString(),
                		doctor.getUser().getFirstName(), doctor.getUser().getLastName(),
                		emailService.getMailFrom()},
                new Locale(patient.getUser().getLanguage()));
        emailService.sendEmail(patient.getUser().getEmail(), emailSubject, emailContent);
        alertVisitDateQuartzJobService.setAlertDateOfVisitJob(visit);
	}
	
	
	/**
	 * Delete selected visit with send email confirmation.
	 * @param visitId
	 * @throws Exception if selected visit date time is after date time now and this visit has status completed.
	 */
	@Transactional
	public void deleteVisit(Long visitId) throws Exception {
		Optional<Visit> visitOptional = visitRepository.findById(visitId);
		Visit visit = visitOptional.get();
		if(visit.getVisitDateTime().isAfter(LocalDateTime.now()) && !visit.getVisitStatus().equals(VisitStatus.COMPLETED)) {
			User user = visit.getPatient().getUser();
			visitRepository.deleteById(visitId);
			alertVisitDateQuartzJobService.removeAlertDateOfVisitJob(visit);
			User userFromRequest = userService.getUserFromRequest();
			logger.info("Delete visit id: {} by user {} {}", visitId, userFromRequest.getFirstName(), userFromRequest.getLastName());
			String emailSubject = messageSource.getMessage("visit.delete.email.subject", null, new Locale(user.getLanguage()));
			String emailContent = messageSource.getMessage("visit.delete.email.content", new String[] {
					user.getFirstName(), user.getLastName(),
					visit.getVisitDateTime().toLocalDate().toString(), visit.getVisitDateTime().toLocalTime().toString(),
					visit.getDoctor().getUser().getFirstName(), visit.getDoctor().getUser().getLastName(),
					emailService.getMailFrom() },
					new Locale(user.getLanguage()));
			emailService.sendEmail(user.getEmail(), emailSubject, emailContent);
		} else {
			String msg = "Attempt delete visit id: {} with past date or having status COMPLETED.";
			logger.info(msg, visitId);
			throw new IllegalStateException(msg);
		}
	}
	
	/**
	 * Save or update doctor diagnosis with referrals if exists and set visit status on completed. 
	 * @param visit
	 * @throws Exception
	 */
	@Transactional
	public void makeDiagnosisAndUpdateVisit(Visit visit) throws Exception {
		Diagnosis diagnosis = visit.getDiagnosis();
		if(diagnosis.getRegisterDateTime() == null) {
			diagnosis.setRegisterDateTime(LocalDateTime.now());
		} else {
			diagnosis.setUpdateDateTime(LocalDateTime.now());
		}
		List<Referral> referralList = diagnosis.getReferralList();
		for (Referral referral : referralList) {
			referral.setDiagnosis(diagnosis);
			if(referral.getRegisterDateTime() == null) {
				referral.setRegisterDateTime(LocalDateTime.now());
			}
		}
		visit.setVisitStatus(VisitStatus.COMPLETED);
		visitRepository.save(visit);
	}
}
