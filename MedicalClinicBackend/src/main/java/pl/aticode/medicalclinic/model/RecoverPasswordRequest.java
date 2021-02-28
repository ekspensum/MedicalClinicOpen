package pl.aticode.medicalclinic.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

/**
 * Model using to receive user data from frontend in order to recover password.
 * @author aticode.pl
 *
 */
@Getter @Setter
public class RecoverPasswordRequest {

	
	@NotNull
	@Pattern(regexp="^[a-zA-Z0-9_@.-]{3,40}$")
	private String login;
	
    @Email
    @NotEmpty
	private String email;
	
	
	public RecoverPasswordRequest() {
	}
	
	public RecoverPasswordRequest(String login, String email) {
		this.login = login;
		this.email = email;
	}
	
}
