package pl.aticode.medicalclinic.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;

import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.entity.visit.Visit;
import pl.aticode.medicalclinic.entity.visit.VisitStatus;


@Service
@Transactional
public class HibernateSearchService {

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Method searching for patient by username or last name or company name or
     * regon or street or phone or email.
     *
     * @param text
     * @return list of found patients.
     */
    @SuppressWarnings("unchecked")
    public List<Patient> searchPatientNamePeselStreetPhoneMailByKeywordQuery(String text) {
    	FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Patient.class)
                .get();
        Query luceneQuery = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(2)
                .withPrefixLength(0)
                .onFields("user.username", "user.lastName", "pesel", "street", "phone", "user.email")
                .matching(text)
                .createQuery();
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Patient.class).getResultList();
    }
    
    /**
     * Method searching for active patient by username or last name or company name or regon or street or phone or email.
     * @param text
     * @return active patients list
     */
    @SuppressWarnings("unchecked")
    public List<Patient> searchPatientEnabledNamePeselStreetPhoneByKeywordQuery(String text) {
    	FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Patient.class)
                .get();
        Query luceneQuery = queryBuilder
                .bool()
                .must(queryBuilder
                        .keyword()
                        .fuzzy()
                        .withEditDistanceUpTo(2)
                        .withPrefixLength(0)
                        .onFields("user.username", "user.lastName", "pesel", "street", "phone", "user.email")
                        .matching(text)
                        .createQuery())
                .must(queryBuilder
                        .keyword()
                        .onField("user.enabled")
                        .matching(Boolean.TRUE)
                        .createQuery())
                .createQuery();
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Patient.class).getResultList();
    }    

    /**
     * Method searching patient by activation string during registration process.
     * @param activationString
     * @return patient or null if not found.
     */
    public Patient searchPatientToActivation(String activationString) {
    	FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Patient.class)
                .get();
        Query luceneQuery = queryBuilder
                .keyword()
                .onField("activationString")
                .matching(activationString)
                .createQuery();
		try {
			return (Patient) fullTextEntityManager.createFullTextQuery(luceneQuery, Patient.class).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
    }

    /**
     * Searching patient by pesel number.
     * @param pesel
     * @return patient or null if not found
     */
    public Patient searchPatientPeselByKeywordQuery(String pesel) {
    	FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Patient.class)
                .get();
        Query luceneQuery = queryBuilder
                .keyword()
                .onField("pesel")
                .matching(pesel)
                .createQuery();
        try {
        	return (Patient) fullTextEntityManager.createFullTextQuery(luceneQuery, Patient.class).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
    }
    
    /**
     * Searching visits by doctor and visit date time range.
     * @param doctor
     * @param dateTimeFrom
     * @param dateTimeTo
     * @return visits list
     */
    @SuppressWarnings("unchecked")
    public List<Visit> searchVisitByDoctorVisitDateTimeRange(Doctor doctor, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo){
    	FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Visit.class)
                .get();
        
        Query luceneQuery = queryBuilder
                .bool()
                .must(queryBuilder
                        .keyword()
                        .onField("doctor.id").matching(doctor.getId())
                        .createQuery())
                .must(queryBuilder
                        .range()
                        .onField("visitDateTime").from(dateTimeFrom).to(dateTimeTo)
                        .createQuery())
                .createQuery();
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Visit.class).getResultList();
    }
    
    /**
     * Searching visit by visit local date time exactly.
     * @param localDateTime
     * @return visit or null if not found
     */
    public Visit searchVisitByVisitDateTimeKeywordQuery(LocalDateTime localDateTime){
    	FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Visit.class)
                .get();
        
        Query luceneQuery = queryBuilder
                .range()
                .onField("visitDateTime").from(localDateTime).to(localDateTime)
                .createQuery();
        try {
			return (Visit) fullTextEntityManager.createFullTextQuery(luceneQuery, Visit.class).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
    }
	
    /**
     * Searching visits by patient and visit date time range.
     * @param patientUsername
     * @param dateTimeFrom
     * @param dateTimeTo
     * @return visits list
     */
	@SuppressWarnings("unchecked")
    public List<Visit> searchVisitByPatientVisitDateTimeRange(String patientUsername, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo){
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Visit.class)
                .get();
        
        Query luceneQuery = queryBuilder
                .bool()
                .must(queryBuilder
                        .keyword()
                        .onField("patient.user.username").matching(patientUsername)
                        .createQuery())
                .must(queryBuilder
                        .range()
                        .onField("visitDateTime").from(dateTimeFrom).to(dateTimeTo)
                        .createQuery())
                .createQuery();
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Visit.class).getResultList();
    }
	
	/**
	 * Searching visits by visit status as planned and visit date time range.
	 * @param dateTimeFrom
	 * @param dateTimeTo
	 * @return visits list
	 */
	@SuppressWarnings("unchecked")
    public List<Visit> searchVisitByVisitStatusPlannedAndDateTimeRange(LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo){
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Visit.class)
                .get();
        
        Query luceneQuery = queryBuilder
                .bool()
                .must(queryBuilder
                        .keyword()
                        .onField("visitStatus").matching(VisitStatus.PLANNED)
                        .createQuery())
                .must(queryBuilder
                        .range()
                        .onField("visitDateTime").from(dateTimeFrom).to(dateTimeTo)
                        .createQuery())
                .createQuery();
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Visit.class).getResultList();
    }
	
	/**
	 * Searching visit by doctor and visit date time range and visit status as not completed (planned or confirmed).
	 * @param doctorUsername
	 * @param dateTimeFrom
	 * @param dateTimeTo
	 * @return visits list
	 */
	@SuppressWarnings("unchecked")
    public List<Visit> searchVisitByDoctorVisitDateTimeRangeAndPlanned(String doctorUsername, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo){
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Visit.class)
                .get();
        
        Query luceneQuery = queryBuilder
                .bool()
                .must(queryBuilder
                        .keyword()
                        .onField("doctor.user.username").matching(doctorUsername)
                        .createQuery())
                .must(queryBuilder
                        .keyword()
                        .onField("visitStatus").matching(VisitStatus.COMPLETED)
                        .createQuery()).not()
                .must(queryBuilder
                        .range()
                        .onField("visitDateTime").from(dateTimeFrom).to(dateTimeTo)
                        .createQuery())
                .createQuery();
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Visit.class).getResultList();
    }
	
	/**
	 * Searching visit by doctor and visit date time range and visit status.
	 * @param doctorUsername
	 * @param dateTimeFrom
	 * @param dateTimeTo
	 * @param visitStatus
	 * @return visits list
	 */
	@SuppressWarnings("unchecked")
    public List<Visit> searchVisitByDoctorVisitDateTimeRangeAndStatus(String doctorUsername, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, VisitStatus visitStatus){
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Visit.class)
                .get();
        
        Query luceneQuery = queryBuilder
                .bool()
                .must(queryBuilder
                        .keyword()
                        .onField("doctor.user.username").matching(doctorUsername)
                        .createQuery())
                .must(queryBuilder
                        .keyword()
                        .onField("visitStatus").matching(visitStatus)
                        .createQuery())
                .must(queryBuilder
                        .range()
                        .onField("visitDateTime").from(dateTimeFrom).to(dateTimeTo)
                        .createQuery())
                .createQuery();
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Visit.class).getResultList();
    }
	
	/**
	 * Searching user by user name for authenticate.
	 * @param username
	 * @return user or null if not found
	 */
    public User searchUserByUsernameForAuthenticate(String username){
    	FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(User.class)
                .get();
        
        Query luceneQuery = queryBuilder
                .bool()
                .must(queryBuilder
                        .keyword()
                        .onField("username").matching(username)
                        .createQuery())
                .must(queryBuilder
                        .keyword()
                        .onField("enabled").matching(Boolean.TRUE)
                        .createQuery())
                .createQuery();
		try {
			return (User) fullTextEntityManager.createFullTextQuery(luceneQuery, User.class).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
    }
    
    /**
     * Searching visits by patient user name for select to cancel (remove) visit. 
     * @param patientUsername
     * @return visits list
     */
	@SuppressWarnings("unchecked")
    public List<Visit> searchVisitToRemoveByPatientUsername(String patientUsername){
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Visit.class)
                .get();
        
        Query luceneQuery = queryBuilder
                .keyword()
                .onField("patient.user.username").matching(patientUsername)
                .createQuery();
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Visit.class).getResultList();
    }
}
