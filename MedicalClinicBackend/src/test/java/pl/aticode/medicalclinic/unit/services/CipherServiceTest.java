package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.aticode.medicalclinic.service.CipherService;


class CipherServiceTest {

	private CipherService cipherService;
	
	@BeforeEach
	void setUp() throws Exception {
		cipherService = new CipherService("encryptionKey");
	}

	@Test
	void testEncodeString() {
		String encodeStringExpected = "xJ4p1s8l1fZK-_rifOUNmg=="; 
		assertEquals(encodeStringExpected, cipherService.encodeString("stringToken123"));
	}

	@Test
	void testDecodeString() {
		String tokenToDecode = "xJ4p1s8l1fZK-_rifOUNmg==";
		assertEquals("stringToken123", cipherService.decodeString(tokenToDecode));
	}

}
