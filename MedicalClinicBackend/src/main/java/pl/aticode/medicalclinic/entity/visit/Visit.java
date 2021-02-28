package pl.aticode.medicalclinic.entity.visit;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Getter;
import lombok.Setter;
import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.Employee;
import pl.aticode.medicalclinic.entity.user.Patient;

@Entity
@Table(name = "visit")
@Getter @Setter
@Indexed
@JsonFilter("visitFilter")
public class Visit implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Field
	private LocalDateTime visitDateTime;
	
	@Enumerated(EnumType.STRING)
	@Field
	private VisitStatus visitStatus;
	
	@OneToOne
	@IndexedEmbedded
	private Patient patient;
	
	@OneToOne
	@IndexedEmbedded(includeEmbeddedObjectId = true)
	private Doctor doctor;
	
	@OneToOne
	private Employee employee;
	
	@Pattern(regexp="^[^~`#$%^*+={[}]|\\\"'<>]{2,512}$")
	@Column(length = 512)
	private String patientAilmentsInfo;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Diagnosis diagnosis;
	
	private LocalDateTime reservationDateTime;
}
