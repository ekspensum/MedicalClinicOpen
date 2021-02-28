package pl.aticode.medicalclinic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.WorkingWeek;
import pl.aticode.medicalclinic.service.UserService;

@CrossOrigin
@RestController
@PreAuthorize("hasRole('DOCTOR')")
@RequestMapping("/api")
public class DoctorController {

	private final static Logger logger = LoggerFactory.getLogger(DoctorController.class);
	
	@Autowired
	private UserService userService;
	
	/**
	 * Receive username, search doctor and send to frontend with handling exceptions. 
	 * @param username
	 * @return
	 */
	@PostMapping(path = "/getDoctor")
	public ResponseEntity<MappingJacksonValue> getDoctor(@RequestBody String username) {
		try {
	        FilterProvider filterProvider = new SimpleFilterProvider()
	        		.addFilter("userFilter", SimpleBeanPropertyFilter.serializeAll())
	        		.addFilter("doctorFilter", SimpleBeanPropertyFilter.serializeAll());
			Doctor doctor = userService.getDoctor(username);
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(doctor);
			mappingJacksonValue.setFilters(filterProvider);
			return ResponseEntity.ok(mappingJacksonValue);
		} catch (Exception e) {
			logger.error("ERROR get doctor", e);
			return new ResponseEntity<MappingJacksonValue>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Receive doctor, check unique username and send to user service to self update with handling exceptions and confirmation. 
	 * @param doctor
	 * @return
	 */
	@PutMapping(path = "/selfUpdateDoctor")
	public ResponseEntity<String> selfUpdateDoctor(@RequestBody Doctor doctor){
		if (!userService.checkUniqueLoginOnUpdate(doctor)) {
			return ResponseEntity.ok("NOTUNIQUE");
		}
		try {
			userService.selfUpdateDoctor(doctor);
			logger.info("Updated doctor {} {}", doctor.getUser().getFirstName(), doctor.getUser().getLastName());
			return ResponseEntity.ok("UPDATED");
		} catch (Exception e) {
			logger.error("ERROR update doctor", e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Receive username, search doctor working week and send to frontend with handling exceptions. 
	 * @param username
	 * @return
	 */
	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@PostMapping(path = "/getDoctorWorkingWeek")
	public ResponseEntity<WorkingWeek> getDoctorWorkingWeek(@RequestBody String username) {
		try {
			Doctor doctor = userService.getDoctor(username);
			return ResponseEntity.ok(doctor.getWorkingWeek());
		} catch (Exception e) {
			logger.error("ERROR get doctor", e);
			return new ResponseEntity<WorkingWeek>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Receive workingweek and send to user service to update with handling exceptions and confirmation. 
	 * @param workingWeek
	 * @return
	 */
	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@PutMapping(path = "/updateDoctorWorkingWeek")
	public ResponseEntity<String> updatetDoctorWorkingWeek(@RequestBody WorkingWeek workingWeek){
		try {
			userService.updatetDoctorWorkingWeek(workingWeek);
			logger.info("Updated working week id {}", workingWeek.getId());
			return ResponseEntity.ok("UPDATED");
		} catch (Exception e) {
			logger.error("ERROR update working week", e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
