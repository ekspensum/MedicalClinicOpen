package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.simpl.RAMJobStore;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.JobStore;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.spi.ThreadExecutor;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailParseException;
import org.springframework.security.core.session.SessionRegistry;

import pl.aticode.medicalclinic.dao.user.PatientRepository;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.service.EmailService;
import pl.aticode.medicalclinic.service.RemovePatientQuartzJobService;
import pl.aticode.medicalclinic.service.RemovePatientQuartzJobService.RemovePatientStatus;
import pl.aticode.medicalclinic.service.UserService;

class RemovePatientQuartzJobServiceTest {
	
	private RemovePatientQuartzJobService removePatientQuartzJobService;
    @Mock
    private UserService userService;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private MessageSource messageSource;
	@Mock
	private JobExecutionContext context;
	@Mock
	private Trigger trigger; 
	@Mock
	private SessionRegistry sessionRegistry;
	@Mock
	private ThreadExecutor threadExecutor;
	@Mock
	private SchedulerSignaler schedulerSignaler;
	@Mock
	private ClassLoadHelper loadHelper;
	
	private Scheduler scheduler;
	private QuartzScheduler quartzScheduler;
	private QuartzSchedulerResources quartzSchedulerResources;
	private JobStore jobStore;
	private JobDetail jobDetail;
	
	@Captor
	private ArgumentCaptor<Long> argumentCaptorLong;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testExecuteInternalJobExecutionContext() throws Exception {
		scheduler = Mockito.mock(Scheduler.class);
		JobKey jobKey = new JobKey("jobKey");
		User user = new User();
		user.setUsername("username");
		user.setFirstName("firstName");
		Patient patient = new Patient();
		patient.setId(33L);
		patient.setSocialUserId("socialUserId");
		patient.setUser(user);
		when(context.getTrigger()).thenReturn(trigger);
		when(trigger.getJobKey()).thenReturn(jobKey);
		when(context.getScheduler()).thenReturn(scheduler);
		
		jobDetail = new JobDetailImpl();
		jobDetail.getJobDataMap().put(jobKey.getName(), patient.getId());
		when(context.getJobDetail()).thenReturn(jobDetail);
		Optional<Patient> optionalPatient = Optional.of(patient);
		when(patientRepository.findById(patient.getId())).thenReturn(optionalPatient);
		
		removePatientQuartzJobService = new RemovePatientQuartzJobService(userService, patientRepository, scheduler, emailService, 
				messageSource, sessionRegistry);
		
		removePatientQuartzJobService.execute(context);
		verify(userService).removePatient(argumentCaptorLong.capture());
		assertEquals(patient.getId(), argumentCaptorLong.getValue());
	}

	@Test
	void testRunRemovePatientJob() throws Exception {
		User user = new User();
		user.setUsername("username");
		user.setFirstName("firstName");
		user.setLanguage("pl");
		user.setEmail("email");
		Patient patientToRemove = new Patient();
		patientToRemove.setId(33L);
		patientToRemove.setSocialUserId("socialUserId");
		patientToRemove.setUser(user);
		when(userService.getPatient(user.getUsername())).thenReturn(patientToRemove);
	
		quartzSchedulerResources = new QuartzSchedulerResources();
		String patientToRemoveId = "customerToRemove"+String.valueOf(patientToRemove.getId());
		
		JobKey jobKey = new JobKey(patientToRemoveId);
		JobDetailImpl jobDetailImpl = new JobDetailImpl();
		jobDetailImpl.setKey(jobKey);

		jobStore = new RAMJobStore();
		jobStore.storeJob(jobDetailImpl, false);		
		
		quartzSchedulerResources.setJobStore(jobStore);
		quartzSchedulerResources.setName("name123");
		quartzSchedulerResources.setThreadExecutor(threadExecutor);
		quartzScheduler = new QuartzScheduler(quartzSchedulerResources, 1000, 1000);
		scheduler = new StdScheduler(quartzScheduler);
		
		removePatientQuartzJobService = new RemovePatientQuartzJobService(userService, patientRepository, scheduler, emailService, 
				messageSource, sessionRegistry);
		assertEquals(RemovePatientStatus.ORDERED_REMOVE_PATIENT, removePatientQuartzJobService.runRemovePatientJob("username"));
		assertEquals(RemovePatientStatus.ORDER_REMOVE_PATIENT_EXIST, removePatientQuartzJobService.runRemovePatientJob("username"));
		
		when(messageSource.getMessage("patient.orderRemove.add.subject", null, new Locale(user.getLanguage()))).thenReturn("emailSubject");
		when(messageSource.getMessage("patient.orderRemove.add.content",
                new String[]{user.getFirstName(), user.getLastName(), user.getUsername(), emailService.getMailFrom()},
                new Locale(user.getLanguage()))).thenReturn("emailContent");
	
		jobKey = new JobKey(patientToRemoveId);
		jobDetailImpl = new JobDetailImpl();
		jobDetailImpl.setKey(jobKey);

		jobStore = new RAMJobStore();
		jobStore.storeJob(jobDetailImpl, false);		
		
		quartzSchedulerResources.setJobStore(jobStore);
		quartzSchedulerResources.setName("name123");
		quartzSchedulerResources.setThreadExecutor(threadExecutor);
		quartzScheduler = new QuartzScheduler(quartzSchedulerResources, 1000, 1000);
		scheduler = new StdScheduler(quartzScheduler);
		
		removePatientQuartzJobService = new RemovePatientQuartzJobService(userService, patientRepository, scheduler, emailService, 
				messageSource, sessionRegistry);
		doThrow(new MailParseException("TEST")).when(emailService).sendEmail(any(String.class), any(String.class), any(String.class));
		assertEquals(RemovePatientStatus.ORDER_REMOVE_PATIENT_DEFEAT, removePatientQuartzJobService.runRemovePatientJob("username"));
	}

	@Test
	void testCancelRemovePatientJob() throws Exception {
		User user = new User();
		user.setUsername("username");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setLanguage("pl");
		Patient patientToRemove = new Patient();
		patientToRemove.setId(66L);
		patientToRemove.setUser(user);
		String customerToRemoveId = "patientToRemove"+String.valueOf(patientToRemove.getId());
		when(userService.getPatient(user.getUsername())).thenReturn(patientToRemove);
		
		scheduler = Mockito.mock(Scheduler.class);
		removePatientQuartzJobService = new RemovePatientQuartzJobService(userService, patientRepository, scheduler, emailService, 
				messageSource, sessionRegistry);
		assertEquals(RemovePatientStatus.ORDER_REMOVE_PATIENT_NOT_EXIST, removePatientQuartzJobService.cancelRemovePatientJob(user.getUsername()));		

		
		JobKey jobKey = new JobKey(customerToRemoveId);
		JobDetail jobDetail = JobBuilder.newJob(RemovePatientQuartzJobService.class)
				.withIdentity(customerToRemoveId, "removePatientJob")
				.withDescription("Remove customer from application - job detail")
				.withIdentity(jobKey)
				.storeDurably()
				.build();
		
		TriggerKey triggerKey = new TriggerKey(customerToRemoveId);
		SimpleTriggerImpl simpleTriggerImpl = new SimpleTriggerImpl();
		simpleTriggerImpl.setKey(triggerKey);
		simpleTriggerImpl.setName(customerToRemoveId);
		simpleTriggerImpl.setGroup("removePatientTrigger");
		simpleTriggerImpl.setJobName(customerToRemoveId);
		simpleTriggerImpl.setJobKey(jobKey);
		simpleTriggerImpl.setStartTime(DateBuilder.evenHourDateAfterNow());
		
		jobStore = new RAMJobStore();
		jobStore.storeJobAndTrigger(jobDetail, simpleTriggerImpl);
		jobStore.initialize(loadHelper, schedulerSignaler);
		
		quartzSchedulerResources = new QuartzSchedulerResources();
		quartzSchedulerResources.setJobStore(jobStore);
		quartzSchedulerResources.setName("name123");
		quartzSchedulerResources.setThreadExecutor(threadExecutor);
		quartzScheduler = new QuartzScheduler(quartzSchedulerResources, 1000, 1000);
		scheduler = new StdScheduler(quartzScheduler);
		
		removePatientQuartzJobService = new RemovePatientQuartzJobService(userService, patientRepository, scheduler, emailService, 
				messageSource, sessionRegistry);
		assertEquals(RemovePatientStatus.CANCEL_OREDER_REMOVE_PATIENT_DEFEAT, removePatientQuartzJobService.cancelRemovePatientJob(user.getUsername()));
	}

}
