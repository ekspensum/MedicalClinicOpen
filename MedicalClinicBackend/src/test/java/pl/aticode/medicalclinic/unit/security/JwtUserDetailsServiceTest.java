package pl.aticode.medicalclinic.unit.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.Role;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.security.JwtUserDetailsService;
import pl.aticode.medicalclinic.service.HibernateSearchService;

class JwtUserDetailsServiceTest {

	@InjectMocks
	private JwtUserDetailsService jwtUserDetailsService;
	@Mock
	private HibernateSearchService hibernateSearchService;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testLoadUserByUsername() {
		Role role1 = new Role();
		role1.setId(1L);
		List<Role> roleList = new ArrayList<>();
		User user = new User();
		user.setRoles(roleList);
		user.setUsername("username");
		user.setPassword("password");
		Patient patient = new Patient();
		patient.setUser(user);
		when(hibernateSearchService.searchUserByUsernameForAuthenticate(user.getUsername())).thenReturn(user);
		assertEquals(user.getPassword(), jwtUserDetailsService.loadUserByUsername(user.getUsername()).getPassword());
	}

}
