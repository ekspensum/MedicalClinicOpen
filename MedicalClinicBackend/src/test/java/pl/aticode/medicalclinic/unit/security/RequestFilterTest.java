package pl.aticode.medicalclinic.unit.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import pl.aticode.medicalclinic.security.JwtService;
import pl.aticode.medicalclinic.security.JwtUserDetailsService;
import pl.aticode.medicalclinic.security.RequestFilter;

class RequestFilterTest {
	
	@InjectMocks
	private RequestFilter requestFilter;
	@Mock
	private JwtService jwtService;
	@Mock
	private JwtUserDetailsService jwtUserDeatailsService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain filterChain;
	@Mock
	private UserDetails userDetails;
	@Captor
	private ArgumentCaptor<String> argumentCaptorToken;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testDoFilterInternalHttpServletRequestHttpServletResponseFilterChain() throws ServletException, IOException {
		String requestTokenHeader = "Bearer requestToken";
		String token = "requestToken";
		String username = "userName";
		when(request.getHeader("Authorization")).thenReturn(requestTokenHeader);
		
		requestFilter.doFilter(request, response, filterChain);
		verify(jwtService).getUsername(argumentCaptorToken.capture());
		assertEquals(token, argumentCaptorToken.getValue());
		
		when(jwtService.getUsername(token)).thenReturn(username);
		when(jwtUserDeatailsService.loadUserByUsername(username)).thenReturn(userDetails);
		when(jwtService.validateToken(token, userDetails)).thenReturn(true);
		requestFilter.doFilter(request, response, filterChain);
	}

}
