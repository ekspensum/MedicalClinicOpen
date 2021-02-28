package pl.aticode.medicalclinic.dao.user;

import org.springframework.data.repository.CrudRepository;

import pl.aticode.medicalclinic.entity.user.MedicalDocument;

public interface MedicalDocumentRepository extends CrudRepository<MedicalDocument, Long> {

}
