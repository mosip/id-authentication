package io.mosip.preregistration.batchjobservices.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.batchjobservices.code.ErrorCode;
import io.mosip.preregistration.batchjobservices.code.ErrorMessage;
import io.mosip.preregistration.batchjobservices.dto.ResponseDto;
import io.mosip.preregistration.batchjobservices.entity.Applicant_demographic;
import io.mosip.preregistration.batchjobservices.entity.PreRegistrationHistoryTable;
import io.mosip.preregistration.batchjobservices.exceptions.NoPreIdAvailableException;
import io.mosip.preregistration.batchjobservices.repository.PreRegistrationDemographicRepository;
import io.mosip.preregistration.batchjobservices.repository.PreRegistrationHistoryTableRepository;
import io.mosip.preregistration.batchjobservices.service.ArchivingConsumedStatusService;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

/**
 * @author M1043008
 *
 */
@Component
public class ArchivingConsumedStatusServiceImpl implements ArchivingConsumedStatusService {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ArchivingConsumedStatusServiceImpl.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The Constant ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE. */
	private static final String HISTORY_STATUS_TABLE_NOT_ACCESSIBLE = "The demographic history table is not accessible";

	/**
	 * The PreRegistration History Table Repository.
	 */
	@Autowired
	private PreRegistrationHistoryTableRepository historyTableRepository;

	/**
	 * The PreRegistration Applicant Demographic Table Repository.
	 */
	@Autowired
	private PreRegistrationDemographicRepository demographicRepository;
	
	
	/* (non-Javadoc)
	 * @see io.mosip.preregistration.batchjobservices.service.ArchivingConsumedStatusService#archivingConsumed()
	 */
	@Override
	public ResponseDto<String> archivingConsumed(){
		
		ResponseDto<String> response = new ResponseDto<>();
		
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

			} catch (DataAccessLayerException e) {
				throw new TablenotAccessibleException(ErrorCode.PRG_PAM_BAT_002.toString(),
						ErrorMessage.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
			}

		} else {

			LOGGER.info("There are currently no Pre-Registration-Ids to be moved");
			throw new NoPreIdAvailableException(ErrorCode.PRG_PAM_BAT_001.name(),ErrorMessage.NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE.name());
		}
		
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(true);
		response.setResponse("Archiving data successfully");
		return response;
		
	}

}
