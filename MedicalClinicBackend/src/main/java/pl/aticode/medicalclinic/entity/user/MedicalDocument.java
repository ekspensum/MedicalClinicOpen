package pl.aticode.medicalclinic.entity.user;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "medical_document")
@Getter @Setter
public class MedicalDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private boolean enabled;
    
    @Transient
    private MedicalByteFile medicalByteFile;
    
	@Pattern(regexp="^[^~`#$%^*+={[}]|\\\"'<>]{0,512}$")
	@Column(length = 512)
    private String description;
    
    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{2,75}$")
    private String fileName;
    
    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{2,75}$")
    private String fileType;
    
    private int fileSize;
    private LocalDateTime registerDateTime;
    
    @ManyToOne
    @JsonIgnore
    private Patient patient;
}
