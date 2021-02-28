package pl.aticode.medicalclinic.entity.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "patient")
@Getter @Setter
@Indexed
@JsonFilter("patientFilter")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field
    private String socialUserId;

    @NotNull
    @Pattern(regexp = "^[0-9]{11}$")
    @Field
    private String pesel;

    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{1,10}$")
	private String gender;

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
    @Field
    private String street;

    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{1,10}$")
    private String streetNo;

    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{0,10}$")
    private String unitNo;
    
    @NotNull
    @Pattern(regexp = "^[0-9-+()]{9,20}$")
    @Field
    private String phone;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<MedicalDocument> medicalDocumentList;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @IndexedEmbedded
    private User user;

    @Field
    @JsonIgnore
    private String activationString;

    private LocalDateTime registerDateTime;
    private LocalDateTime editDateTime;

    @JsonIgnore
    @OneToOne
    private User userRegister;

    @JsonIgnore
    @OneToOne
    private User userEdit;

}
