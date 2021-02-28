package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import pl.aticode.medicalclinic.dao.user.UserRepository;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.service.CipherService;
import pl.aticode.medicalclinic.service.EmailService;
import pl.aticode.medicalclinic.service.PasswordService;

class PasswordServiceTest {

	private PasswordService passwordService;
	private PasswordEncoder passwordEncoder;
	
    @Mock
    private EmailService emailService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CipherService cipherService;
	@Captor
	private ArgumentCaptor<User> argumentCaptorUser;
    

	@BeforeEach
	void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        passwordService = new PasswordService("host_frontend", 8, emailService, messageSource, userRepository, cipherService, passwordEncoder);
	}

	@Test
	void testCreatePassword() {
		assertEquals(8, passwordService.createPassword().length());
	}

	@Test
	void testResetPassword() throws Exception {
        User user = new User();
        user.setLanguage("pl");
        passwordService.resetPassword(user);
        verify(userRepository).save(argumentCaptorUser.capture());
        assertEquals(60, argumentCaptorUser.getValue().getPassword().length());
	}

	@Test
	void testRecoverPassword() throws Exception {
        User user = new User();
        user.setId(11L);
        user.setUsername("username");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        when(userRepository.findByUsername("login")).thenReturn(user);
        
        user.setEnabled(false);
        assertFalse(passwordService.recoverPassword("login", "email"));
        
        user.setEnabled(true);
        user.setEmail("emailXXX");
        assertFalse(passwordService.recoverPassword("login", "email"));
        
        user.setLanguage("pl");
        user.setEmail("email");
        assertTrue(passwordService.recoverPassword("login", "email"));
	}

}
