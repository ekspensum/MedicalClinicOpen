package pl.aticode.medicalclinic.dao.user;

import org.springframework.data.repository.CrudRepository;

import pl.aticode.medicalclinic.entity.user.Owner;
import pl.aticode.medicalclinic.entity.user.User;

public interface OwnerRepository extends CrudRepository<Owner, Long> {

	Owner findByUser(User user);
	
}
