package pl.aticode.medicalclinic.unit.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

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

import pl.aticode.medicalclinic.controller.OwnerController;
import pl.aticode.medicalclinic.entity.user.Employee;
import pl.aticode.medicalclinic.entity.user.Owner;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.service.UserService;

class OwnerControllerTest {

	private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private User user;
    private Employee employee;
    private String jsonEmployee;
    private Owner owner;
    private String jsonOwner;
    
    @InjectMocks
    private OwnerController ownerController;
    @Mock
	private UserService userService;
	@Captor
	private ArgumentCaptor<Employee> argumentCaptorEmployee;
	@Captor
	private ArgumentCaptor<Owner> argumentCaptorOwner;
    
	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
		user = new User();
		user.setUsername("username");
		employee = new Employee();
		employee.setId(33L);
		employee.setUser(user);
		jsonEmployee = objectMapper.writeValueAsString(employee);
		owner = new Owner();
		owner.setId(55L);
		owner.setUser(user);
		jsonOwner = objectMapper.writeValueAsString(owner);
	}

	@Test
	void testAddAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addAdmin").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("NOTUNIQUE"));
        
        when(userService.checkUniqueLoginOnRegister(employee.getUser().getUsername())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addAdmin").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        verify(userService).addAdmin(argumentCaptorEmployee.capture());
        assertEquals(employee.getId(), argumentCaptorEmployee.getValue().getId());
       
        doThrow(new ConstraintViolationException(null)).when(userService).addAdmin(any(Employee.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addAdmin").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("INVALID"));
        
        doThrow(new Exception("TEST")).when(userService).addAdmin(any(Employee.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addAdmin").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testUpdateAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateAdmin").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        verify(userService).updateAdminOrEmployee(argumentCaptorEmployee.capture());
        assertEquals(employee.getId(), argumentCaptorEmployee.getValue().getId());
        
        doThrow(new Exception("TEST")).when(userService).updateAdminOrEmployee(any(Employee.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateAdmin").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonEmployee))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetOwner() throws Exception {
		when(userService.getOwner(user.getUsername())).thenReturn(owner);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getOwner").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonOwner));
        
        doThrow(new Exception("TEST")).when(userService).getOwner(any(String.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/getOwner").contentType(MediaType.TEXT_PLAIN_VALUE).content(user.getUsername()))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetAllEmployees() throws Exception {
		List<Employee> employeeList = new ArrayList<>();
		employeeList.add(new Employee());
		employeeList.add(new Employee());
		String jsonEmployeeList = objectMapper.writeValueAsString(employeeList);
		when(userService.getAllEmployees()).thenReturn(employeeList);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllEmployees").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonEmployeeList));
		
		doThrow(new PersistenceException("TEST")).when(userService).getAllEmployees();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllEmployees").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testSelfUpdateOwner() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateOwner").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonOwner))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("NOTUNIQUE"));
        
        when(userService.checkUniqueLoginOnUpdate(any(Owner.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateOwner").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonOwner))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        verify(userService).selfUpdateOwner(argumentCaptorOwner.capture());
        assertEquals(owner.getId(), argumentCaptorOwner.getValue().getId());
        
        doThrow(new Exception("TEST")).when(userService).selfUpdateOwner(any(Owner.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/selfUpdateOwner").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonOwner))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

}
