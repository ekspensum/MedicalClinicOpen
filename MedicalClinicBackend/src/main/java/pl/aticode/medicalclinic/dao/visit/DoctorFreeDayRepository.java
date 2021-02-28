package pl.aticode.medicalclinic.dao.visit;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.visit.DoctorFreeDay;

public interface DoctorFreeDayRepository extends PagingAndSortingRepository<DoctorFreeDay, Long> {

	List<DoctorFreeDay> findByDoctorAndFreeDayBetween(Doctor doctor, LocalDate freeDayFrom, LocalDate freeDayTo);
	List<DoctorFreeDay> findByDoctorAndFreeDay(Doctor doctor, LocalDate freeDayFrom);
	
	@Query("Select doctorFreeDay from DoctorFreeDay doctorFreeDay where doctorFreeDay.doctor.user.username = ?1")
	List<DoctorFreeDay> findByDoctorUsername(String username);
}
