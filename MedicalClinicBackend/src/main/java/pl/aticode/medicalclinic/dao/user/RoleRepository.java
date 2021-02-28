package pl.aticode.medicalclinic.dao.user;

import org.springframework.data.repository.CrudRepository;

import pl.aticode.medicalclinic.entity.user.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

	
}
