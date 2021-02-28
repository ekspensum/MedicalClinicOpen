package pl.aticode.medicalclinic.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import pl.aticode.medicalclinic.entity.visit.VisitStatus;

/**
 * Model using to receive visit data from frontend. 
 * @author aticode.pl
 *
 */
@Getter @Setter
public class VisitData {

	private LocalDate visitDate;
	private LocalTime visitTime;
	private String doctorUsername;
	private String patientUsername;
	private String employeeUsername;
	private String patientAilmentsInfo;
	private LocalDate visitDateFrom;
	private LocalDate visitDateTo;
	private VisitStatus visitStatus;
}
