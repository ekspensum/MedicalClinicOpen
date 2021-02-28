package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.EntityContext;
import org.hibernate.search.query.dsl.FuzzyContext;
import org.hibernate.search.query.dsl.MustJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.query.dsl.RangeContext;
import org.hibernate.search.query.dsl.RangeMatchingContext;
import org.hibernate.search.query.dsl.RangeMatchingContext.FromRangeContext;
import org.hibernate.search.query.dsl.RangeTerminationExcludable;
import org.hibernate.search.query.dsl.TermContext;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.TermTermination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.entity.visit.Visit;
import pl.aticode.medicalclinic.entity.visit.VisitStatus;
import pl.aticode.medicalclinic.service.HibernateSearchService;

class HibernateSearchServiceTest {
	
	LocalDateTime localDateTimeNow;
	
	@InjectMocks
	private HibernateSearchService hibernateSearchService;
	@Mock
	private EntityManager entityManager;
	@Mock
	private FullTextSession fullTextSession;
	@Mock
	private FuzzyContext fuzzyContext;
	@Mock
	private SearchFactory searchFactory;
	@Mock
	private QueryContextBuilder queryContextBuilder;
	@Mock
	private EntityContext entityContext;
	@Mock
	private QueryBuilder queryBuilder;
	@Mock
	private TermContext termContext;
	@Mock
	private TermMatchingContext termMatchingContext;
	@Mock
	private TermTermination termTermination;
	@Mock
	private RangeContext rangeContext;
	@Mock
	private RangeMatchingContext rangeMatchingContext;
	@Mock
	private FromRangeContext<LocalDateTime> fromRangeContext;
	@Mock
	private RangeTerminationExcludable rangeTerminationExcludable;
	@Mock
	private Query luceneQuery;
	@Mock
	private FullTextQuery fullTextQuery;
	@SuppressWarnings("rawtypes")
	@Mock
	private BooleanJunction<BooleanJunction> booleanJunction;
	@Mock
	private MustJunction mustJunction;


	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(entityManager.unwrap(Session.class)).thenReturn(fullTextSession);
		when(fullTextSession.getSearchFactory()).thenReturn(searchFactory);
		when(searchFactory.buildQueryBuilder()).thenReturn(queryContextBuilder);
		localDateTimeNow = LocalDateTime.now();
	}

	@Test
	void testSearchPatientNamePeselStreetPhoneMailByKeywordQuery() {
		when(queryContextBuilder.forEntity(Patient.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);
		
		when(queryBuilder.keyword()).thenReturn(termContext);
		when(termContext.fuzzy()).thenReturn(fuzzyContext);
		when(fuzzyContext.withEditDistanceUpTo(2)).thenReturn(fuzzyContext);
		when(fuzzyContext.withPrefixLength(0)).thenReturn(fuzzyContext);
		when(fuzzyContext.onFields("user.username", "user.lastName", "pesel", "street", "phone", "user.email")).thenReturn(termMatchingContext);
		String text = "searchingText";
		when(termMatchingContext.matching(text)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Patient.class)).thenReturn(fullTextQuery);
		List<Patient> patientList = new ArrayList<>();
		when(fullTextQuery.getResultList()).thenReturn(patientList);
		
		assertNotNull(hibernateSearchService.searchPatientNamePeselStreetPhoneMailByKeywordQuery(text));
	}

	@Test
	void testSearchPatientEnabledNamePeselStreetPhoneByKeywordQuery() {
		when(queryContextBuilder.forEntity(Patient.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);
		
		when(queryBuilder.bool()).thenReturn(booleanJunction);
		when(queryBuilder.keyword()).thenReturn(termContext);
		
		when(termContext.fuzzy()).thenReturn(fuzzyContext);
		when(fuzzyContext.withEditDistanceUpTo(2)).thenReturn(fuzzyContext);
		when(fuzzyContext.withPrefixLength(0)).thenReturn(fuzzyContext);
		when(fuzzyContext.onFields("user.username", "user.lastName", "pesel", "street", "phone", "user.email")).thenReturn(termMatchingContext);
		String text = "searchingText";
		when(termMatchingContext.matching(text)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery)).thenReturn(mustJunction);
		
		when(termContext.onField("user.enabled")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(Boolean.TRUE)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery).createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Patient.class)).thenReturn(fullTextQuery);
		List<Patient> patientList = new ArrayList<>();
		when(fullTextQuery.getResultList()).thenReturn(patientList);
		
		assertNotNull(hibernateSearchService.searchPatientEnabledNamePeselStreetPhoneByKeywordQuery(text));
	}

	@Test
	void testSearchPatientToActivation() {
		String activationString = "encodeActivationStringBase64";
		
		when(queryContextBuilder.forEntity(Patient.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);
		
		when(queryBuilder.keyword()).thenReturn(termContext);
		when(termContext.onField("activationString")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(activationString)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Patient.class)).thenReturn(fullTextQuery);
		Patient patient = new Patient();
		when(fullTextQuery.getSingleResult()).thenReturn(patient);
		
		assertNotNull(hibernateSearchService.searchPatientToActivation(activationString));
	}

	@Test
	void testSearchPatientPeselByKeywordQuery() {
		String peselNo = "peselNo";
		
		when(queryContextBuilder.forEntity(Patient.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);
		
		when(queryBuilder.keyword()).thenReturn(termContext);
		when(termContext.onField("pesel")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(peselNo)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Patient.class)).thenReturn(fullTextQuery);
		Patient patient = new Patient();
		when(fullTextQuery.getSingleResult()).thenReturn(patient);
		
		assertNotNull(hibernateSearchService.searchPatientPeselByKeywordQuery(peselNo));
	}
	
	@Test
	void testSearchVisitByDoctorVisitDateTimeRange() {
		Doctor doctor = new Doctor();
		doctor.setId(33L);
		
		when(queryContextBuilder.forEntity(Visit.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);

		when(queryBuilder.bool()).thenReturn(booleanJunction);
		when(queryBuilder.keyword()).thenReturn(termContext);
		
		when(termContext.onField("doctor.id")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(doctor.getId())).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.range()).thenReturn(rangeContext);
		when(rangeContext.onField("visitDateTime")).thenReturn(rangeMatchingContext);
		when(rangeMatchingContext.from(localDateTimeNow.minusDays(1))).thenReturn(fromRangeContext);
		when(fromRangeContext.to(localDateTimeNow)).thenReturn(rangeTerminationExcludable);
		when(rangeTerminationExcludable.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery).createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Visit.class)).thenReturn(fullTextQuery);
		List<Visit> visitList = Arrays.asList(new Visit(), new Visit());
		when(fullTextQuery.getResultList()).thenReturn(visitList);
		
		assertEquals(2, hibernateSearchService.searchVisitByDoctorVisitDateTimeRange(doctor, localDateTimeNow.minusDays(1), localDateTimeNow).size());
	}

	@Test
	void testSearchVisitByVisitDateTimeKeywordQuery() {
		when(queryContextBuilder.forEntity(Visit.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);
		
		when(queryBuilder.range()).thenReturn(rangeContext);
		when(rangeContext.onField("visitDateTime")).thenReturn(rangeMatchingContext);
		when(rangeMatchingContext.from(localDateTimeNow)).thenReturn(fromRangeContext);
		when(fromRangeContext.to(localDateTimeNow)).thenReturn(rangeTerminationExcludable);
		when(rangeTerminationExcludable.createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Visit.class)).thenReturn(fullTextQuery);
		when(fullTextQuery.getSingleResult()).thenReturn(new Visit());
		
		assertNotNull(hibernateSearchService.searchVisitByVisitDateTimeKeywordQuery(localDateTimeNow));
	}

	@Test
	void testSearchVisitByPatientVisitDateTimeRange() {
		String patientUsername = "patientUsername";
		when(queryContextBuilder.forEntity(Visit.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);

		when(queryBuilder.bool()).thenReturn(booleanJunction);
		when(queryBuilder.keyword()).thenReturn(termContext);
		
		when(termContext.onField("patient.user.username")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(patientUsername)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.range()).thenReturn(rangeContext);
		when(rangeContext.onField("visitDateTime")).thenReturn(rangeMatchingContext);
		when(rangeMatchingContext.from(localDateTimeNow.minusDays(1))).thenReturn(fromRangeContext);
		when(fromRangeContext.to(localDateTimeNow)).thenReturn(rangeTerminationExcludable);
		when(rangeTerminationExcludable.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery).createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Visit.class)).thenReturn(fullTextQuery);
		List<Visit> visitList = Arrays.asList(new Visit(), new Visit());
		when(fullTextQuery.getResultList()).thenReturn(visitList);
		
		assertEquals(2, hibernateSearchService.searchVisitByPatientVisitDateTimeRange(patientUsername, 
				localDateTimeNow.minusDays(1), localDateTimeNow).size());
	}

	@Test
	void testSearchVisitByVisitStatusPlannedAndDateTimeRange() {
		when(queryContextBuilder.forEntity(Visit.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);

		when(queryBuilder.bool()).thenReturn(booleanJunction);
		when(queryBuilder.keyword()).thenReturn(termContext);
		
		when(termContext.onField("visitStatus")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(VisitStatus.PLANNED)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.range()).thenReturn(rangeContext);
		when(rangeContext.onField("visitDateTime")).thenReturn(rangeMatchingContext);
		when(rangeMatchingContext.from(localDateTimeNow.minusDays(1))).thenReturn(fromRangeContext);
		when(fromRangeContext.to(localDateTimeNow)).thenReturn(rangeTerminationExcludable);
		when(rangeTerminationExcludable.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery).createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Visit.class)).thenReturn(fullTextQuery);
		List<Visit> visitList = Arrays.asList(new Visit(), new Visit());
		when(fullTextQuery.getResultList()).thenReturn(visitList);
		
		assertEquals(2, hibernateSearchService.searchVisitByVisitStatusPlannedAndDateTimeRange(
				localDateTimeNow.minusDays(1), localDateTimeNow).size());
	}

	@Test
	void testSearchVisitByDoctorVisitDateTimeRangeAndPlanned() {
		String doctorUsername = "doctorUsername";
		when(queryContextBuilder.forEntity(Visit.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);

		when(queryBuilder.bool()).thenReturn(booleanJunction);
		when(queryBuilder.keyword()).thenReturn(termContext);
		
		when(termContext.onField("doctor.user.username")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(doctorUsername)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery)).thenReturn(mustJunction);
		
		when(termContext.onField("visitStatus")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(VisitStatus.COMPLETED)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(mustJunction.not()).thenReturn(mustJunction);
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.range()).thenReturn(rangeContext);
		when(rangeContext.onField("visitDateTime")).thenReturn(rangeMatchingContext);
		when(rangeMatchingContext.from(localDateTimeNow.minusDays(1))).thenReturn(fromRangeContext);
		when(fromRangeContext.to(localDateTimeNow)).thenReturn(rangeTerminationExcludable);
		when(rangeTerminationExcludable.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery).must(luceneQuery).createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Visit.class)).thenReturn(fullTextQuery);
		List<Visit> visitList = Arrays.asList(new Visit(), new Visit());
		when(fullTextQuery.getResultList()).thenReturn(visitList);
		
		assertEquals(2, hibernateSearchService.searchVisitByDoctorVisitDateTimeRangeAndPlanned(doctorUsername, 
				localDateTimeNow.minusDays(1), localDateTimeNow).size());
	}

	@Test
	void testSearchVisitByDoctorVisitDateTimeRangeAndStatus() {
		String doctorUsername = "doctorUsername";
		when(queryContextBuilder.forEntity(Visit.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);

		when(queryBuilder.bool()).thenReturn(booleanJunction);
		when(queryBuilder.keyword()).thenReturn(termContext);
		
		when(termContext.onField("doctor.user.username")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(doctorUsername)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery)).thenReturn(mustJunction);
		
		when(termContext.onField("visitStatus")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(VisitStatus.PLANNED)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.range()).thenReturn(rangeContext);
		when(rangeContext.onField("visitDateTime")).thenReturn(rangeMatchingContext);
		when(rangeMatchingContext.from(localDateTimeNow.minusDays(1))).thenReturn(fromRangeContext);
		when(fromRangeContext.to(localDateTimeNow)).thenReturn(rangeTerminationExcludable);
		when(rangeTerminationExcludable.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery).must(luceneQuery).createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Visit.class)).thenReturn(fullTextQuery);
		List<Visit> visitList = Arrays.asList(new Visit(), new Visit(), new Visit());
		when(fullTextQuery.getResultList()).thenReturn(visitList);
		
		assertEquals(3, hibernateSearchService.searchVisitByDoctorVisitDateTimeRangeAndStatus(doctorUsername, 
				localDateTimeNow.minusDays(1), localDateTimeNow, VisitStatus.PLANNED).size());
	}

	@Test
	void testSearchUserByUsernameForAuthenticate() {
		String username = "username";
		when(queryContextBuilder.forEntity(User.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);

		when(queryBuilder.bool()).thenReturn(booleanJunction);
		when(queryBuilder.keyword()).thenReturn(termContext);
		
		when(termContext.onField("username")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(username)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery)).thenReturn(mustJunction);
		
		when(termContext.onField("enabled")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(Boolean.TRUE)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery)).thenReturn(mustJunction);
		
		when(queryBuilder.bool().must(luceneQuery).must(luceneQuery).createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, User.class)).thenReturn(fullTextQuery);
		when(fullTextQuery.getSingleResult()).thenReturn(new User());
		
		assertNotNull(hibernateSearchService.searchUserByUsernameForAuthenticate(username));
	}

	@Test
	void testSearchVisitToRemoveByPatientUsername() {
		String patientUsername = "patientUsername";
		when(queryContextBuilder.forEntity(Visit.class)).thenReturn(entityContext);
		when(entityContext.get()).thenReturn(queryBuilder);
		
		when(queryBuilder.keyword()).thenReturn(termContext);
		when(termContext.onField("patient.user.username")).thenReturn(termMatchingContext);
		when(termMatchingContext.matching(patientUsername)).thenReturn(termTermination);
		when(termTermination.createQuery()).thenReturn(luceneQuery);
		
		when(fullTextSession.createFullTextQuery(luceneQuery, Visit.class)).thenReturn(fullTextQuery);
		List<Visit> visitList = Arrays.asList(new Visit(), new Visit(), new Visit());
		when(fullTextQuery.getResultList()).thenReturn(visitList);
		
		assertEquals(3, hibernateSearchService.searchVisitToRemoveByPatientUsername(patientUsername).size());
	}

}
