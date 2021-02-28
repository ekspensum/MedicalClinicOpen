package pl.aticode.medicalclinic.dao.visit;

import org.springframework.data.repository.PagingAndSortingRepository;

import pl.aticode.medicalclinic.entity.visit.Visit;

public interface VisitRepository extends PagingAndSortingRepository<Visit, Long> {

	
}
