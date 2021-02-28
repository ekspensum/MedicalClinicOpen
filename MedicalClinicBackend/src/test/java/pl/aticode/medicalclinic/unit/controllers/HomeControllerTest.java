package pl.aticode.medicalclinic.unit.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import pl.aticode.medicalclinic.controller.HomeController;
import pl.aticode.medicalclinic.entity.user.Company;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.model.ContactUs;
import pl.aticode.medicalclinic.model.RecoverPasswordRequest;
import pl.aticode.medicalclinic.service.ActivationService;
import pl.aticode.medicalclinic.service.CipherService;
import pl.aticode.medicalclinic.service.EmailService;
import pl.aticode.medicalclinic.service.HibernateSearchService;
import pl.aticode.medicalclinic.service.PasswordService;
import pl.aticode.medicalclinic.service.ReCaptchaService;
import pl.aticode.medicalclinic.service.UserService;

class HomeControllerTest {
	
	private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private User user;
    private Patient patient;
    private String jsonPatient;
    
    @InjectMocks
    private HomeController homeController;
	@Mock
	private UserService userService;
	@Mock
	private PasswordService passwordService;
	@Mock
	private CipherService cipherService;
	@Mock
	private ActivationService activationService;
	@Mock
	private EmailService emailService;
	@Mock
	private ReCaptchaService reCaptchaService;
	@Mock
	private HibernateSearchService hibernateSearchService;
	@Mock
    private MessageSource messageSource;
	@Mock
	private HttpServletRequest servletRequest;

	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
		user = new User();
		user.setUsername("username");
		patient = new Patient();
		patient.setId(22L);
		patient.setPesel("72050783671");
		patient.setUser(user);
		jsonPatient = objectMapper.writeValueAsString(patient);
	}

	@Test
	void testForgetPassword() throws Exception {
		RecoverPasswordRequest recoverPasswordRequest = new RecoverPasswordRequest("login", "email@mail.com");
		String jsonRecoverPasswordRequest = objectMapper.writeValueAsString(recoverPasswordRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/forgetPassword")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonRecoverPasswordRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("RECAPTCHA_FAIL"));
        
        String reCaptchHeader = "reCaptchHeader";
		when(reCaptchaService.verify(reCaptchHeader)).thenReturn(true);
		when(passwordService.recoverPassword(recoverPasswordRequest.getLogin(), recoverPasswordRequest.getEmail())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/forgetPassword").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonRecoverPasswordRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("RECOVER_PASS_MAIL"));
        
        doThrow(new IOException("TEST")).when(reCaptchaService).verify(reCaptchHeader);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/forgetPassword").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonRecoverPasswordRequest))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testResetPasswordByLink() throws Exception {
		String resetPasswordString = "resetPasswordString";
		String decodeTokenPassword = "11;"+LocalDateTime.now().plusHours(1);
		when(cipherService.decodeString(resetPasswordString)).thenReturn(decodeTokenPassword);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/resetPasswordByLink").contentType(MediaType.TEXT_PLAIN_VALUE).content(resetPasswordString))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("RESET"));
        
		decodeTokenPassword = "11;"+LocalDateTime.now().minusHours(1);
		when(cipherService.decodeString(resetPasswordString)).thenReturn(decodeTokenPassword);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/resetPasswordByLink").contentType(MediaType.TEXT_PLAIN_VALUE).content(resetPasswordString))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("DELAYED"));
        
		decodeTokenPassword = "NaN;"+LocalDateTime.now().plusHours(1);
		when(cipherService.decodeString(resetPasswordString)).thenReturn(decodeTokenPassword);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/resetPasswordByLink").contentType(MediaType.TEXT_PLAIN_VALUE).content(resetPasswordString))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testContactUs() throws Exception {
		ContactUs contactUs = new ContactUs();
		contactUs.setMessage("message");
		contactUs.setReplyEmail("mail@mail.com");
		contactUs.setSubject("subject");
		String jsonContactUs = objectMapper.writeValueAsString(contactUs);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sendEmailByContactUs")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonContactUs))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("RECAPTCHA_FAIL"));
        
        String reCaptchHeader = "reCaptchHeader";
		when(reCaptchaService.verify(reCaptchHeader)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sendEmailByContactUs").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonContactUs))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("SENT_EMAIL"));
        
        doThrow(new IOException("TEST")).when(reCaptchaService).verify(reCaptchHeader);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sendEmailByContactUs").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonContactUs))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testAddPatient() throws Exception {
		when(hibernateSearchService.searchPatientPeselByKeywordQuery(patient.getPesel())).thenReturn(patient);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByRegistrationPage")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("PATIEN_NOT_ENABLED"));
        
        patient.getUser().setEnabled(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByRegistrationPage")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("PATIEN_ACCOUNT_EXIST"));
        
        when(hibernateSearchService.searchPatientPeselByKeywordQuery(patient.getPesel())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByRegistrationPage")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("NOTUNIQUE"));
        
        when(userService.checkUniqueLoginOnRegister(patient.getUser().getUsername())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByRegistrationPage")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("RECAPTCHA_FAIL"));
        
        String reCaptchHeader = "reCaptchHeader";
		when(reCaptchaService.verify(reCaptchHeader)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByRegistrationPage").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        
        doThrow(new ConstraintViolationException(null)).when(userService).addPatientByRegistrationPage(any(Patient.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByRegistrationPage").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("INVALID"));
        
        doThrow(new IOException("TEST")).when(reCaptchaService).verify(reCaptchHeader);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addPatientByRegistrationPage").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testActivationPatient() throws Exception {
		String encodeActivationStringBase64 = "encodeActivationStringBase64";
		String activationString = "activationString";
		when(cipherService.decodeString(encodeActivationStringBase64)).thenReturn(activationString);
		when(activationService.setActivePatient(activationString)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/activationPatient")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(encodeActivationStringBase64))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ACTIVATED"));
        
        when(activationService.setActivePatient(activationString)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/activationPatient")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(encodeActivationStringBase64))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ACTIVATED_FAIL"));
        
		doThrow(new IllegalStateException("TEST")).when(cipherService).decodeString(encodeActivationStringBase64);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/activationPatient")
        		.contentType(MediaType.TEXT_PLAIN_VALUE).content(encodeActivationStringBase64))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testGetCompanyData() throws Exception {
		Company company = new Company();
		String jsonCompany = objectMapper.writeValueAsString(company);
		when(userService.getCompanyData()).thenReturn(company);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getCompanyData").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonCompany));
		
		doThrow(new PersistenceException("TEST")).when(userService).getCompanyData();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/getCompanyData").accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	void testAddSocialUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addSocialUser")
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("RECAPTCHA_FAIL"));
        
        String reCaptchHeader = "reCaptchHeader";
		when(reCaptchaService.verify(reCaptchHeader)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addSocialUser").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        
		when(hibernateSearchService.searchPatientPeselByKeywordQuery(patient.getPesel())).thenReturn(patient);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addSocialUser").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("PATIEN_NOT_ENABLED"));
        
        user.setEnabled(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addSocialUser").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("UPDATED"));
        
        when(hibernateSearchService.searchPatientPeselByKeywordQuery(patient.getPesel())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addSocialUser").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("ADDED"));
        
        doThrow(new ConstraintViolationException(null)).when(userService).addSocialUser(any(Patient.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addSocialUser").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("INVALID"));
        
        doThrow(new IOException("TEST")).when(reCaptchaService).verify(reCaptchHeader);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addSocialUser").header("reCaptcha", reCaptchHeader)
        		.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPatient))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

}
