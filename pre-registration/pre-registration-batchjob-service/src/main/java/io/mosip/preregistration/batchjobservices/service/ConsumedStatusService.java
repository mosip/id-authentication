/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.batchjobservices.entity.DemographicEntityConsumed;
import io.mosip.preregistration.batchjobservices.entity.DocumentEntity;
import io.mosip.preregistration.batchjobservices.entity.DocumentEntityConsumed;
import io.mosip.preregistration.batchjobservices.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntityConsumed;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingPKConsumed;
import io.mosip.preregistration.batchjobservices.exception.util.BatchServiceExceptionCatcher;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.GenericUtil;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */

@Component
public class ConsumedStatusService {

	/** The Constant LOGGER. */
	private Logger log = LoggerConfiguration.logConfig(ConsumedStatusService.class);


	/** The Constant Status comments. */
	private static final String STATUS_COMMENTS = "Processed by registration processor";

	/** The Constant Status comments. */
	private static final String NEW_STATUS_COMMENTS = "Application consumed";
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;
	
	@Value("${version}")
	String versionUrl;

	@Value("${mosip.preregistration.batchjob.service.consumed.id}")
	String idUrl;

	/**
	 * Autowired reference for {@link #batchServiceDAO}
	 */
	@Autowired
	private BatchServiceDAO batchServiceDAO;

	/**
	 * This method will copy demographic , document , booking details to the respective 
	 * consumed table and delete from original table if status is consumed.
	 * 
	 * @return Response DTO
	 */
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<String> demographicConsumedStatus() {

		MainResponseDTO<String> response = new MainResponseDTO<>();
		response.setId(idUrl);
		response.setVersion(versionUrl);
		List<ProcessedPreRegEntity> preRegList = null;
		try {
			preRegList = batchServiceDAO.getAllConsumedPreIds(STATUS_COMMENTS);

			preRegList.forEach(iterate -> {
				String preRegId = iterate.getPreRegistrationId();

				DemographicEntityConsumed demographicEntityConsumed = new DemographicEntityConsumed();
				
				RegistrationBookingEntityConsumed bookingEntityConsumed = new RegistrationBookingEntityConsumed();
				
				DemographicEntity demographicEntity = batchServiceDAO.getApplicantDemographicDetails(preRegId);
				if (demographicEntity != null) {

					BeanUtils.copyProperties(demographicEntity, demographicEntityConsumed);
					demographicEntityConsumed.setStatusCode(StatusCodes.CONSUMED.getCode());
					batchServiceDAO.updateConsumedDemographic(demographicEntityConsumed);

					List<DocumentEntity> documentEntityList = batchServiceDAO.getDocumentDetails(preRegId);
					documentEntityList.forEach(documentEntity->{
						if(documentEntity!=null) {
							DocumentEntityConsumed documentEntityConsumed = new DocumentEntityConsumed();
							BeanUtils.copyProperties(documentEntity, documentEntityConsumed);
							batchServiceDAO.updateConsumedDocument(documentEntityConsumed);
						}
					});
					
					
					RegistrationBookingEntity bookingEntity = batchServiceDAO.getPreRegId(preRegId);
					BeanUtils.copyProperties(bookingEntity, bookingEntityConsumed);
					RegistrationBookingPKConsumed consumedPk=new RegistrationBookingPKConsumed();
					consumedPk.setBookingDateTime(bookingEntity.getBookingPK().getBookingDateTime());
					consumedPk.setPreregistrationId(bookingEntity.getBookingPK().getPreregistrationId());
					bookingEntityConsumed.setBookingPK(consumedPk);
					batchServiceDAO.updateConsumedBooking(bookingEntityConsumed);

					if(documentEntityList!=null) {
						batchServiceDAO.deleteDocument(documentEntityList);
					}
					batchServiceDAO.deleteBooking(bookingEntity);
					batchServiceDAO.deleteDemographic(demographicEntity);
					log.info("sessionId", "idType", "id", "Update the status successfully into Consumed tables for Pre-RegistrationId: "+preRegId);
					
					iterate.setStatusComments(NEW_STATUS_COMMENTS);
					batchServiceDAO.updateProcessedList(iterate);
					log.info("sessionId", "idType", "id", "Update the comment successfully into Processed PreId List table for Pre-RegistrationId: "+preRegId);
				}

			});

		} catch (Exception e) {
			new BatchServiceExceptionCatcher().handle(e,response);
		}
		response.setResponsetime(GenericUtil.getCurrentResponseTime());
		response.setId(idUrl);
		response.setVersion(versionUrl);
		response.setResponse("Demographic status to consumed updated successfully");
		return response;
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
	}

}
