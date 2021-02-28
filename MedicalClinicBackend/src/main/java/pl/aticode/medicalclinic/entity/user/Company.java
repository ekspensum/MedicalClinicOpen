package pl.aticode.medicalclinic.entity.user;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.pl.NIP;
import org.hibernate.validator.constraints.pl.REGON;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company")
@Getter @Setter
public class Company implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{2,75}$")
    private String companyName;

    @REGON
    private String regon;
    
    @NIP
    private String nip;

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
    @DecimalMin(value = "-90.000000")
    @DecimalMax(value = "90.0000000000")
    @Digits(integer=2, fraction=10)
    private BigDecimal latitude;
    
    @NotNull
    @DecimalMin(value = "-180.000000")
    @DecimalMax(value = "180.0000000000")
    @Digits(integer=3, fraction=10)
    private BigDecimal longitude;
    
    @Email
    @NotEmpty
    private String email;

    @NotNull
    @Pattern(regexp = "^[0-9-+()]{9,20}$")
    private String phone1;
    
    @Pattern(regexp = "^$|[0-9-+()]{9,20}$")
    private String phone2;
}
