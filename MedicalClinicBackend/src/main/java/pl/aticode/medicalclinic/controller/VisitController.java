package pl.aticode.medicalclinic.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.visit.ClinicFreeDay;
import pl.aticode.medicalclinic.entity.visit.DoctorFreeDay;
import pl.aticode.medicalclinic.entity.visit.Visit;
import pl.aticode.medicalclinic.model.DoctorFreeDayData;
import pl.aticode.medicalclinic.model.VisitData;
import pl.aticode.medicalclinic.service.VisitService;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class VisitController {

	private final static Logger logger = LoggerFactory.getLogger(VisitController.class);
	
	@Autowired
	private VisitService visitService;
	
	public VisitController() {
	}

	public VisitController(VisitService visitService) {
		this.visitService = visitService;
	}

	/**
	 * Get all doctors from data base include his working week and send to frontend with handling exceptions. Provide data to select at schedule visit.
	 * @param from
	 * @param to
	 * @return allDoctors
	 */
	@PreAuthorize("hasAnyRole('PATIENT','EMPLOYEE')")
	@GetMapping(path = "/getAllDoctorsForScheduleVisit/{from}/{to}")
	public ResponseEntity<MappingJacksonValue> getAllDoctorsForScheduleVisit(@PathVariable(name = "from") int from, @PathVariable(name = "to") int to) {
		try {
	        FilterProvider filterProvider = new SimpleFilterProvider()
	        		.addFilter("userFilter", SimpleBeanPropertyFilter.serializeAll())
	        		.addFilter("doctorFilter", SimpleBeanPropertyFilter.serializeAll());
			List<Doctor> allDoctors = visitService.getAllDoctorsForScheduleVisit(from, to);
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(allDoctors);
			mappingJacksonValue.setFilters(filterProvider);
			return ResponseEntity.ok(mappingJacksonValue);
		} catch (Exception e) {
			logger.error("ERROR get all doctors for schedule visit", e);
			return new ResponseEntity<MappingJacksonValue>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Receive visit data and send to visit service to schedule visit by patient with handling exceptions and confirmation.
	 * @param visitData
	 * @return
	 */
	@PreAuthorize("hasRole('PATIENT')")
	@PostMapping(path = "/scheduleVisitByPatient")
	public ResponseEntity<String> scheduleVisitByPatient(@RequestBody VisitData visitData) {
		try {
			boolean checkVisitTerm = visitService.checkVisitTerm(visitData);
			if(checkVisitTerm) {
				return ResponseEntity.ok("VISIT_TERM_TAKEN");
			}
			visitService.scheduleVisitByPatient(visitData);
			logger.info("Scheduled visit by patient with date: {} {}, doctor username: {}, patient username: {}", 
					visitData.getVisitDate(), visitData.getVisitTime(), visitData.getDoctorUsername(), visitData.getPatientUsername());
			return ResponseEntity.ok("SCHEDULED_VISIT");
		} catch (Exception e) {
			logger.error("ERROR schedule visit by patient", e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Receive visit data and send to visit service to schedule visit by employee with handling exceptions and confirmation.
	 * @param visitData
	 * @return
	 */
	@PreAuthorize("hasRole('EMPLOYEE')")
	@PostMapping(path = "/scheduleVisitByEmployee")
	public ResponseEntity<String> scheduleVisitByEmployee(@RequestBody VisitData visitData) {
		try {
			boolean checkVisitTerm = visitService.checkVisitTerm(visitData);
			if(checkVisitTerm) {
				return ResponseEntity.ok("VISIT_TERM_TAKEN");
			}
			visitService.scheduleVisitByEmployee(visitData);
			logger.info("Scheduled visit by employee with date: {} {}, doctor username: {}, patient username: {}, employee username: {}", 
					visitData.getVisitDate(), visitData.getVisitTime(), visitData.getDoctorUsername(), visitData.getPatientUsername(), 
					visitData.getEmployeeUsername());
			return ResponseEntity.ok("SCHEDULED_VISIT");
		} catch (Exception e) {
			logger.error("ERROR schedule visit by employee", e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * In use by administrator.
	 * Receive doctor free day data, check it taken and send to visit service with handling exceptions and confirmation when free day is already taken.
	 * Method return all doctor free days in list to frontend after added this free day.
	 * Method also filter json data exclude doctor working week and user photo for better performance. 
	 * @param doctorFreeDayData
	 * @return doctorFreeDayList filtered
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(path = "/addDoctorFreeDayByAdmin")
	public ResponseEntity<MappingJacksonValue> addDoctorFreeDayByAdmin(@RequestBody DoctorFreeDayData doctorFreeDayData){
		try {
			boolean checkDoctorFreeDay = visitService.checkDoctorFreeDay(doctorFreeDayData.getDoctorUsername(), doctorFreeDayData.getDoctorFreeDay());
			if(checkDoctorFreeDay) {
				return new ResponseEntity<MappingJacksonValue>(HttpStatus.LOCKED);
			}
			visitService.addDoctorFreeDay(doctorFreeDayData.getDoctorUsername(), doctorFreeDayData.getDoctorFreeDay());
			SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.serializeAllExcept("photo");
			SimpleBeanPropertyFilter doctorFilter = SimpleBeanPropertyFilter.serializeAllExcept("workingWeek");
	        FilterProvider filterProvider = new SimpleFilterProvider()
	        		.addFilter("doctorFilter", doctorFilter)
	        		.addFilter("userFilter", userFilter);
			List<DoctorFreeDay> doctorFreeDayList = visitService.getDoctorFreeDayListByAdmin();
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(doctorFreeDayList);
			mappingJacksonValue.setFilters(filterProvider);
			logger.info("Added doctor free day with doctor username: {} and free day: {}", doctorFreeDayData.getDoctorUsername(), 
					doctorFreeDayData.getDoctorFreeDay());
			return ResponseEntity.ok(mappingJacksonValue);
		} catch (Exception e) {
			logger.error("ERROR add doctor free day", e);
			return new ResponseEntity<MappingJacksonValue>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * In use by doctor.
	 * Receive doctor free day data, check it taken and send to visit service with handling exceptions and confirmation when free day is already taken.
	 * Method return all doctor free days in list to frontend after added this free day.
	 * Method also filter json data exclude doctor working week and user photo for better performance. 
	 * @param doctorFreeDayData
	 * @return doctorFreeDayList filtered
	 */
	@PreAuthorize("hasRole('DOCTOR')")
	@PostMapping(path = "/addDoctorFreeDayByDoctor")
	public ResponseEntity<MappingJacksonValue> addDoctorFreeDayByDoctor(@RequestBody DoctorFreeDayData doctorFreeDayData){
		try {
			boolean checkDoctorFreeDay = visitService.checkDoctorFreeDay(doctorFreeDayData.getDoctorUsername(), doctorFreeDayData.getDoctorFreeDay());
			if(checkDoctorFreeDay) {
				return new ResponseEntity<MappingJacksonValue>(HttpStatus.LOCKED);
			}
			visitService.addDoctorFreeDay(doctorFreeDayData.getDoctorUsername(), doctorFreeDayData.getDoctorFreeDay());
			SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.serializeAllExcept("photo");
			SimpleBeanPropertyFilter doctorFilter = SimpleBeanPropertyFilter.serializeAllExcept("workingWeek");
	        FilterProvider filterProvider = new SimpleFilterProvider()
	        		.addFilter("doctorFilter", doctorFilter)
	        		.addFilter("userFilter", userFilter);
			List<DoctorFreeDay> doctorFreeDayList = visitService.getDoctorFreeDayListByDoctor(doctorFreeDayData.getDoctorUsername());
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(doctorFreeDayList);
			mappingJacksonValue.setFilters(filterProvider);
			logger.info("Added doctor free day with doctor username: {} and free day: {}", doctorFreeDayData.getDoctorUsername(), 
					doctorFreeDayData.getDoctorFreeDay());
			return ResponseEntity.ok(mappingJacksonValue);
		} catch (Exception e) {
			logger.error("ERROR add doctor free day", e);
			return new ResponseEntity<MappingJacksonValue>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Receive range of date, send to visit service and get all doctors include his working week and send to frontend with handling exceptions. 
	 * Method also filter sensitive doctors data.
	 * @param from
	 * @param to
	 * @return
	 */
	@GetMapping(path = "/getAllDoctorsForAgendaPage/{from}/{to}")
	public ResponseEntity<MappingJacksonValue> getAllDoctorsForAgendaPage(@PathVariable(name = "from") int from, @PathVariable(name = "to") int to) {
		try {
			SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.serializeAllExcept("username", "roles", "email", "language");
			SimpleBeanPropertyFilter doctorFilter = SimpleBeanPropertyFilter.serializeAllExcept("pesel", "country", "zipCode", "city", "street", 
					"streetNo", "unitNo", "phone", "registerDateTime", "editDateTime");
	        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("userFilter", userFilter).addFilter("doctorFilter", doctorFilter);
			List<Doctor> allDoctors = visitService.getAllDoctorsForAgendaPage(from, to);
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(allDoctors);
			mappingJacksonValue.setFilters(filterProvider);
			return ResponseEntity.ok(mappingJacksonValue);
		} catch (Exception e) {
			logger.error("ERROR get all doctors for agenda page", e);
			return new ResponseEntity<MappingJacksonValue>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Receive visitData, send to visit service to get patient visit list, filters it and send to frontend with handling exceptions.
	 * Method filter json data exclude doctor working week, user photo, medical document, employee and diagnosis for better performance. 
	 * @param visitData
	 * @return
	 */
	@PreAuthorize("hasAnyRole('PATIENT','EMPLOYEE')")
	@PostMapping(path = "/getPatientVisits")
	public ResponseEntity<MappingJacksonValue> getPatientVisits(@RequestBody VisitData visitData) {
		try {
			SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.serializeAllExcept("photo");
			SimpleBeanPropertyFilter patientFilter = SimpleBeanPropertyFilter.serializeAllExcept("medicalDocumentList");
			SimpleBeanPropertyFilter doctorFilter = SimpleBeanPropertyFilter.serializeAllExcept("workingWeek");
	        SimpleBeanPropertyFilter visitFilter = SimpleBeanPropertyFilter.serializeAllExcept("employee", "diagnosis");
	        FilterProvider filterProvider = new SimpleFilterProvider()
	        		.addFilter("userFilter", userFilter)
	        		.addFilter("patientFilter", patientFilter)
			        .addFilter("doctorFilter", doctorFilter)
			        .addFilter("visitFilter", visitFilter);
			List<Visit> patientVisits = visitService.getPatientVisits(visitData);
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(patientVisits);
			mappingJacksonValue.setFilters(filterProvider);
			return ResponseEntity.ok(mappingJacksonValue);
		} catch (Exception e) {
			logger.error("ERROR get patient visits", e);
			return new ResponseEntity<MappingJacksonValue>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
