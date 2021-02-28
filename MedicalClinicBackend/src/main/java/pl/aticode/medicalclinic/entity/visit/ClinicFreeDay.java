package pl.aticode.medicalclinic.entity.visit;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import pl.aticode.medicalclinic.entity.user.User;

@Entity
@Table(name = "clinic_free_day")
@Getter @Setter
public class ClinicFreeDay implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private LocalDate freeDay;
	
	@JsonIgnore
    @OneToOne
    private User userRegister;
}
