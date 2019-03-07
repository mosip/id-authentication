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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.batchjobservices.entity.DemographicEntity;
import io.mosip.preregistration.batchjobservices.entity.DemographicEntityConsumed;
import io.mosip.preregistration.batchjobservices.entity.DocumentEntity;
import io.mosip.preregistration.batchjobservices.entity.DocumentEntityConsumed;
import io.mosip.preregistration.batchjobservices.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntityConsumed;
import io.mosip.preregistration.batchjobservices.exceptions.util.BatchServiceExceptionCatcher;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.core.code.StatusCodes;
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
				String preRegId = iterate.getPreRegistrationId();

				DemographicEntityConsumed demographicEntityConsumed = new DemographicEntityConsumed();
				DocumentEntityConsumed documentEntityConsumed = new DocumentEntityConsumed();
				RegistrationBookingEntityConsumed bookingEntityConsumed = new RegistrationBookingEntityConsumed();
				
				DemographicEntity demographicEntity = batchServiceDAO.getApplicantDemographicDetails(preRegId);
				if (demographicEntity != null) {

					BeanUtils.copyProperties(demographicEntity, demographicEntityConsumed);
					demographicEntityConsumed.setStatusCode(StatusCodes.CONSUMED.getCode());
					batchServiceDAO.updateConsumedDemographic(demographicEntityConsumed);

					DocumentEntity documentEntity = batchServiceDAO.getDocumentDetails(preRegId);
					BeanUtils.copyProperties(documentEntity, documentEntityConsumed);
					batchServiceDAO.updateConsumedDocument(documentEntityConsumed);

					RegistrationBookingEntity bookingEntity = batchServiceDAO.getPreRegId(preRegId);
					BeanUtils.copyProperties(bookingEntity, bookingEntityConsumed);
					batchServiceDAO.updateConsumedBooking(bookingEntityConsumed);

					batchServiceDAO.deleteBooking(bookingEntity);
					batchServiceDAO.deleteDocument(documentEntity);
					batchServiceDAO.deleteDemographic(demographicEntity);
					LOGGER.info(LOGDISPLAY, "Update the status successfully into Consumed tables Pre-Registration");
				}

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
