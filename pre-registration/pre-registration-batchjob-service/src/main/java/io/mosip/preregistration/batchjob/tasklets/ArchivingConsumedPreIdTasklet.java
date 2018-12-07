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
import org.springframework.stereotype.Component;

import io.mosip.preregistration.batchjob.entity.Applicant_demographic;
import io.mosip.preregistration.batchjob.entity.PreRegistrationHistoryTable;
import io.mosip.preregistration.batchjob.repository.PreRegistrationDemographicRepository;
import io.mosip.preregistration.batchjob.repository.PreRegistrationHistoryTableRepository;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

@Component
public class ArchivingConsumedPreIdTasklet implements Tasklet {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDemographicStatusTasklet.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The Constant ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE. */
	private static final String HISTORY_STATUS_TABLE_NOT_ACCESSIBLE = "The demographic history table is not accessible";

	@Autowired
	private PreRegistrationHistoryTableRepository historyTableRepository;

	@Autowired
	private PreRegistrationDemographicRepository demographicRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		List<Applicant_demographic> demographicList = new ArrayList<Applicant_demographic>();

		demographicList = demographicRepository.findAll();

		PreRegistrationHistoryTable historyTable = new PreRegistrationHistoryTable();

		if (!demographicList.isEmpty()) {

			try {
				demographicList.forEach(iterate -> {

					historyTable.setApplicantDetailJson(iterate.getApplicantDetailJson());
					historyTable.setCr_appuser_id(iterate.getCr_appuser_id());
					historyTable.setCreateDateTime(iterate.getCreateDateTime());
					historyTable.setCreatedBy(iterate.getCreatedBy());
					historyTable.setDeletedDateTime(iterate.getDeletedDateTime());
					historyTable.setGroupId(iterate.getGroupId());
					historyTable.setIsDeleted(iterate.getIsDeleted());
					historyTable.setLangCode(iterate.getLangCode());
					historyTable.setPreRegistrationId(iterate.getPreRegistrationId());
					historyTable.setStatusCode(iterate.getStatusCode());
					historyTable.setUpdatedBy(iterate.getUpdatedBy());

					historyTableRepository.save(historyTable);
					
					demographicRepository.delete(iterate);

					LOGGER.info(LOGDISPLAY, "Update the history table from applicant demographic table");

				});

			} catch (TablenotAccessibleException e) {
				LOGGER.error(LOGDISPLAY, HISTORY_STATUS_TABLE_NOT_ACCESSIBLE, e);
			}

		} else {

			LOGGER.info("There are currently no Pre-Registration-Ids to be moved");
		}

		return RepeatStatus.FINISHED;
	}

}
