package pl.aticode.medicalclinic.entity.user;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employee")
@Getter @Setter
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^$|^[0-9-+()]{9,20}$")
    private String phone;

    @Pattern(regexp = "^$|^[0-9]{11}$")
    private String pesel;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    private LocalDateTime registerDateTime;
    private LocalDateTime editDateTime;

    @JsonIgnore
    @OneToOne
    private User userRegister;

    @JsonIgnore
    @OneToOne
    private User userEdit;


}
