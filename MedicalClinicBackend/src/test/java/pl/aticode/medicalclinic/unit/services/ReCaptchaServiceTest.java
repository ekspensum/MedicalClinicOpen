package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.aticode.medicalclinic.service.ReCaptchaService;


class ReCaptchaServiceTest {

	private ReCaptchaService reCaptchaService;
	
	@BeforeEach
	void setUp() throws Exception {
		reCaptchaService = new ReCaptchaService();
	}

	@Test
	void testVerify() throws IOException {
		assertFalse(reCaptchaService.verify("reCaptchaResponse"));
		assertFalse(reCaptchaService.verify(""));
	}

}
