package pl.aticode.medicalclinic.unit.security;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.aticode.medicalclinic.dao.user.PatientRepository;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.model.SocialUser;
import pl.aticode.medicalclinic.security.AuthenticationController;
import pl.aticode.medicalclinic.security.AuthenticationRequest;
import pl.aticode.medicalclinic.security.AuthenticationResponse;
import pl.aticode.medicalclinic.security.JwtService;
import pl.aticode.medicalclinic.security.JwtUserDetailsService;

class AuthenticationControllerTest {
	
	private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private AuthenticationRequest authenticationRequest;
    private String jsonAuthenticationRequest;
    private AuthenticationResponse authenticationResponse;
    private String jsonAuthenticationResponse;
    private String token;
    private String googleTokenVerify;
    private String facebookTokenVerify;
    
    @InjectMocks
    private AuthenticationController authenticationController;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private JwtService jwtService;
	@Mock
	private JwtUserDetailsService jwtUserDetailsService;
	@Mock
	private PatientRepository patientRepository;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private UserDetails userDetails;

	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setSocialUserId("000000000000");
        jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        token = "JsonWebToken";
        authenticationResponse = new AuthenticationResponse(token, true);
        jsonAuthenticationResponse = objectMapper.writeValueAsString(authenticationResponse);
        when(jwtService.generateToken(userDetails)).thenReturn(token);
        googleTokenVerify = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=";
        facebookTokenVerify = "https://graph.facebook.com/me?access_token=";
	}

	@Test
	void testCreateAuthenticationTokenForRegisteredUser() throws Exception {
		when(jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername())).thenReturn(userDetails);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateRegisterdUser").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonAuthenticationResponse));
	}

	@Test
	void testCreateAuthenticationTokenForSocialUser() throws Exception {
		User user = new User();
		user.setUsername("username");
		user.setPassword("password");
		Patient patient = new Patient();
		patient.setUser(user);
		Optional<Patient> optionalPatient = Optional.of(patient);
		when(patientRepository.findBySocialUserId(authenticationRequest.getSocialUserId())).thenReturn(optionalPatient);
		SocialUser socialUser = new SocialUser();
		socialUser.setSub("000000000000");
		socialUser.setId("000000000000");
		ResponseEntity<SocialUser> googleUserResponse = ResponseEntity.ok(socialUser);
		when(restTemplate.getForEntity(googleTokenVerify+authenticationRequest.getSocialUserToken(), SocialUser.class)).thenReturn(googleUserResponse);
		
		authenticationRequest.setProvider("GOOGLE");
		jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
		when(jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername())).thenReturn(userDetails);
		
		List<GrantedAuthority> roles = new ArrayList<>();
		roles.add(new SimpleGrantedAuthority("ROLE_PATIENT"));
		org.springframework.security.core.userdetails.User userSecurity = 
				new org.springframework.security.core.userdetails.User(patient.getUser().getUsername(), patient.getUser().getPassword(), roles);
		when(jwtService.generateToken(userSecurity)).thenReturn(token);
		
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateSocialUser").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonAuthenticationResponse));
        
        ResponseEntity<SocialUser> facebookUserResponse = ResponseEntity.ok(socialUser);
		when(restTemplate.getForEntity(facebookTokenVerify+authenticationRequest.getSocialUserToken(), SocialUser.class)).thenReturn(facebookUserResponse);
		authenticationRequest.setProvider("FACEBOOK");
		jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
		when(jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername())).thenReturn(userDetails);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateSocialUser").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonAuthenticationResponse));
        
        optionalPatient = Optional.ofNullable(null);
        when(patientRepository.findBySocialUserId(authenticationRequest.getSocialUserId())).thenReturn(optionalPatient);
        authenticationResponse = new AuthenticationResponse(false);
        jsonAuthenticationResponse = objectMapper.writeValueAsString(authenticationResponse);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateSocialUser").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonAuthenticationResponse));
        
		authenticationRequest.setProvider("GOOGLE");
		jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateSocialUser").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(jsonAuthenticationResponse));
        
		authenticationRequest.setProvider("GOOGLEXXXXXXXXXXXXX");
		jsonAuthenticationRequest = objectMapper.writeValueAsString(authenticationRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticateSocialUser").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonAuthenticationRequest))
        .andExpect(MockMvcResultMatchers.status().isForbidden());        
	}

}
