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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "owner")
@Getter @Setter
public class Owner implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[0-9-+()]{9,20}$")
    private String phone;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
    
    @Valid
    @OneToOne(cascade = CascadeType.ALL)    
    private Company company;
    
    @JsonIgnore
    private LocalDateTime registeredDateTime;
    @JsonIgnore
    private LocalDateTime editDateTime;

}
