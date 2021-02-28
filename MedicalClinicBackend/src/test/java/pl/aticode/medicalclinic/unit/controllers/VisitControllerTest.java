package pl.aticode.medicalclinic.unit.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import pl.aticode.medicalclinic.controller.VisitController;
import pl.aticode.medicalclinic.dao.user.DoctorRepository;
import pl.aticode.medicalclinic.dao.visit.DoctorFreeDayRepository;
import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.entity.visit.ClinicFreeDay;
import pl.aticode.medicalclinic.entity.visit.DoctorFreeDay;
import pl.aticode.medicalclinic.entity.visit.Visit;
import pl.aticode.medicalclinic.entity.visit.VisitStatus;
import pl.aticode.medicalclinic.model.DoctorFreeDayData;
import pl.aticode.medicalclinic.model.VisitData;
import pl.aticode.medicalclinic.service.HibernateSearchService;
import pl.aticode.medicalclinic.service.UserService;
import pl.aticode.medicalclinic.service.VisitService;

class VisitControllerTest {

	private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @InjectMocks
    private VisitController visitController;
    @Mock
	private UserService userService;
    @Mock
	private HibernateSearchService hibernateSearchService;
    @Mock
    private VisitService visitService;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DoctorFreeDayRepository doctorFreeDayRepository;
    
	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
		MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
	}

	@Test
	void testGetAllDoctorsForScheduleVisit() throws Exception {
		List<Doctor> doctorList = new ArrayList<>();
		doctorList.add(new Doctor());
		doctorList.add(new Doctor());
		String jsonDoctorList = objectMapper.writeValueAsString(doctorList);
		when(visitService.getAllDoctorsForScheduleVisit(0, 7)).thenReturn(doctorList);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDoctorsForScheduleVisit/0/7").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonDoctorList));
		
		doThrow(new PersistenceException("TEST")).when(visitService).getAllDoctorsForScheduleVisit(0, 7);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDoctorsForScheduleVisit/0/7").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testScheduleVisitByPatient() throws Exception {
		VisitData visitData = new VisitData();
		visitData.setDoctorUsername("doctorUsername");
		String jsonvisitData = objectMapper.writeValueAsString(visitData);
		when(visitService.checkVisitTerm(any(VisitData.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/scheduleVisitByPatient")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonvisitData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("VISIT_TERM_TAKEN"));
        
        when(visitService.checkVisitTerm(any(VisitData.class))).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/scheduleVisitByPatient")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonvisitData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("SCHEDULED_VISIT"));
        
        doThrow(new PersistenceException("TEST")).when(visitService).scheduleVisitByPatient(any(VisitData.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/scheduleVisitByPatient")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonvisitData))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testScheduleVisitByEmployee() throws Exception {
		VisitData visitData = new VisitData();
		visitData.setDoctorUsername("doctorUsername");
		String jsonVisitData = objectMapper.writeValueAsString(visitData);
		when(visitService.checkVisitTerm(any(VisitData.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/scheduleVisitByEmployee")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("VISIT_TERM_TAKEN"));
        
        when(visitService.checkVisitTerm(any(VisitData.class))).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/scheduleVisitByEmployee")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("SCHEDULED_VISIT"));
        
        doThrow(new PersistenceException("TEST")).when(visitService).scheduleVisitByEmployee(any(VisitData.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/scheduleVisitByEmployee")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testAddClinicFreeDay() throws Exception {
		ClinicFreeDay clinicFreeDay = new ClinicFreeDay();
		List<ClinicFreeDay> clinicFreeDaylist = new ArrayList<>();
		clinicFreeDaylist.add(clinicFreeDay);
		
		LocalDate localDateNow = LocalDate.now();
		when(visitService.checkClinicFreeDay(localDateNow)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addClinicFreeDay")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(localDateNow.toString()))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        
        when(visitService.checkClinicFreeDay(localDateNow)).thenReturn(false);
        when(visitService.getClinicFreeDayList()).thenReturn(clinicFreeDaylist);
        String jsonClinicFreeDaylist = objectMapper.writeValueAsString(clinicFreeDaylist);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addClinicFreeDay")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(localDateNow.toString()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonClinicFreeDaylist));
        
        doThrow(new PersistenceException("TEST")).when(visitService).addClinicFreeDay(localDateNow);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addClinicFreeDay")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(localDateNow.toString()))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetClinicFreeDayList() throws Exception {
		ClinicFreeDay clinicFreeDay = new ClinicFreeDay();
		List<ClinicFreeDay> clinicFreeDaylist = new ArrayList<>();
		clinicFreeDaylist.add(clinicFreeDay);
		String jsonClinicFreeDaylist = objectMapper.writeValueAsString(clinicFreeDaylist);
		
		when(visitService.getClinicFreeDayList()).thenReturn(clinicFreeDaylist);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getClinicFreeDayList").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonClinicFreeDaylist));
		
		doThrow(new PersistenceException("TEST")).when(visitService).getClinicFreeDayList();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getClinicFreeDayList")).andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testRemoveClinicFreeDay() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/removeClinicFreeDay/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("FREE_DAY_REMOVED"));
		
		doThrow(new PersistenceException("TEST")).when(visitService).removeClinicFreeDay(7L);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/removeClinicFreeDay/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testAddDoctorFreeDayByAdmin() throws Exception {
		DoctorFreeDay doctorFreeDay = new DoctorFreeDay();
		List<DoctorFreeDay> doctorFreeDayList = new ArrayList<>();
		doctorFreeDayList.add(doctorFreeDay);
		
		DoctorFreeDayData doctorFreeDayData = new DoctorFreeDayData();
		doctorFreeDayData.setDoctorFreeDay(LocalDate.of(2021, 1, 23));
		doctorFreeDayData.setDoctorUsername("doctorUsername");
		String jsonDoctorFreeDayData = objectMapper.writeValueAsString(doctorFreeDayData);
		
		Doctor doctor = new Doctor();
		when(doctorRepository.findByUserUsername("doctorUsername")).thenReturn(doctor);
		when(doctorFreeDayRepository.findByDoctorAndFreeDay(doctor, doctorFreeDayData.getDoctorFreeDay())).thenReturn(doctorFreeDayList);

		visitService = new VisitService(userService, hibernateSearchService, doctorFreeDayRepository, doctorRepository);
		visitController = new VisitController(visitService);
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
		
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctorFreeDayByAdmin")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctorFreeDayData))
        .andExpect(MockMvcResultMatchers.status().is(423));
        
        when(visitService.getDoctorFreeDayListByAdmin()).thenReturn(doctorFreeDayList);
        String jsonDoctorFreeDaylist = objectMapper.writeValueAsString(doctorFreeDayList);
        doctorFreeDayData.setDoctorFreeDay(LocalDate.of(2021, 1, 24));
        jsonDoctorFreeDayData = objectMapper.writeValueAsString(doctorFreeDayData);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctorFreeDayByAdmin")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctorFreeDayData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonDoctorFreeDaylist));
        
        userService = null;
		visitService = new VisitService(userService, hibernateSearchService, doctorFreeDayRepository, doctorRepository);
		visitController = new VisitController(visitService);
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctorFreeDayByAdmin")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctorFreeDayData))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testAddDoctorFreeDayByDoctor() throws Exception {
		DoctorFreeDay doctorFreeDay = new DoctorFreeDay();
		List<DoctorFreeDay> doctorFreeDayList = new ArrayList<>();
		doctorFreeDayList.add(doctorFreeDay);
		
		DoctorFreeDayData doctorFreeDayData = new DoctorFreeDayData();
		doctorFreeDayData.setDoctorFreeDay(LocalDate.of(2021, 1, 23));
		doctorFreeDayData.setDoctorUsername("doctorUsername");
		String jsonDoctorFreeDayData = objectMapper.writeValueAsString(doctorFreeDayData);
		
		Doctor doctor = new Doctor();
		when(doctorRepository.findByUserUsername("doctorUsername")).thenReturn(doctor);
		when(doctorFreeDayRepository.findByDoctorAndFreeDay(doctor, doctorFreeDayData.getDoctorFreeDay())).thenReturn(doctorFreeDayList);

		visitService = new VisitService(userService, hibernateSearchService, doctorFreeDayRepository, doctorRepository);
		visitController = new VisitController(visitService);
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
		
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctorFreeDayByDoctor")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctorFreeDayData))
        .andExpect(MockMvcResultMatchers.status().is(423));
        
        when(visitService.getDoctorFreeDayListByDoctor(doctorFreeDayData.getDoctorUsername())).thenReturn(doctorFreeDayList);
        String jsonDoctorFreeDaylist = objectMapper.writeValueAsString(doctorFreeDayList);
        doctorFreeDayData.setDoctorFreeDay(LocalDate.of(2021, 1, 24));
        jsonDoctorFreeDayData = objectMapper.writeValueAsString(doctorFreeDayData);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctorFreeDayByDoctor")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctorFreeDayData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonDoctorFreeDaylist));
        
        userService = null;
		visitService = new VisitService(userService, hibernateSearchService, doctorFreeDayRepository, doctorRepository);
		visitController = new VisitController(visitService);
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addDoctorFreeDayByDoctor")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctorFreeDayData))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetDoctorFreeDayListByAdmin() throws Exception {
		DoctorFreeDay doctorFreeDay = new DoctorFreeDay();
		List<DoctorFreeDay> doctorFreeDayList = new ArrayList<>();
		doctorFreeDayList.add(doctorFreeDay);
		
        when(visitService.getDoctorFreeDayListByAdmin()).thenReturn(doctorFreeDayList);
        String jsonDoctorFreeDaylist = objectMapper.writeValueAsString(doctorFreeDayList);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getDoctorFreeDayListByAdmin"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonDoctorFreeDaylist));
        
        doThrow(new PersistenceException("TEST")).when(visitService).getDoctorFreeDayListByAdmin();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getDoctorFreeDayListByAdmin"))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetDoctorFreeDayListByDoctor() throws Exception {
		DoctorFreeDay doctorFreeDay = new DoctorFreeDay();
		List<DoctorFreeDay> doctorFreeDayList = new ArrayList<>();
		doctorFreeDayList.add(doctorFreeDay);
		
		String username = "username";
        when(visitService.getDoctorFreeDayListByDoctor(username)).thenReturn(doctorFreeDayList);
        String jsonDoctorFreeDaylist = objectMapper.writeValueAsString(doctorFreeDayList);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctorFreeDayListByDoctor")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(username))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonDoctorFreeDaylist));
        
        doThrow(new PersistenceException("TEST")).when(visitService).getDoctorFreeDayListByDoctor(username);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctorFreeDayListByDoctor")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(username))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testRemoveDoctorFreeDay() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/removeDoctorFreeDay/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("FREE_DAY_REMOVED"));
		
		doThrow(new PersistenceException("TEST")).when(visitService).removeDoctorFreeDay(7L);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/removeDoctorFreeDay/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetAllDoctorsForAgendaPage() throws Exception {
		Doctor doctor1 = new Doctor();
		doctor1.setPesel("71020461825");
		Doctor doctor2 = new Doctor();
		doctor2.setPesel("51120334223");
		List<Doctor> doctorList = new ArrayList<>();
		doctorList.add(doctor1);
		doctorList.add(doctor2);
		when(visitService.getAllDoctorsForAgendaPage(0, 7)).thenReturn(doctorList);
		String expectedJsonDoctorList = "[{\"id\":null,\"experience\":null,\"user\":null,\"workingWeek\":null},{\"id\":null,\"experience\":null,\"user\":null,\"workingWeek\":null}]";
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDoctorsForAgendaPage/0/7").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(expectedJsonDoctorList));
		
		doThrow(new PersistenceException("TEST")).when(visitService).getAllDoctorsForAgendaPage(0, 7);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDoctorsForAgendaPage/0/7").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetPatientVisits() throws Exception {
		VisitData visitData = new VisitData();
		visitData.setPatientUsername("patientUsername");
		visitData.setVisitDateFrom(LocalDate.parse("2021-01-01"));
		visitData.setVisitDateTo(LocalDate.parse("2021-01-31"));
		String jsonVisitData = objectMapper.writeValueAsString(visitData);
		
		List<Visit> visitList = new ArrayList<>();
		visitList.add(new Visit());
		when(hibernateSearchService.searchVisitByPatientVisitDateTimeRange(visitData.getPatientUsername(), 
				visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59))).thenReturn(visitList);
		
		visitService = new VisitService(userService, hibernateSearchService, doctorFreeDayRepository, doctorRepository);
		visitController = new VisitController(visitService);
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
		
		String expectedJsonVisitList = "[{\"id\":null,\"visitDateTime\":null,\"visitStatus\":null,\"patient\":null,\"doctor\":null,\"patientAilmentsInfo\":null,\"reservationDateTime\":null}]";
		mockMvc.perform(MockMvcRequestBuilders.post("/api/getPatientVisits")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(expectedJsonVisitList));
		
		doThrow(new PersistenceException("TEST")).when(hibernateSearchService).searchVisitByPatientVisitDateTimeRange(
				visitData.getPatientUsername(),	visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/getPatientVisits")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testDeleteVisit() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/deleteVisit/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("DELETED_VISIT"));
		
		doThrow(new PersistenceException("TEST")).when(visitService).deleteVisit(7L);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/deleteVisit/7").accept(MediaType.ALL_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetVisit() throws Exception {
		Visit visit = new Visit();
		visit.setId(33L);
		String jsonVisit = objectMapper.writeValueAsString(visit);
		
		when(visitService.getVisit(visit.getId())).thenReturn(visit);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getVisit/"+visit.getId()))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonVisit));
		
		doThrow(new PersistenceException("TEST")).when(visitService).getVisit(visit.getId());
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getVisit/"+visit.getId()))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetPlannedVisits() throws Exception {
		VisitData visitData = new VisitData();
		visitData.setPatientUsername("patientUsername");
		visitData.setVisitDateFrom(LocalDate.parse("2021-01-01"));
		visitData.setVisitDateTo(LocalDate.parse("2021-01-31"));
		String jsonVisitData = objectMapper.writeValueAsString(visitData);
		
		List<Visit> visitList = new ArrayList<>();
		visitList.add(new Visit());
		when(hibernateSearchService.searchVisitByVisitStatusPlannedAndDateTimeRange(
				visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59))).thenReturn(visitList);
		
		visitService = new VisitService(userService, hibernateSearchService, doctorFreeDayRepository, doctorRepository);
		visitController = new VisitController(visitService);
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
		
		String expectedJsonVisitList = "[{\"id\":null,\"visitDateTime\":null,\"visitStatus\":null,\"patient\":null,\"doctor\":null,\"reservationDateTime\":null}]";
		mockMvc.perform(MockMvcRequestBuilders.post("/api/getPlannedVisits")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(expectedJsonVisitList));
		
		doThrow(new PersistenceException("TEST")).when(hibernateSearchService).searchVisitByVisitStatusPlannedAndDateTimeRange(
				visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/getPlannedVisits")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testConfirmationVisit() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/confirmationVisit")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content("11"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("CONFIRMED_VISIT"));
		
		doThrow(new PersistenceException("TEST")).when(visitService).confirmationVisit(11L);
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/confirmationVisit")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content("11")).andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetDoctorPlannedVisitsByDate() throws Exception {
		VisitData visitData = new VisitData();
		visitData.setDoctorUsername("doctorUsername");
		visitData.setVisitDateFrom(LocalDate.parse("2021-01-01"));
		visitData.setVisitDateTo(LocalDate.parse("2021-01-31"));
		String jsonVisitData = objectMapper.writeValueAsString(visitData);
		
		List<Visit> visitList = new ArrayList<>();
		visitList.add(new Visit());
		when(hibernateSearchService.searchVisitByDoctorVisitDateTimeRangeAndPlanned(visitData.getDoctorUsername(), 
				visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59))).thenReturn(visitList);
		
		visitService = new VisitService(userService, hibernateSearchService, doctorFreeDayRepository, doctorRepository);
		visitController = new VisitController(visitService);
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
		
		String expectedJsonVisitList = "[{\"id\":null,\"visitDateTime\":null,\"visitStatus\":null,\"patient\":null,\"doctor\":null,\"reservationDateTime\":null}]";
		mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctorPlannedVisitsByDate")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(expectedJsonVisitList));
		
		doThrow(new PersistenceException("TEST")).when(hibernateSearchService).searchVisitByDoctorVisitDateTimeRangeAndPlanned(
				visitData.getDoctorUsername(), visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctorPlannedVisitsByDate")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetDoctorVisitsByDateAndStatus() throws Exception {
		VisitData visitData = new VisitData();
		visitData.setDoctorUsername("doctorUsername");
		visitData.setVisitDateFrom(LocalDate.parse("2021-01-01"));
		visitData.setVisitDateTo(LocalDate.parse("2021-01-31"));
		visitData.setVisitStatus(VisitStatus.COMPLETED);
		String jsonVisitData = objectMapper.writeValueAsString(visitData);
		
		List<Visit> visitList = new ArrayList<>();
		visitList.add(new Visit());
		when(hibernateSearchService.searchVisitByDoctorVisitDateTimeRangeAndStatus(visitData.getDoctorUsername(), 
				visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59),
				visitData.getVisitStatus())).thenReturn(visitList);
		
		visitService = new VisitService(userService, hibernateSearchService, doctorFreeDayRepository, doctorRepository);
		visitController = new VisitController(visitService);
		mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
		
		String expectedJsonVisitList = "[{\"id\":null,\"visitDateTime\":null,\"visitStatus\":null,\"patient\":null,\"doctor\":null,\"reservationDateTime\":null}]";
		mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctorVisitsByDateAndStatus")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(expectedJsonVisitList));
		
		doThrow(new PersistenceException("TEST")).when(hibernateSearchService).searchVisitByDoctorVisitDateTimeRangeAndStatus(
				visitData.getDoctorUsername(), visitData.getVisitDateFrom().atStartOfDay(), visitData.getVisitDateTo().atTime(23, 59),
				visitData.getVisitStatus());
		mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctorVisitsByDateAndStatus")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisitData))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testMakeDiagnosis() throws Exception {
		Visit visit = new Visit();
		Doctor doctor = new Doctor();
		doctor.setUser(new User());
		visit.setDoctor(doctor);
		String jsonVisit = objectMapper.writeValueAsString(visit);
		
        mockMvc.perform(MockMvcRequestBuilders.post("/api/makeDiagnosis")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisit))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED_DIAGNOSIS"));
        
        doThrow(new PersistenceException("TEST")).when(visitService).makeDiagnosisAndUpdateVisit(any(Visit.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/makeDiagnosis")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonVisit))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

}
