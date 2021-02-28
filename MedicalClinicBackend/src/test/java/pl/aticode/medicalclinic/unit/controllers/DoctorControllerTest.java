package pl.aticode.medicalclinic.unit.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import pl.aticode.medicalclinic.controller.DoctorController;
import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.entity.user.WorkingWeek;
import pl.aticode.medicalclinic.service.UserService;

class DoctorControllerTest {

	private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private User user;
    private Doctor doctor;
    private String jsonDoctor;
    private String jsonDoctorWorkingWeek;
    
    @InjectMocks
    private DoctorController doctorController;
    @Mock
	private UserService userService;
	@Captor
	private ArgumentCaptor<Doctor> argumentCaptorDoctor;
    
	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();
		user = new User();
		user.setUsername("username");
		doctor = new Doctor();
		doctor.setId(44L);
		doctor.setUser(user);
		WorkingWeek workingWeek = new WorkingWeek();
		workingWeek.setId(11L);
		workingWeek.setWorkingWeekMapByte("workingWeekMapByte".getBytes());
		doctor.setWorkingWeek(workingWeek);
		jsonDoctor = objectMapper.writeValueAsString(doctor);
		jsonDoctorWorkingWeek = objectMapper.writeValueAsString(workingWeek); 
	}

	@Test
	void testGetDoctor() throws Exception {
		when(userService.getDoctor(user.getUsername())).thenReturn(doctor);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctor").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonDoctor));
        
        doThrow(new Exception("TEST")).when(userService).getDoctor(any(String.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctor").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetDoctorWorkingWeek() throws Exception {
		when(userService.getDoctor(user.getUsername())).thenReturn(doctor);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctorWorkingWeek").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonDoctorWorkingWeek));
        
        doThrow(new Exception("TEST")).when(userService).getDoctor(any(String.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getDoctorWorkingWeek").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}
	
	@Test
	void testSelfUpdateDoctor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateDoctor").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctor))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("NOTUNIQUE"));
        
        when(userService.checkUniqueLoginOnUpdate(any(Doctor.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateDoctor").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctor))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        verify(userService).selfUpdateDoctor(argumentCaptorDoctor.capture());
        assertEquals(doctor.getId(), argumentCaptorDoctor.getValue().getId());
        
        doThrow(new Exception("TEST")).when(userService).selfUpdateDoctor(any(Doctor.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateDoctor").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonDoctor))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testUpdateDoctorWorkingWeek() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateDoctorWorkingWeek").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonDoctorWorkingWeek))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
        doThrow(new Exception("TEST")).when(userService).updatetDoctorWorkingWeek(any(WorkingWeek.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateDoctorWorkingWeek").contentType(MediaType.APPLICATION_JSON_VALUE)
        		.content(jsonDoctorWorkingWeek))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}
}
