package pl.aticode.medicalclinic.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Model using to receive user data from social media.
 * @author aticode.pl
 *
 */
@Getter @Setter
public class SocialUser {

//	Google:
	private String sub;
	private String exp;
	private String expires_in;
	private String email;
	
//	Facebook:
	private String id;
	private String name;
}
