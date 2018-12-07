package io.mosip.preregistration.batchjob.tasklets;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.preregistration.batchjob.entity.Applicant_demographic;
import io.mosip.preregistration.batchjob.entity.Processed_prereg_list;
import io.mosip.preregistration.batchjob.repository.PreRegistrationProcessedPreIdRepository;
import io.mosip.preregistration.batchjob.repository.PreRegistrationDemographicRepository;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

/**
 * @author M1043008
 *
 */
//@RefreshScope
@Component
public class UpdateDemographicStatusTasklet implements Tasklet {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDemographicStatusTasklet.class);
	
	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";
	
	/** The Constant ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE. */
	private static final String APPLICANT_DEMOGRAPHIC_STATUS_TABLE_NOT_ACCESSIBLE = "The applicant demographic table is not accessible";

	private static final String STATUS = "Consumed";
	
	private static final boolean IS_NEW=true;
	
	@Autowired
	@Qualifier("preRegProcessedRepository")
	private PreRegistrationProcessedPreIdRepository preRegListRepo;
	
	@Autowired
	@Qualifier("preRegistrationDemographicRepository")
	private PreRegistrationDemographicRepository preRegistrationDemographicRepository;
	

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		
		List<Processed_prereg_list> preRegList=new ArrayList<>();
		
		preRegList=preRegListRepo.findByisNew(IS_NEW);
		
		if(!preRegList.isEmpty()) {
			preRegList.forEach(iterate ->{
				String status=iterate.getStatusCode();
				String preRegId=iterate.getPrereg_id();
				
				try {
					Applicant_demographic applicant_demographic=preRegistrationDemographicRepository.findBypreRegistrationId(preRegId);
					
					applicant_demographic.setStatusCode(status);
					
					preRegistrationDemographicRepository.save(applicant_demographic);
					
					iterate.setNew(false);
					
					LOGGER.info(LOGDISPLAY,"Update the status successfully into Applicant demographic table");
					
				} catch (TablenotAccessibleException e) {

					LOGGER.error(LOGDISPLAY,APPLICANT_DEMOGRAPHIC_STATUS_TABLE_NOT_ACCESSIBLE , e);
				}
			});
			
		}
		else {
			LOGGER.info("There are currently no Pre-Registration-Ids to be moved");
		}
		return RepeatStatus.FINISHED;
	}

}
