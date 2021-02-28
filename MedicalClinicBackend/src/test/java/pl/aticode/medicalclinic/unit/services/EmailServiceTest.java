package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import pl.aticode.medicalclinic.model.FileAttachment;
import pl.aticode.medicalclinic.service.EmailService;

class EmailServiceTest {
	
	private EmailService emailService;
	@Mock
	private JavaMailSender javaMailSender;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		emailService = new EmailService("mailFrom", 2, 5, javaMailSender);
	}

	@Test
	void testSendEmailFileAttachment() throws Exception {
		List<FileAttachment> attachmentList = new ArrayList<>();
		emailService.sendEmailFileAttachment("mailTo", "emailSubject", "emailContent", attachmentList);
		attachmentList.add(new FileAttachment());
		attachmentList.add(new FileAttachment());
		attachmentList.add(new FileAttachment());
		assertThrows(Exception.class, ()-> {
			emailService.sendEmailFileAttachment("mailTo", "emailSubject", "emailContent", attachmentList);
		});
	}

	@Test
	void testSendEmail() {
		emailService.sendEmail("mailTo", "emailSubject", "emailContent");
	}

}
