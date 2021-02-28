package pl.aticode.medicalclinic.entity.user;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "users")
@Getter @Setter
@Indexed
@DynamicUpdate
@JsonFilter("userFilter")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Pattern(regexp="^[a-zA-Z0-9_@.-]{3,40}$")
	@Field(analyzer = @Analyzer(impl = WhitespaceAnalyzer.class))
	private String username;
	
	@NotNull
	@JsonIgnore
	private String password;
    
	@Field
	private boolean enabled;
	
	@Size(min = 4, max = 25)
	@Transient
	private String passwordField;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Role> roles;

    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{2,25}$")
    private String firstName;

    @NotNull
    @Pattern(regexp = "^[^~`!#$%^*+={[}]|:;\"'<>]{2,25}$")
    @Field
    private String lastName;

    @Email
    @NotEmpty
    @Field
    private String email;
    
    @NotEmpty
    private String language;

    @Size(min = 0, max = 40000)
    private byte[] photo;
    
    @Transient
    private String photoString;
}
