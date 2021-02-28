package pl.aticode.medicalclinic.entity.user;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.search.annotations.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doctor")
@Getter @Setter
@JsonFilter("doctorFilter")
public class Doctor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Pattern(regexp="^[^~`#$%^*+={[}]|\\\"'<>]{2,512}$")
	@Column(length = 512)
	private String experience;
	
	@Pattern(regexp = "^$|^[0-9]{11}$")
    private String pesel;

    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{2,25}$")
    private String country;

    @NotNull
    @Pattern(regexp = "^[0-9]{2}-[0-9]{3}$")
    private String zipCode;

    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{2,25}$")
    private String city;

    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{2,25}$")
    private String street;

    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{1,10}$")
    private String streetNo;

    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{0,10}$")
    private String unitNo;
    
    @NotNull
    @Pattern(regexp = "^[0-9-+()]{9,20}$")
    private String phone;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @IndexedEmbedded
    private User user;
	
	@OneToOne(cascade = CascadeType.ALL)
	private WorkingWeek workingWeek;

    private LocalDateTime registerDateTime;
    private LocalDateTime editDateTime;

    @JsonIgnore
    @OneToOne
    private User userRegister;

    @JsonIgnore
    @OneToOne
    private User userEdit;
	
}
