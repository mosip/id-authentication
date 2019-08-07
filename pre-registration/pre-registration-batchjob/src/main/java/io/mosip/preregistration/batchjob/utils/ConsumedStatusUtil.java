package io.mosip.preregistration.batchjob.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.batchjob.entity.DemographicEntityConsumed;
import io.mosip.preregistration.batchjob.entity.DocumentEntityConsumed;
import io.mosip.preregistration.batchjob.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.batchjob.entity.RegistrationBookingEntityConsumed;
import io.mosip.preregistration.batchjob.entity.RegistrationBookingPKConsumed;
import io.mosip.preregistration.batchjob.exception.util.BatchServiceExceptionCatcher;
import io.mosip.preregistration.batchjob.repository.utils.BatchJpaRepositoryImpl;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.common.entity.DocumentEntity;
import io.mosip.preregistration.core.common.entity.RegistrationBookingEntity;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class ConsumedStatusUtil {
	
	/** The Constant LOGGER. */
	private Logger log = LoggerConfiguration.logConfig(ConsumedStatusUtil.class);

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
	private BatchJpaRepositoryImpl batchJpaRepositoryImpl;

	@Autowired
	AuditLogUtil auditLogUtil;

	@Value("${mosip.batch.token.authmanager.userName}")
	private String auditUsername;
	
	@Value("${mosip.batch.token.authmanager.appId}")
	private String auditUserId;

	/**
	 * This method will copy demographic , document , booking details to the
	 * respective consumed table and delete from original table if status is
	 * consumed.
	 * 
	 * @return Response DTO
	 */

	public boolean demographicConsumedStatus() {

		List<ProcessedPreRegEntity> preRegList = null;
		boolean isSaveSuccess = false;
		try {
			preRegList = batchJpaRepositoryImpl.getAllConsumedPreIds(STATUS_COMMENTS);
			log.info("sdfsdf", "arg1", "arg2", "arg3");

			preRegList.forEach(iterate -> {
				String preRegId = iterate.getPreRegistrationId();

				DemographicEntityConsumed demographicEntityConsumed = new DemographicEntityConsumed();

				RegistrationBookingEntityConsumed bookingEntityConsumed = new RegistrationBookingEntityConsumed();

				DemographicEntity demographicEntity = batchJpaRepositoryImpl.getApplicantDemographicDetails(preRegId);
				if (demographicEntity != null) {

					demographicEntityConsumed.setApplicantDetailJson(demographicEntity.getApplicantDetailJson());
					demographicEntityConsumed.setCrAppuserId(demographicEntity.getCrAppuserId());
					demographicEntityConsumed.setCreateDateTime(demographicEntity.getCreateDateTime());
					demographicEntityConsumed.setCreatedBy(demographicEntity.getCreatedBy());
					demographicEntityConsumed.setDemogDetailHash(demographicEntity.getDemogDetailHash());
					demographicEntityConsumed.setEncryptedDateTime(demographicEntity.getEncryptedDateTime());
					demographicEntityConsumed.setLangCode(demographicEntity.getLangCode());
					demographicEntityConsumed.setPreRegistrationId(demographicEntity.getPreRegistrationId());
					demographicEntityConsumed.setUpdateDateTime(demographicEntity.getUpdateDateTime());
					demographicEntityConsumed.setUpdatedBy(demographicEntity.getUpdatedBy());
					demographicEntityConsumed.setStatusCode(StatusCodes.CONSUMED.getCode());
					batchJpaRepositoryImpl.updateConsumedDemographic(demographicEntityConsumed);

					List<DocumentEntity> documentEntityList = batchJpaRepositoryImpl.getDocumentDetails(preRegId);
					if (documentEntityList != null) {
						documentEntityList.forEach(documentEntity -> {

							DocumentEntityConsumed documentEntityConsumed = new DocumentEntityConsumed();
							documentEntityConsumed.setCrBy(documentEntity.getCrBy());
							documentEntityConsumed.setCrDtime(documentEntity.getCrDtime());
							documentEntityConsumed.setDocCatCode(documentEntity.getDocCatCode());
							documentEntityConsumed.setDocFileFormat(documentEntity.getDocFileFormat());
							documentEntityConsumed.setDocHash(documentEntity.getDocHash());
							documentEntityConsumed.setDocId(documentEntity.getDocId());
							documentEntityConsumed.setDocName(documentEntity.getDocName());
							documentEntityConsumed.setDocTypeCode(documentEntity.getDocTypeCode());
							documentEntityConsumed.setDocumentId(documentEntity.getDocumentId());
							documentEntityConsumed.setEncryptedDateTime(documentEntity.getEncryptedDateTime());
							documentEntityConsumed.setLangCode(documentEntity.getLangCode());
							documentEntityConsumed.setPreregId(documentEntity.getDemographicEntity().getPreRegistrationId());
							documentEntityConsumed.setStatusCode(documentEntity.getStatusCode());
							documentEntityConsumed.setUpdBy(documentEntity.getUpdBy());
							documentEntityConsumed.setUpdDtime(documentEntity.getUpdDtime());
							batchJpaRepositoryImpl.updateConsumedDocument(documentEntityConsumed);

						});

					}
					RegistrationBookingEntity bookingEntity = batchJpaRepositoryImpl.getPreRegId(preRegId);
					RegistrationBookingPKConsumed consumedPk = new RegistrationBookingPKConsumed();
					consumedPk.setBookingDateTime(bookingEntity.getBookingPK().getBookingDateTime());
					consumedPk.setPreregistrationId(bookingEntity.getDemographicEntity().getPreRegistrationId());
					bookingEntityConsumed.setBookingPK(consumedPk);
					bookingEntityConsumed.setCrBy(bookingEntity.getCrBy());
					bookingEntityConsumed.setCrDate(bookingEntity.getCrDate());
					bookingEntityConsumed.setId(bookingEntity.getId());
					bookingEntityConsumed.setLangCode(bookingEntity.getLangCode());
					bookingEntityConsumed.setRegDate(bookingEntity.getRegDate());
					bookingEntityConsumed.setRegistrationCenterId(bookingEntity.getRegistrationCenterId());
					bookingEntityConsumed.setSlotFromTime(bookingEntity.getSlotFromTime());
					bookingEntityConsumed.setSlotToTime(bookingEntity.getSlotToTime());
					bookingEntityConsumed.setUpBy(bookingEntity.getUpBy());
					bookingEntityConsumed.setUpdDate(bookingEntity.getUpdDate());
					batchJpaRepositoryImpl.updateConsumedBooking(bookingEntityConsumed);

					if (documentEntityList != null) {
						batchJpaRepositoryImpl.deleteDocument(documentEntityList);
					}
					batchJpaRepositoryImpl.deleteBooking(bookingEntity);
					batchJpaRepositoryImpl.deleteDemographic(demographicEntity);
					log.info("sessionId", "idType", "id",
							"Update the status successfully into Consumed tables for Pre-RegistrationId: " + preRegId);

					iterate.setStatusComments(NEW_STATUS_COMMENTS);
					batchJpaRepositoryImpl.updateProcessedList(iterate);
					log.info("sessionId", "idType", "id",
							"Update the comment successfully into Processed PreId List table for Pre-RegistrationId: "
									+ preRegId);
				}

			});
			isSaveSuccess = true;

		} catch (Exception e) {
			new BatchServiceExceptionCatcher().handle(e);
		} finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_412.toString(), EventName.CONSUMEDSTATUS.toString(),
						EventType.BUSINESS.toString(),
						"Upadted the consumed status & the consumed PreRegistration ids successfully saved in the database",
						AuditLogVariables.PRE_REGISTRATION_ID.toString(), auditUserId,
						auditUsername, null);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Consumed status failed to update", AuditLogVariables.NO_ID.toString(),
						auditUserId, auditUsername, null);
			}
		}
		return true;
	}

	/**
	 * This method is used to audit all the consumed status events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,
			String userId, String userName, String refId) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setSessionUserId(userId);
		auditRequestDto.setSessionUserName(userName);
		auditRequestDto.setDescription(description);
		auditRequestDto.setIdType(idType);
		auditRequestDto.setId(refId);
		auditRequestDto.setModuleId(AuditLogVariables.BAT.toString());
		auditRequestDto.setModuleName(AuditLogVariables.CONSUMED_BATCH_SERVICE.toString());
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}

}
