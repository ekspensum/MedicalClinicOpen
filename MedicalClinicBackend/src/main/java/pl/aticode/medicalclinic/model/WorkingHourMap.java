package pl.aticode.medicalclinic.model;

import java.io.Serializable;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

/**
 * Model using as field in WorkingDayMap model.
 * @author aticode.pl
 *
 */
@Getter @Setter
public class WorkingHourMap implements Serializable {

	private static final long serialVersionUID = 1L;

	private LocalTime workingTime;
	private boolean working;
	private boolean disabled;

	
	public WorkingHourMap() {
	}
	
	public WorkingHourMap(LocalTime workingTime, boolean working, boolean disabled) {
		this.workingTime = workingTime;
		this.working = working;
		this.disabled = disabled;
	}
	
}
