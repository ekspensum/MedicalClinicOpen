package pl.aticode.medicalclinic.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.spi.ThreadExecutor;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSendException;

import pl.aticode.medicalclinic.dao.visit.VisitRepository;
import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.user.User;
import pl.aticode.medicalclinic.entity.visit.Visit;
import pl.aticode.medicalclinic.service.AlertVisitDateQuartzJobService;
import pl.aticode.medicalclinic.service.EmailService;

class AlertVisitDateQuartzJobServiceTest {

	@InjectMocks
	private AlertVisitDateQuartzJobService alertVisitDateQuartzJobService;
    @Mock
    private VisitRepository visitRepository;
    @Mock
    private Scheduler scheduler;
    @Mock
    private EmailService emailService;
    @Mock
    private MessageSource messageSource;
	@Mock
	private JobExecutionContext context;
	@Mock
	private SchedulerContext schedulerContext;
	@Mock
	private Trigger trigger; 
	@Mock
	private ThreadExecutor threadExecutor;
	@Mock
	private SchedulerSignaler schedulerSignaler;
	@Mock
	private JobDetail jobDetail;
	@Mock
	private JobDataMap jobDataMap;
	@Captor
	private ArgumentCaptor<String> argCaptorMailTo;
	@Captor
	private ArgumentCaptor<String> argCaptorMailSubject;
	@Captor
	private ArgumentCaptor<String> argCaptorMailContent;
	@Captor
	private ArgumentCaptor<JobDetail> argCaptorJobDetail;
	@Captor
	private ArgumentCaptor<Set<SimpleTrigger>> argCaptorSimpleTrgger;
	@Captor
	private ArgumentCaptor<Boolean> argCaptorReplace;
	
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testExecuteInternalJobExecutionContext() throws SchedulerException {
		User user = new User();
		user.setEmail("email");
		user.setLanguage("pl");
		Patient patient = new Patient();
		patient.setUser(user);
		Doctor doctor = new Doctor();
		doctor.setUser(user);
		Visit visit = new Visit();
		visit.setId(55L);
		visit.setPatient(patient);
		visit.setDoctor(doctor);
		visit.setVisitDateTime(LocalDateTime.now().plusDays(1));
		
		JobKey jobKey = new JobKey("jobKey");
		when(context.getTrigger()).thenReturn(trigger);
		when(trigger.getJobKey()).thenReturn(jobKey);
		when(context.getScheduler()).thenReturn(scheduler);
		when(scheduler.getContext()).thenReturn(schedulerContext);
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.get(jobKey.getName())).thenReturn(visit.getId());
		Optional<Visit> optionalVisit = Optional.of(visit);
		when(visitRepository.findById(visit.getId())).thenReturn(optionalVisit);
		
		alertVisitDateQuartzJobService.execute(context);
		verify(emailService).sendEmail(argCaptorMailTo.capture(), argCaptorMailSubject.capture(), argCaptorMailContent.capture());
		assertEquals(user.getEmail(), argCaptorMailTo.getValue());
		
		doThrow(new MailSendException("TEST")).when(emailService).sendEmail(argCaptorMailTo.capture(), argCaptorMailSubject.capture(), argCaptorMailContent.capture());
		alertVisitDateQuartzJobService.execute(context);
		assertThrows(MailSendException.class, () -> emailService.sendEmail(argCaptorMailTo.capture(), argCaptorMailSubject.capture(), argCaptorMailContent.capture()));
	}

	@Test
	void testSetAlertDateOfVisitJob() throws SchedulerException {
		Visit visit = new Visit();
		visit.setId(55L);
		visit.setVisitDateTime(LocalDateTime.now().plusDays(1));
		
		alertVisitDateQuartzJobService.setAlertDateOfVisitJob(visit);
		verify(scheduler).scheduleJob(argCaptorJobDetail.capture(), argCaptorSimpleTrgger.capture(), argCaptorReplace.capture());
		assertEquals("alertDateOfVisit55", argCaptorJobDetail.getValue().getKey().getName());
		
		doThrow(new SchedulerException("TEST")).when(scheduler).scheduleJob(argCaptorJobDetail.capture(), argCaptorSimpleTrgger.capture(), argCaptorReplace.capture());
		alertVisitDateQuartzJobService.setAlertDateOfVisitJob(visit);
		assertThrows(SchedulerException.class, () -> scheduler.scheduleJob(argCaptorJobDetail.capture(), argCaptorSimpleTrgger.capture(), argCaptorReplace.capture()));
	}

	@Test
	void testRemoveAlertDateOfVisitJob() throws SchedulerException {
		Visit visit = new Visit();
		visit.setId(55L);
		
		when(scheduler.checkExists(new JobKey("alertDateOfVisit55", "alertDateOfVisitJob"))).thenReturn(true);
		alertVisitDateQuartzJobService.removeAlertDateOfVisitJob(visit);
		
		doThrow(new SchedulerException("TEST")).when(scheduler).checkExists(new JobKey("alertDateOfVisit55", "alertDateOfVisitJob"));
		alertVisitDateQuartzJobService.removeAlertDateOfVisitJob(visit);
		assertThrows(SchedulerException.class, () -> scheduler.checkExists(new JobKey("alertDateOfVisit55", "alertDateOfVisitJob")));

	}

}
