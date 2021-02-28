package pl.aticode.medicalclinic.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.User;

public interface DoctorRepository extends PagingAndSortingRepository<Doctor, Long> {
	
	Doctor findByUser(User user);
	Doctor findByUserUsername(String username);
}
