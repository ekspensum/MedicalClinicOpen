package pl.aticode.medicalclinic.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import pl.aticode.medicalclinic.entity.user.Employee;
import pl.aticode.medicalclinic.entity.user.User;

public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {

	Employee findByUser(User user);
	
}
