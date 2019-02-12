/**
 * 
 */
package io.mosip.registration.processor.stages.packet.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.MainRequestDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.MainResponseDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ReverseDataSyncRequestDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ReverseDatasyncReponseDTO;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.utils.ApplicantDocumentValidation;
import io.mosip.registration.processor.stages.utils.CheckSumValidation;
import io.mosip.registration.processor.stages.utils.DocumentUtility;
import io.mosip.registration.processor.stages.utils.FilesValidation;
import io.mosip.registration.processor.stages.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class PacketValidatorStage.
 *
 * @author M1022006
 * @author Girish Yarru
 */

@RefreshScope
@Service
public class PacketValidatorStage extends MosipVerticleManager {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketValidatorStage.class);

	/** The adapter. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant APPLICANT_TYPE. */
	public static final String APPLICANT_TYPE = "applicantType";

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The core audit request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	DocumentUtility documentUtility;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;

	/** The registration id. */
	private String registrationId = "";

	/** The description. */
	private String description;

	/** The is transaction successful. */
	private boolean isTransactionSuccessful;

	/** The secs. */
	private long secs = 30;

	private static final String PRE_REG_ID = "mosip.pre-registration.datasync";
	private static final String VERSION = "1.0";
	private static final String CREATED_BY = "MOSIP_SYSTEM";

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		mosipEventBus.getEventbus().setPeriodic(secs * 1000, msg -> process(new MessageDTO()));
	}

	/**
	 * Send message.
	 *
	 * @param mosipEventBus
	 *            the mosip event bus
	 * @param message
	 *            the message
	 */
	public void sendMessage(MosipEventBus mosipEventBus, MessageDTO message) {
		this.send(mosipEventBus, MessageBusAddress.PACKET_VALIDATOR_BUS_OUT, message);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {

		List<InternalRegistrationStatusDto> dtolist = null;
		List<String> registrationIds = new ArrayList<>();
		List<String> preRegistrationIds = new ArrayList<>();

		try {

			object.setMessageBusAddress(MessageBusAddress.PACKET_VALIDATOR_BUS_IN);
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.FALSE);

			dtolist = registrationStatusService
					.getByStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());
			if (!(dtolist.isEmpty())) {
				dtolist.forEach(dto -> {
					this.registrationId = dto.getRegistrationId();
					description = "";
					isTransactionSuccessful = false;
					InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
					try {
						registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);
						InputStream packetMetaInfoStream = adapter.getFile(registrationId,
								PacketFiles.PACKET_META_INFO.name());
						PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil
								.inputStreamtoJavaObject(packetMetaInfoStream, PacketMetaInfo.class);

						FilesValidation filesValidation = new FilesValidation(adapter, registrationStatusDto);
						boolean isFilesValidated = filesValidation.filesValidation(registrationId,
								packetMetaInfo.getIdentity());
						boolean isCheckSumValidated = false;
						boolean isApplicantDocumentValidation = false;
						InputStream documentInfoStream = null;
						InputStream demographicInfoStream = null;
						byte[] bytesArray = null;
						List<Document> documentList = null;
						byte[] bytes = null;
						if (isFilesValidated) {
							documentInfoStream = adapter.getFile(registrationId,
									PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
							bytes = IOUtils.toByteArray(documentInfoStream);
							documentList = documentUtility.getDocumentList(bytes);

							CheckSumValidation checkSumValidation = new CheckSumValidation(adapter,
									registrationStatusDto);

							isCheckSumValidated = checkSumValidation.checksumvalidation(registrationId,
									packetMetaInfo.getIdentity());

							if (isCheckSumValidated) {
								ApplicantDocumentValidation applicantDocumentValidation = new ApplicantDocumentValidation(
										registrationStatusDto);
								isApplicantDocumentValidation = applicantDocumentValidation
										.validateDocument(packetMetaInfo.getIdentity(), documentList, registrationId);

							}

						}

						if (isFilesValidated && isCheckSumValidated && isApplicantDocumentValidation) {
							object.setIsValid(Boolean.TRUE);
							registrationStatusDto.setStatusComment(StatusMessage.PACKET_STRUCTURAL_VALIDATION_SUCCESS);
							registrationStatusDto
									.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_SUCCESS.toString());
							packetInfoManager.savePacketData(packetMetaInfo.getIdentity());
							demographicInfoStream = adapter.getFile(registrationId,
									PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
							bytesArray = IOUtils.toByteArray(demographicInfoStream);
							packetInfoManager.saveDemographicInfoJson(bytesArray,
									packetMetaInfo.getIdentity().getMetaData());
							packetInfoManager.saveDocuments(documentList);
							// ReverseDataSync
							registrationIds.add(dto.getRegistrationId());
							IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
							String preRegId = identityIteratorUtil.getFieldValue(
									packetMetaInfo.getIdentity().getMetaData(), JsonConstant.PREREGISTRATIONID);
							if (preRegId != null)
								preRegistrationIds.add(preRegId);
							object.setRid(dto.getRegistrationId());
							isTransactionSuccessful = true;
							description = "Structural validation success for registrationId " + registrationId;

						} else {
							object.setIsValid(Boolean.FALSE);
							object.setRid(dto.getRegistrationId());
							int retryCount = registrationStatusDto.getRetryCount() != null
									? registrationStatusDto.getRetryCount() + 1
									: 1;
							description = "File validation(" + isFilesValidated + ")/Checksum validation("
									+ isCheckSumValidated + ")/Applicant Document Validation("
									+ isApplicantDocumentValidation + ") failed for registrationId " + registrationId;
							isTransactionSuccessful = false;
							registrationStatusDto.setRetryCount(retryCount);

							registrationStatusDto
									.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED.toString());
							registrationStatusDto.setStatusComment(description);
						}

						registrationStatusDto.setUpdatedBy(USER);

						setApplicant(packetMetaInfo.getIdentity(), registrationStatusDto);

						registrationStatusService.updateRegistrationStatus(registrationStatusDto);
					} catch (DataAccessException e) {
						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
								PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + e.getMessage()
										+ ExceptionUtils.getStackTrace(e));
						object.setInternalError(Boolean.TRUE);
						isTransactionSuccessful = false;
						description = "Data access exception for the packet with registrationId " + registrationId
								+ "::" + e.getMessage();
						object.setIsValid(Boolean.FALSE);
						object.setRid(dto.getRegistrationId());

					} catch (IOException exc) {
						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
								PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + exc.getMessage()
										+ ExceptionUtils.getStackTrace(exc));
						object.setInternalError(Boolean.TRUE);
						description = "Internal error occured while processing for the registrationId " + registrationId
								+ "::" + exc.getMessage();
						object.setIsValid(Boolean.FALSE);
						object.setRid(dto.getRegistrationId());

					} catch (Exception ex) {
						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
								PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + ex.getMessage()
										+ ExceptionUtils.getStackTrace(ex));
						object.setInternalError(Boolean.TRUE);
						isTransactionSuccessful = false;
						description = "Internal error occured while processing for the registrationId " + registrationId
								+ "::" + ex.getMessage();
						object.setIsValid(Boolean.FALSE);
						object.setRid(dto.getRegistrationId());

					} finally {

						regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description);
						if (object.getInternalError()) {
							registrationStatusDto.setUpdatedBy(USER);
							int retryCount = registrationStatusDto.getRetryCount() != null
									? registrationStatusDto.getRetryCount() + 1
									: 1;
							isTransactionSuccessful = false;
							registrationStatusDto.setRetryCount(retryCount);

							registrationStatusDto
									.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED.toString());
							registrationStatusDto.setStatusComment(description);
							registrationStatusService.updateRegistrationStatus(registrationStatusDto);

						}
						sendMessage(mosipEventBus, object);
						String eventId = "";
						String eventName = "";
						String eventType = "";

						if (isTransactionSuccessful) {
							eventId = EventId.RPR_402.toString();
							eventName = EventName.UPDATE.toString();
							eventType = EventType.BUSINESS.toString();
						} else {

							eventId = EventId.RPR_405.toString();
							eventName = EventName.EXCEPTION.toString();
							eventType = EventType.SYSTEM.toString();
						}
						auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
								registrationId);

					}
				});
			}

			if (!registrationIds.isEmpty()) {
				isTransactionSuccessful = false;
				// preregIds = packetInfoManager.getRegOsiPreRegId(registrationIds);
				MainResponseDTO<ReverseDatasyncReponseDTO> mainResponseDto = null;
				if (preRegistrationIds != null && !preRegistrationIds.isEmpty()) {
					MainRequestDTO<ReverseDataSyncRequestDTO> mainRequestDto = new MainRequestDTO<>();
					mainRequestDto.setId(PRE_REG_ID);
					mainRequestDto.setVer(VERSION);
					mainRequestDto.setReqTime(new Date());
					ReverseDataSyncRequestDTO reverseDataSyncRequestDto = new ReverseDataSyncRequestDTO();
					reverseDataSyncRequestDto.setCreatedBy(CREATED_BY);
					reverseDataSyncRequestDto.setLangCode("eng");
					reverseDataSyncRequestDto.setPreRegistrationIds(preRegistrationIds);
					reverseDataSyncRequestDto.setCreatedDateTime(new Date());
					reverseDataSyncRequestDto.setUpdateDateTime(new Date());
					reverseDataSyncRequestDto.setUpdateBy(CREATED_BY);
					mainRequestDto.setRequest(reverseDataSyncRequestDto);

					mainResponseDto = (MainResponseDTO) restClientService.postApi(ApiName.REVERSEDATASYNC, "", "",
							mainRequestDto, MainResponseDTO.class);
					isTransactionSuccessful = true;
					description = "Reverse data sync in packet validator successful for " + preRegistrationIds.size()
							+ " pre-registrationIds";

				} else {
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationIds.toString(),
							PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage()
									+ "as pre registartion Ids are not found");
					isTransactionSuccessful = false;
					description = "Reverse data sync in packet validator failed as there are no any pre-registrationIds";

				}

				if (mainResponseDto != null && mainResponseDto.getErr() != null) {
					regProcLogger.error(LoggerFileConstant.REGISTRATIONID.toString(), registrationIds.toString(),
							PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
							mainResponseDto.getErr().toString());
					isTransactionSuccessful = false;
					description = "Internal error occured while processing reverse data sync for "
							+ preRegistrationIds.size() + " preRegistrationIds" + mainResponseDto.getErr().toString();

				}

			}

		} catch (TablenotAccessibleException e) {
			object.setInternalError(Boolean.TRUE);
			sendMessage(mosipEventBus, object);
			regProcLogger.error(LoggerFileConstant.REGISTRATIONID.toString(), registrationIds.toString(),
					PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage(), e.toString());
			isTransactionSuccessful = false;
			description = "Registration status table not accessible for any of these " + registrationIds.size()
					+ " registrationIds";

		} catch (ApisResourceAccessException e) {

			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();

				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationIds.toString(),
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
						httpClientException.getResponseBodyAsString() + ExceptionUtils.getStackTrace(e));
				isTransactionSuccessful = false;
				description = "HttpClientErrorException while processing reverse data sync "
						+ httpClientException.getResponseBodyAsString();
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationIds.toString(),
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
						httpServerException.getResponseBodyAsString() + ExceptionUtils.getStackTrace(e));
				isTransactionSuccessful = false;
				description = "HttpServerErrorException while processing reverse data sync "
						+ httpServerException.getResponseBodyAsString();
			} else {

				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationIds.toString(),
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(), e.getMessage());
				isTransactionSuccessful = false;
				description = "Internal error occured while processing reverse data sync " + e.getMessage();

			}

		} finally {
			String eventId = "";
			String eventName = "";
			String eventType = "";
			if (isTransactionSuccessful) {
				eventId = EventId.RPR_402.toString();
				eventName = EventName.UPDATE.toString();
				eventType = EventType.BUSINESS.toString();
			} else {

				eventId = EventId.RPR_405.toString();
				eventName = EventName.EXCEPTION.toString();
				eventType = EventType.SYSTEM.toString();
			}
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationIds.isEmpty() ? null : registrationIds.toString());
		}

		return object;
	}

	/**
	 * Sets the applicant.
	 *
	 * @param identity
	 *            the identity
	 * @param registrationStatusDto
	 *            the registration status dto
	 */
	private void setApplicant(Identity identity, InternalRegistrationStatusDto registrationStatusDto) {
		IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
		String applicantType = identityIteratorUtil.getFieldValue(identity.getMetaData(), APPLICANT_TYPE);
		registrationStatusDto.setApplicantType(applicantType);

	}

}
