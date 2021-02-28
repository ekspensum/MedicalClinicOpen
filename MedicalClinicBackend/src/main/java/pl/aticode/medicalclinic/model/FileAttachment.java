package pl.aticode.medicalclinic.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Model using to receive medical document from frontend.
 * @author aticode.pl
 *
 */
@Getter @Setter
public class FileAttachment {

	  private String fileBase64;
	  private String fileName;
	  private String fileType;
	  private int fileSize;
	  
}
