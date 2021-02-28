package pl.aticode.medicalclinic.entity.visit;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "referral")
@Getter @Setter
public class Referral {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull
	@Pattern(regexp="^[^~`#$%^*+={[}]|\\\"'<>]{2,512}$")
	@Column(length = 512)
	private String recognition;
	
	@NotNull
	@Pattern(regexp="^[^~`#$%^*+={[}]|\\\"'<>]{2,512}$")
	@Column(length = 512)
	private String scopeOfExam;
	
	@NotNull
	@Pattern(regexp="^[^~`#$%^*+={[}]|\\\"'<>]{2,512}$")
	@Column(length = 512)
	private String researchUnit;
	
	private LocalDateTime registerDateTime;
	
	@ManyToOne
	@JsonIgnore
	private Diagnosis diagnosis;
}
