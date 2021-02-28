package pl.aticode.medicalclinic.dao.user;

import org.springframework.data.repository.CrudRepository;

import pl.aticode.medicalclinic.entity.user.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByUsername(String username);
	
}
