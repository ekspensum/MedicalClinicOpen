package pl.aticode.medicalclinic.model;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

/**
 * Model using to receive email message data from frontend in order to send email by contact us page with attachments.
 * @author aticode.pl
 *
 */
@Getter @Setter
public class ContactUs {

	@NotNull
	@Pattern(regexp = "^[^~`#$%^*+={[}]|\"'<>]{2,75}$")
	private String subject;
	
	@NotNull
	@Pattern(regexp = "^[^~`#$%^*+={[}]|\"'<>]{2,512}$")
	private String message;
	
	@Email
	@NotEmpty
	private String replyEmail;
	
	private List<FileAttachment> attachmentList;
	
}
