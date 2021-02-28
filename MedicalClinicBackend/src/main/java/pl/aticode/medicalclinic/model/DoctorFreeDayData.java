package pl.aticode.medicalclinic.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * Model using to receive doctor free day data from frontend. 
 * @author aticode.pl
 *
 */
@Getter @Setter
public class DoctorFreeDayData {

	private LocalDate doctorFreeDay;
	private String doctorUsername;
}
