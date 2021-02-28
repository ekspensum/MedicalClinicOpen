package pl.aticode.medicalclinic.dao.user;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import pl.aticode.medicalclinic.entity.user.MedicalByteFile;

public interface MedicalByteFileRepository extends CrudRepository<MedicalByteFile, Long> {

	Optional<MedicalByteFile> findByMedicalDocumentId(Long medicalDocumentId);
	void deleteByMedicalDocumentId(Long medicalDocumentId);
}
