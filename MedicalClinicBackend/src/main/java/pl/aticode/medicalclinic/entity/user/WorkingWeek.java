package pl.aticode.medicalclinic.entity.user;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.aticode.medicalclinic.model.WorkingDayMap;


@Entity
@Table(name = "working_week")
public class WorkingWeek implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(WorkingWeek.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	private byte [] workingWeekMapByte;
	
	@Transient
	private List<WorkingDayMap> workingWeekMap;
	
    @JsonIgnore
    @OneToOne
    private User userEdit;
    
    private LocalDateTime editDateTime;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getWorkingWeekMapByte() {
		return workingWeekMapByte;
	}
	
	public void setWorkingWeekMapByte(byte[] workingWeekMapByte) {
		this.workingWeekMapByte = workingWeekMapByte;
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkingDayMap> getWorkingWeekMap() {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(this.workingWeekMapByte);
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			workingWeekMap = (List<WorkingDayMap>) objectInputStream.readObject();
			inputStream.close();
		} catch (ClassNotFoundException | IOException e) {
			logger.error(e.getMessage());
		}
		return workingWeekMap;
	}

	public void setWorkingWeekMap(List<WorkingDayMap> workingWeekMap) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() ;
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
			outputStream.writeObject(workingWeekMap);
			outputStream.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		this.workingWeekMapByte = byteArrayOutputStream.toByteArray();
	}

	public User getUserEdit() {
		return userEdit;
	}

	public void setUserEdit(User userEdit) {
		this.userEdit = userEdit;
	}

	public LocalDateTime getEditDateTime() {
		return editDateTime;
	}

	public void setEditDateTime(LocalDateTime editDateTime) {
		this.editDateTime = editDateTime;
	}

	
}
