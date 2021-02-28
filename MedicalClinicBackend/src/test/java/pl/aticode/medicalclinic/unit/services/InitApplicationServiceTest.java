package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.assertj.core.util.IterableUtil;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import pl.aticode.medicalclinic.dao.user.OwnerRepository;
import pl.aticode.medicalclinic.dao.user.RoleRepository;
import pl.aticode.medicalclinic.dao.user.UserRepository;
import pl.aticode.medicalclinic.entity.user.Owner;
import pl.aticode.medicalclinic.entity.user.Role;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.service.InitApplicationService;

class InitApplicationServiceTest {

	@InjectMocks
	private InitApplicationService initApplicationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private OwnerRepository ownerRepository;
	@Captor
	private ArgumentCaptor<Owner> argumentCaptorOwner;
	@Captor
	private ArgumentCaptor<Role> argumentCaptorRole;
	@Mock
	private EntityManagerFactory entityManagerFactory;
	@Mock
	private EntityManager entityManager;
	@Mock
	private FullTextSession fullTextSession;
	@Mock
	private MassIndexer massIndexer;

	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testInitiateIndexing() {
		when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
		when(entityManager.unwrap(Session.class)).thenReturn(fullTextSession);
		Mockito.when(fullTextSession.createIndexer()).thenReturn(massIndexer);
		Mockito.when(massIndexer.batchSizeToLoadObjects(15)).thenReturn(massIndexer);
		Mockito.when(massIndexer.cacheMode(CacheMode.NORMAL)).thenReturn(massIndexer);
		Mockito.when(massIndexer.threadsToLoadObjects(3)).thenReturn(massIndexer);
		initApplicationService = new InitApplicationService(passwordEncoder, userRepository, roleRepository, ownerRepository, entityManagerFactory);
	}
	
	@Test
	void testInsertBasicDataToDatabase() {
		Iterable<Role> iterableRole = IterableUtil.iterable();
		when(roleRepository.findAll()).thenReturn(iterableRole);
		Iterable<User> iterableUser = IterableUtil.iterable();
		when(userRepository.findAll()).thenReturn(iterableUser);
		
		initApplicationService.insertBasicDataToDatabase();
		verify(roleRepository, times(6)).save(argumentCaptorRole.capture());
		assertEquals(6, argumentCaptorRole.getAllValues().size());
		
		iterableRole = IterableUtil.iterable(new Role());
		when(roleRepository.findAll()).thenReturn(iterableRole);
		assertThrows(IllegalStateException.class, ()-> {
			initApplicationService.insertBasicDataToDatabase();
		});

		verify(ownerRepository).save(argumentCaptorOwner.capture());
		assertNotNull(argumentCaptorOwner.getValue());
		
	}

}
