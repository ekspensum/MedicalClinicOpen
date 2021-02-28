package pl.aticode.medicalclinic.dao.user;

import org.springframework.data.repository.CrudRepository;

import pl.aticode.medicalclinic.entity.user.WorkingWeek;

public interface WorkingWeekRepository extends CrudRepository<WorkingWeek, Long> {

}
