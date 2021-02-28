package pl.aticode.medicalclinic.dao.visit;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import pl.aticode.medicalclinic.entity.visit.ClinicFreeDay;

public interface ClinicFreeDayRepository extends PagingAndSortingRepository<ClinicFreeDay, Long> {

	List<ClinicFreeDay> findByFreeDayBetween(LocalDate freeDayFrom, LocalDate freeDayTo);
	List<ClinicFreeDay> findByFreeDay(LocalDate freeDay);
	
}
