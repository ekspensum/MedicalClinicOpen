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

import pl.aticode.medicalclinic.controller.EmployeeController;
import pl.aticode.medicalclinic.entity.user.Employee;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.service.UserService;

class EmployeeControllerTest {

	private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private User user;
    private Employee employee;
    private String jsonEmployee;
    
    @InjectMocks
    private EmployeeController employeeController;
    @Mock
	private UserService userService;
	@Captor
	private ArgumentCaptor<Employee> argumentCaptorEmployee;
    
	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
		user = new User();
		user.setUsername("username");
		employee = new Employee();
		employee.setId(44L);
		employee.setUser(user);
		jsonEmployee = objectMapper.writeValueAsString(employee);
	}

	@Test
	void testGetEmployee() throws Exception {
		when(userService.getEmployee(user.getUsername())).thenReturn(employee);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getEmployee").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonEmployee));
        
        doThrow(new Exception("TEST")).when(userService).getEmployee(any(String.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getEmployee").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testSelfUpdateEmployee() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateEmployee").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("NOTUNIQUE"));
        
        when(userService.checkUniqueLoginOnUpdate(any(Employee.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateEmployee").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        verify(userService).selfUpdateEmployee(argumentCaptorEmployee.capture());
        assertEquals(employee.getId(), argumentCaptorEmployee.getValue().getId());
        
        doThrow(new Exception("TEST")).when(userService).selfUpdateEmployee(any(Employee.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateEmployee").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

}
