package pl.aticode.medicalclinic.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Model using at transform working week byte (saved in database) to working day map list (working week) and revert.   
 * @author aticode.pl
 *
 */
@Getter @Setter
public class WorkingDayMap implements Serializable {

	private static final long serialVersionUID = 1L;

	private DayOfWeek dayOfWeek;
	private boolean working;
	private List<WorkingHourMap> workingHourMapList;
	private LocalDate visitDate;
	private boolean freeDay;
	
	
	public WorkingDayMap() {
	}

	public WorkingDayMap(DayOfWeek dayOfWeek, boolean working, List<WorkingHourMap> workingHourMapList) {
		this.dayOfWeek = dayOfWeek;
		this.working = working;
		this.workingHourMapList = workingHourMapList;
	}
	
}
