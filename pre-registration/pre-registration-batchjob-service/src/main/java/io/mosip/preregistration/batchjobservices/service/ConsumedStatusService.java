/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.batchjobservices.entity.ApplicantDemographic;
import io.mosip.preregistration.batchjobservices.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.exceptions.util.BatchServiceExceptionCatcher;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */

@Component
public class ConsumedStatusService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumedStatusService.class);

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The Constant Status comments. */
	private static final String STATUS_COMMENTS = "Processed by registration processor";

	/** The Constant Status comments. */
	private static final String NEW_STATUS_COMMENTS = "Application consumed";

	/**
	 * Autowired reference for {@link #batchServiceDAO}
	 */
	@Autowired
	private BatchServiceDAO batchServiceDAO;
	
	/**
	 * @return Response DTO
	 */
	public MainResponseDTO<String> demographicConsumedStatus() {

		MainResponseDTO<String> response = new MainResponseDTO<>();

		List<ProcessedPreRegEntity> preRegList = new ArrayList<>();
		try {
			preRegList = batchServiceDAO.getAllConsumedPreIds(STATUS_COMMENTS);

			preRegList.forEach(iterate -> {
				String status = iterate.getStatusCode();
				String preRegId = iterate.getPreRegistrationId();

				ApplicantDemographic demographicEntity = batchServiceDAO.getApplicantDemographicDetails(preRegId);
				demographicEntity.setStatusCode(status);
				batchServiceDAO.updateApplicantDemographic(demographicEntity);
				LOGGER.info(LOGDISPLAY, "Update the status successfully into Applicant demographic table");
				
				RegistrationBookingEntity bookingEntity=batchServiceDAO.getPreRegId(preRegId);
				bookingEntity.setStatusCode(status);
				batchServiceDAO.updateBooking(bookingEntity);
				LOGGER.info(LOGDISPLAY, "Update the status successfully into Booking table");
				
				iterate.setStatusComments(NEW_STATUS_COMMENTS);
				batchServiceDAO.updateProcessedList(iterate);
				LOGGER.info(LOGDISPLAY, "Update the comment successfully into Processed PreId List table");
				

			});

		} catch (Exception e) {
			 new BatchServiceExceptionCatcher().handle(e);
		}
		response.setResTime(getCurrentResponseTime());
		response.setStatus(true);
		response.setResponse("Demographic status to consumed updated successfully");
		return response;
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}

}
