package pl.aticode.medicalclinic.dao.user;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;

public interface PatientRepository extends PagingAndSortingRepository<Patient, Long> {

	Patient findByUser(User user);
	Optional<Patient> findBySocialUserId(String socialUserId);
	
}
