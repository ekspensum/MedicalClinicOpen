package pl.aticode.medicalclinic.entity.visit;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "diagnosis")
@Getter @Setter
public class Diagnosis {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull
	@Pattern(regexp="^[^~`#$%^*+={[}]|\\\"'<>]{2,512}$")
	@Column(length = 512)
	private String ailments;
	
	@NotNull
	@Pattern(regexp="^[^~`#$%^*+={[}]|\\\"'<>]{5,1024}$")
	@Column(length = 1024)
	private String orders;
	
	@Pattern(regexp="^[^~`#$%^*+={[}]|\\\"'<>]{2,512}$")
	@Column(length = 512)
	private String medicines;
	
	private LocalDateTime registerDateTime;
	private LocalDateTime updateDateTime;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "diagnosis")
	private List<Referral> referralList;
}
