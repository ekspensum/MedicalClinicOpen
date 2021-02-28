package pl.aticode.medicalclinic.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import pl.aticode.medicalclinic.dao.visit.VisitRepository;
import pl.aticode.medicalclinic.entity.user.Doctor;
import pl.aticode.medicalclinic.entity.user.Patient;
import pl.aticode.medicalclinic.entity.visit.Visit;


@Service
public class AlertVisitDateQuartzJobService extends QuartzJobBean {
	
	private final static Logger logger = LoggerFactory.getLogger(AlertVisitDateQuartzJobService.class);
    
	private String alertDateOfVisitId = null;
    private String alertDateOfVisitIdTrigger1 = null;
    private String alertDateOfVisitIdTrigger2 = null;
    private TriggerKey triggerKey1 = null;
    private TriggerKey triggerKey2 = null;
    private JobKey jobKey = null;
    
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private EmailService emailService;
    @Autowired
    private MessageSource messageSource;
    
    
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
        	JobKey jobKey = context.getTrigger().getJobKey();
			final long visitId = (long) context.getJobDetail().getJobDataMap().get(jobKey.getName());
			Optional<Visit> visitOptional = visitRepository.findById(visitId);
			if(visitOptional.isPresent()) {
				Visit visit = visitOptional.get();
				Patient patient = visit.getPatient();
				Doctor doctor = visit.getDoctor();
				String emailSubject = messageSource.getMessage("visit.alert.date.email.subject", null, new Locale(patient.getUser().getLanguage()));
				String emailContent = messageSource.getMessage("visit.alert.date.email.content",
						new String[]{patient.getUser().getFirstName(), patient.getUser().getLastName(), 
								visit.getVisitDateTime().toLocalDate().toString(), visit.getVisitDateTime().toLocalTime().toString(),
								doctor.getUser().getFirstName(), doctor.getUser().getLastName(),
								emailService.getMailFrom()},
						new Locale(patient.getUser().getLanguage()));
				emailService.sendEmail(patient.getUser().getEmail(), emailSubject, emailContent);
				logger.info("Sent alert at oncoming date of visit id: {}", visit.getId());
				if(LocalDate.now().equals(visit.getVisitDateTime().toLocalDate())){
					scheduler.deleteJob(jobKey);
				}
			} else {
				throw new JobExecutionException("Visit not found!");
			}
		} catch (MailException e) {
            logger.error("Mail ERROR send alert at oncomming date of visit {}", e);
        } catch (SchedulerException e) {
            logger.error("Scheduler ERROR send alert at oncomming date of visit {}", e);
        } catch (Exception e) {
            logger.error("Unknow ERROR send alert at oncomming date of visit {}", e);
        }
	}

	/**
	 * Setting alert at oncoming date of visit.
	 * @param visit
	 */
    public void setAlertDateOfVisitJob(Visit visit) {
        alertDateOfVisitId = "alertDateOfVisit" + String.valueOf(visit.getId());
        try {
            JobDetail jobDetail = JobBuilder.newJob(AlertVisitDateQuartzJobService.class)
                    .withIdentity(alertDateOfVisitId, "alertDateOfVisitJob")
                    .withDescription("Notification at date of visit - job detail")
                    .storeDurably()
                    .build();
            jobDetail.getJobDataMap().put(alertDateOfVisitId, visit.getId());
            alertDateOfVisitIdTrigger1 = jobDetail.getKey().getName()+"Trigger1";
            SimpleTrigger trigger2DaysBefore = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(alertDateOfVisitIdTrigger1, "alertDateOfVisitTrigger")
                    .withDescription("Notification at date of visit - trigger")
                    .startAt(DateBuilder.newDate()
                            .atHourOfDay(6).atMinute(0)
                            .onDay(visit.getVisitDateTime().minusDays(2).getDayOfMonth())
                            .inMonth(visit.getVisitDateTime().getMonthValue())
                            .inYear(visit.getVisitDateTime().getYear())
                            .build())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                    .build();
            alertDateOfVisitIdTrigger2 = jobDetail.getKey().getName()+"Trigger2";
            SimpleTrigger triggerInTheSameDay = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(alertDateOfVisitIdTrigger2, "alertDateOfVisitTrigger")
                    .withDescription("Notification at date of visit - trigger")
                    .startAt(DateBuilder.newDate()
                            .atHourOfDay(6).atMinute(0)
                            .onDay(visit.getVisitDateTime().getDayOfMonth())
                            .inMonth(visit.getVisitDateTime().getMonthValue())
                            .inYear(visit.getVisitDateTime().getYear())
                            .build())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                    .build();  
            Set<SimpleTrigger> triggersForJob = new HashSet<>();
            triggersForJob.add(trigger2DaysBefore);
            triggersForJob.add(triggerInTheSameDay);
            scheduler.scheduleJob(jobDetail, triggersForJob, true);
            logger.info("Added alert at oncoming date of visit id: {}", visit.getId());
        } catch (SchedulerException e) {
            logger.error("Scheduler ERROR send alert at oncoming date of visit {}", e);
        }
    }
	
    /**
     * Remove alert at oncoming date of visit - after canceled visit term. 
     * @param visit
     */
    public void removeAlertDateOfVisitJob(Visit visit) {
        alertDateOfVisitId = "alertDateOfVisit" + String.valueOf(visit.getId());
        alertDateOfVisitIdTrigger1 = alertDateOfVisitId + "Trigger1";
        alertDateOfVisitIdTrigger2 = alertDateOfVisitId + "Trigger2";
        triggerKey1 = TriggerKey.triggerKey(alertDateOfVisitIdTrigger1, "alertDateOfVisitTrigger");
        triggerKey2 = TriggerKey.triggerKey(alertDateOfVisitIdTrigger2, "alertDateOfVisitTrigger");
        jobKey = JobKey.jobKey(alertDateOfVisitId, "alertDateOfVisitJob");
        try {
            boolean checkExistsJob = scheduler.checkExists(jobKey);
            if (checkExistsJob) {
                scheduler.pauseTrigger(triggerKey1);
                scheduler.unscheduleJob(triggerKey1);
                scheduler.pauseTrigger(triggerKey2);
                scheduler.unscheduleJob(triggerKey2);
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException ex) {
            logger.error("Scheduler ERROR remove alert at oncomming date to make an offer {}", ex);
        }
    }
	

}
