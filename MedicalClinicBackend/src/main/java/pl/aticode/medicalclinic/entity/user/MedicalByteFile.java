package pl.aticode.medicalclinic.entity.user;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "medical_byte_file")
@Getter @Setter
public class MedicalByteFile implements Serializable {
    
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private byte [] file;
    
    @Transient
    private String fileBase64;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MedicalDocument medicalDocument;
}
