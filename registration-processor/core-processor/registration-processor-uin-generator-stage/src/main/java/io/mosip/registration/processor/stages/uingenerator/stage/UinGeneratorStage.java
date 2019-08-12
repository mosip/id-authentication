package io.mosip.registration.processor.stages.uingenerator.stage;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.EventId;
import io.mosip.registration.processor.core.constant.EventName;
import io.mosip.registration.processor.core.constant.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.RequestWrapper;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.Documents;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.status.util.StatusUtil;
import io.mosip.registration.processor.core.status.util.TrimExceptionMessage;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.idreposervice.IdRepoService;
import io.mosip.registration.processor.packet.storage.entity.RegLostUinDetEntity;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.uingenerator.constants.UINConstants;
import io.mosip.registration.processor.stages.uingenerator.dto.UinDto;
import io.mosip.registration.processor.stages.uingenerator.dto.UinGenResponseDto;
import io.mosip.registration.processor.stages.uingenerator.dto.UinRequestDto;
import io.mosip.registration.processor.stages.uingenerator.dto.UinResponseDto;
import io.mosip.registration.processor.stages.uingenerator.dto.VidRequestDto;
import io.mosip.registration.processor.stages.uingenerator.dto.VidResponseDto;
import io.mosip.registration.processor.stages.uingenerator.exception.VidCreationException;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.IdRequestDto;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.RequestDto;
import io.mosip.registration.processor.stages.uingenerator.util.UinStatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class UinGeneratorStage.
 * 
 * @author Ranjitha Siddegowda
 * @author Rishabh Keshari
 */
@Service
public class UinGeneratorStage extends MosipVerticleAPIManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(UinGeneratorStage.class);

	@Autowired
	private Environment env;

	@Autowired
	private IdRepoService idRepoService;

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;

	@Value("${registration.processor.id.repo.vidType}")
	private String vidType;

	/** The cluster manager url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** The id repo create. */
	@Value("${registration.processor.id.repo.create}")
	private String idRepoCreate;

	/** The id repo update. */
	@Value("${registration.processor.id.repo.update}")
	private String idRepoUpdate;

	/** server port number. */
	@Value("${server.port}")
	private String port;

	/** The adapter. */
	@Autowired
	private PacketManager adapter;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** Mosip router for APIs */
	@Autowired
	MosipRouter router;

	/** The registration processor rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	/** The demographic dedupe repository. */
	@Autowired
	private BasePacketRepository<RegLostUinDetEntity, String> regLostUinDetEntity;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The utility. */
	@Autowired
	private Utilities utility;

	@Autowired
	RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	@Autowired
	ABISHandlerUtil aBISHandlerUtil;
	
	private TrimExceptionMessage trimExceptionMessage = new TrimExceptionMessage();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MessageDTO process(MessageDTO object) {
		boolean isTransactionSuccessful = Boolean.FALSE;
		object.setMessageBusAddress(MessageBusAddress.UIN_GENERATION_BUS_IN);
		object.setInternalError(Boolean.FALSE);
		object.setIsValid(true);
		LogDescription description = new LogDescription();
		String registrationId = object.getRid();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "UinGeneratorStage::process()::entry");
		UinGenResponseDto uinResponseDto = null;

		InternalRegistrationStatusDto registrationStatusDto = null;
		registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);
		try {
			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.UIN_GENERATOR.toString());
			registrationStatusDto.setRegistrationStageName(this.getClass().getSimpleName());

			if ((RegistrationType.LOST.toString()).equalsIgnoreCase(object.getReg_type().name())) {
				String lostPacketRegId = object.getRid();
				String matchedRegId = regLostUinDetEntity.getLostUinMatchedRegId(lostPacketRegId);
				if (matchedRegId != null) {
					linkRegIdWrtUin(lostPacketRegId, matchedRegId, object, description);
				}

			} else {

				IdResponseDTO idResponseDTO = new IdResponseDTO();
				InputStream idJsonStream = adapter.getFile(registrationId,
						PacketFiles.DEMOGRAPHIC.name() + UINConstants.FILE_SEPARATOR + PacketFiles.ID.name());
				byte[] idJsonBytes = IOUtils.toByteArray(idJsonStream);
				String getJsonStringFromBytes = new String(idJsonBytes);
				JSONObject identityJson = (JSONObject) JsonUtil.objectMapperReadValue(getJsonStringFromBytes,
						JSONObject.class);
				JSONObject demographicIdentity = JsonUtil.getJSONObject(identityJson,
						utility.getGetRegProcessorDemographicIdentity());
				Number number = JsonUtil.getJSONValue(demographicIdentity, UINConstants.UIN);
				Long uinFieldCheck = number != null ? number.longValue() : null;
				if (uinFieldCheck == null) {

					String test = (String) registrationProcessorRestClientService.getApi(ApiName.UINGENERATOR, null, "",
							"", String.class);

					Gson gsonObj = new Gson();
					uinResponseDto = gsonObj.fromJson(test, UinGenResponseDto.class);

					long uinInLong = Long.parseLong(uinResponseDto.getResponse().getUin());
					demographicIdentity.put("UIN", uinInLong);

					idResponseDTO = sendIdRepoWithUin(registrationId, demographicIdentity,
							uinResponseDto.getResponse().getUin(), description);
					if (idResponseDTO != null && idResponseDTO.getResponse() != null) {
						generateVid(registrationId, uinResponseDto.getResponse().getUin());
						registrationStatusDto.setStatusComment(StatusUtil.UIN_GENERATED_SUCCESS.getMessage());
						registrationStatusDto.setSubStatusCode(StatusUtil.UIN_GENERATED_SUCCESS.getCode());
						sendResponseToUinGenerator(registrationId, uinResponseDto.getResponse().getUin(),
								UINConstants.UIN_ASSIGNED);
						isTransactionSuccessful = true;
						registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSED.toString());
						description.setMessage(StatusUtil.UIN_GENERATED_SUCCESS.getMessage() + registrationId);
						registrationStatusDto
								.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.PROCESSED.toString());

					} else {
						String statusComment = idResponseDTO.getErrors() != null
								? idResponseDTO.getErrors().get(0).getMessage()
								: UINConstants.NULL_IDREPO_RESPONSE;
						registrationStatusDto.setStatusComment(trimExceptionMessage.trimExceptionMessage(StatusUtil.UIN_GENERATION_FAILED.getMessage() + statusComment));
						registrationStatusDto.setSubStatusCode(StatusUtil.UIN_GENERATION_FAILED.getCode());
						object.setInternalError(Boolean.TRUE);
						registrationStatusDto.setStatusCode(RegistrationStatusCode.REJECTED.toString());
						registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
								.getStatusCode(RegistrationExceptionTypeCode.PACKET_UIN_GENERATION_FAILED));
						sendResponseToUinGenerator(registrationId, uinResponseDto.getResponse().getUin(),
								UINConstants.UIN_UNASSIGNED);
						isTransactionSuccessful = false;
						description.setMessage(UINConstants.UIN_FAILURE + registrationId + "::" + statusComment);
						String idres = idResponseDTO != null ? idResponseDTO.toString()
								: UINConstants.NULL_IDREPO_RESPONSE;

						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
								statusComment + "  :  " + idres);
						object.setIsValid(false);
					}
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString() + registrationId, "Response from IdRepo API",
							"is :" + idResponseDTO.toString());
				} else {
					if ((RegistrationType.ACTIVATED.toString()).equalsIgnoreCase(object.getReg_type().toString())) {
						isTransactionSuccessful = reActivateUin(idResponseDTO, registrationId, uinFieldCheck, object,
								demographicIdentity, description);
					} else if ((RegistrationType.DEACTIVATED.toString())
							.equalsIgnoreCase(object.getReg_type().toString())) {
						idResponseDTO = deactivateUin(registrationId, uinFieldCheck, object, demographicIdentity,
								description);
					} else if (RegistrationType.UPDATE.toString().equalsIgnoreCase(object.getReg_type().toString())
							|| (RegistrationType.RES_UPDATE.toString()
									.equalsIgnoreCase(object.getReg_type().toString()))) {
						isTransactionSuccessful = uinUpdate(registrationId, uinFieldCheck, object, demographicIdentity,
								description);
					}
				}

			}
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, description.getMessage());
			registrationStatusDto.setUpdatedBy(UINConstants.USER);

		} catch (FSAdapterException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto
					.setStatusComment(trimExceptionMessage.trimExceptionMessage(StatusUtil.FS_ADAPTER_EXCEPTION.getMessage() + e.getMessage()));
			registrationStatusDto.setSubStatusCode(StatusUtil.FS_ADAPTER_EXCEPTION.getCode());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_UGS_PACKET_STORE_NOT_ACCESSIBLE.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			isTransactionSuccessful = false;
			description.setMessage("FileSytem is not accessible for packet " + registrationId + "::" + e.getMessage());
			object.setIsValid(Boolean.FALSE);
			object.setRid(registrationId);
		} catch (ApisResourceAccessException ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto.setStatusComment(trimExceptionMessage.trimExceptionMessage(StatusUtil.API_RESOUCE_ACCESS_FAILED.getMessage() + ex.getMessage()));
			registrationStatusDto.setSubStatusCode(StatusUtil.API_RESOUCE_ACCESS_FAILED.getCode());
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					RegistrationStatusCode.FAILED.toString() + ex.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
			description.setMessage("Internal error occurred in UINGenerator stage while processing registrationId "
					+ registrationId + "::" + ex.getMessage());

		} catch (IOException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(trimExceptionMessage.trimExceptionMessage(StatusUtil.IO_EXCEPTION.getMessage() + e.getMessage()));
			registrationStatusDto.setSubStatusCode(StatusUtil.IO_EXCEPTION.getCode());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
			description.setMessage("Internal error in UINGenerator stage while processing registrationId "
					+ registrationId + e.getMessage());
		} catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(trimExceptionMessage.trimExceptionMessage(StatusUtil.UNKNOWN_EXCEPTION_OCCURED.getMessage()));
			registrationStatusDto.setSubStatusCode(StatusUtil.UNKNOWN_EXCEPTION_OCCURED.getCode());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					RegistrationStatusCode.FAILED.toString() + ex.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
			description.setMessage("Internal error occurred in UINGenerator stage while processing registrationId "
					+ registrationId + ex.getMessage());
		} finally {
			if (description.getStatusComment() != null)
				registrationStatusDto.setStatusComment(description.getStatusComment());
			if (description.getCode() != null)
				registrationStatusDto.setStatusCode(description.getCode());

			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					registrationId, ApiName.AUDIT);

		}

		return object;
	}

	/**
	 * Send id repo with uin.
	 *
	 * @param regId
	 *            the reg id
	 * @param uin
	 *            the uin
	 * @param description
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws VidCreationException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws PacketDecryptionFailureException
	 * @throws Exception
	 */
	private IdResponseDTO sendIdRepoWithUin(String regId, JSONObject demographicIdentity, String uin,
			LogDescription description)
			throws ApisResourceAccessException, JsonParseException, JsonMappingException, IOException,
			VidCreationException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {

		List<Documents> documentInfo = getAllDocumentsByRegId(regId);
		RequestDto requestDto = new RequestDto();
		requestDto.setIdentity(demographicIdentity);
		requestDto.setDocuments(documentInfo);
		requestDto.setRegistrationId(regId);
		requestDto.setStatus(RegistrationType.ACTIVATED.toString());
		requestDto.setBiometricReferenceId(uin);

		IdResponseDTO result = null;
		IdRequestDto idRequestDTO = new IdRequestDto();
		idRequestDTO.setId(idRepoCreate);
		idRequestDTO.setRequest(requestDto);
		idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
		idRequestDTO.setVersion(UINConstants.idRepoApiVersion);
		idRequestDTO.setMetadata(null);

		try {

			result = (IdResponseDTO) registrationProcessorRestClientService.postApi(ApiName.IDREPOSITORY, "", "",
					idRequestDTO, IdResponseDTO.class);

		} catch (ApisResourceAccessException e) {

			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				description.setMessage(UINConstants.UIN_GENERATION_FAILED + regId + "::"
						+ httpClientException.getResponseBodyAsString());
				throw new ApisResourceAccessException(httpClientException.getResponseBodyAsString(),
						httpClientException);
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				description.setMessage(UINConstants.UIN_GENERATION_FAILED + regId + "::"
						+ httpServerException.getResponseBodyAsString());

				throw new ApisResourceAccessException(httpServerException.getResponseBodyAsString(),
						httpServerException);
			} else {
				description.setMessage(UINConstants.UIN_GENERATION_FAILED + regId + "::" + e.getMessage());
				throw e;
			}

		}
		return result;

	}

	/**
	 * Gets the all documents by reg id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the all documents by reg id
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws ApisResourceAccessException
	 * @throws PacketDecryptionFailureException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private List<Documents> getAllDocumentsByRegId(String regId) throws IOException, PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		List<Documents> applicantDocuments = new ArrayList<>();

		JSONObject idJSON = getDemoIdentity(regId);
		RegistrationProcessorIdentity regProcessorIdentityJson = getMappeedJSONIdentity();
		String proofOfAddressLabel = regProcessorIdentityJson.getIdentity().getPoa().getValue();
		String proofOfDateOfBirthLabel = regProcessorIdentityJson.getIdentity().getPob().getValue();
		String proofOfIdentityLabel = regProcessorIdentityJson.getIdentity().getPoi().getValue();
		String proofOfRelationshipLabel = regProcessorIdentityJson.getIdentity().getPor().getValue();
		String applicantBiometricLabel = regProcessorIdentityJson.getIdentity().getIndividualBiometrics().getValue();

		JSONObject proofOfAddress = JsonUtil.getJSONObject(idJSON, proofOfAddressLabel);
		JSONObject proofOfDateOfBirth = JsonUtil.getJSONObject(idJSON, proofOfDateOfBirthLabel);
		JSONObject proofOfIdentity = JsonUtil.getJSONObject(idJSON, proofOfIdentityLabel);
		JSONObject proofOfRelationship = JsonUtil.getJSONObject(idJSON, proofOfRelationshipLabel);
		JSONObject applicantBiometric = JsonUtil.getJSONObject(idJSON, applicantBiometricLabel);
		if (proofOfAddress != null) {
			applicantDocuments
					.add(getIdDocumnet(regId, PacketFiles.DEMOGRAPHIC.name(), proofOfAddress, proofOfAddressLabel));
		}
		if (proofOfDateOfBirth != null) {
			applicantDocuments.add(
					getIdDocumnet(regId, PacketFiles.DEMOGRAPHIC.name(), proofOfDateOfBirth, proofOfDateOfBirthLabel));
		}
		if (proofOfIdentity != null) {
			applicantDocuments
					.add(getIdDocumnet(regId, PacketFiles.DEMOGRAPHIC.name(), proofOfIdentity, proofOfIdentityLabel));
		}
		if (proofOfRelationship != null) {
			applicantDocuments.add(getIdDocumnet(regId, PacketFiles.DEMOGRAPHIC.name(), proofOfRelationship,
					proofOfRelationshipLabel));
		}
		if (applicantBiometric != null) {
			applicantDocuments.add(
					getIdDocumnet(regId, PacketFiles.BIOMETRIC.name(), applicantBiometric, applicantBiometricLabel));
		}
		return applicantDocuments;
	}

	private Documents getIdDocumnet(String registrationId, String folderPath, JSONObject idDocObj, String idDocLabel)
			throws IOException, PacketDecryptionFailureException, ApisResourceAccessException,
			io.mosip.kernel.core.exception.IOException {
		Documents documentsInfoDto = new Documents();
		InputStream poiStream = adapter.getFile(registrationId,
				folderPath + UINConstants.FILE_SEPARATOR + idDocObj.get("value"));
		documentsInfoDto.setValue(CryptoUtil.encodeBase64(IOUtils.toByteArray(poiStream)));
		documentsInfoDto.setCategory(idDocLabel);
		return documentsInfoDto;
	}

	/**
	 * Update id repo wit uin.
	 *
	 * @param regId
	 *            the reg id
	 * @param uin
	 *            the uin
	 * @param object
	 *            the object
	 * @param description
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RegistrationProcessorCheckedException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws PacketDecryptionFailureException
	 */
	private boolean uinUpdate(String regId, Long uin, MessageDTO object, JSONObject demographicIdentity,
			LogDescription description)
			throws ApisResourceAccessException, IOException, RegistrationProcessorCheckedException,
			PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		IdResponseDTO result;
		boolean isTransactionSuccessful = Boolean.FALSE;
		List<Documents> documentInfo = utility.getAllDocumentsByRegId(regId);
		result = idRepoRequestBuilder(RegistrationType.ACTIVATED.toString().toUpperCase(), regId, documentInfo,
				demographicIdentity);
		if (result != null && result.getResponse() != null) {

			if ((RegistrationType.ACTIVATED.toString().toUpperCase())
					.equalsIgnoreCase(result.getResponse().getStatus())) {
				isTransactionSuccessful = true;
				description.setCode(RegistrationStatusCode.PROCESSED.toString());
				description.setStatusComment(StatusUtil.UIN_DATA_UPDATION_SUCCESS.getMessage());
				description.setSubStatusCode(StatusUtil.UIN_DATA_UPDATION_SUCCESS.getCode());
				description.setMessage(StatusUtil.UIN_DATA_UPDATION_SUCCESS.getMessage() +  " for registration Id: " + regId);
				object.setIsValid(Boolean.TRUE);
			}
		} else {
			String statusComment = result != null && result.getErrors() != null ? result.getErrors().get(0).getMessage()
					: UINConstants.NULL_IDREPO_RESPONSE;
			description.setCode(RegistrationStatusCode.FAILED.toString());
			description.setStatusComment(trimExceptionMessage.trimExceptionMessage(StatusUtil.UIN_DATA_UPDATION_FAILED.getMessage() + statusComment));
			description.setSubStatusCode(StatusUtil.UIN_DATA_UPDATION_FAILED.getCode());
			description
					.setMessage(UINConstants.UIN_FAILURE + regId + "::" + result != null && result.getErrors() != null
							? result.getErrors().get(0).getMessage()
							: UINConstants.NULL_IDREPO_RESPONSE);
			object.setIsValid(Boolean.FALSE);
		}
		return isTransactionSuccessful;
	}

	/**
	 * Id repo request builder.
	 *
	 * @param status
	 *            the status
	 * @param regId
	 *            the reg id
	 * @param uin
	 *            the uin
	 * @param documentInfo
	 *            the document info
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 */
	private IdResponseDTO idRepoRequestBuilder(String status, String regId, List<Documents> documentInfo,
			JSONObject demographicIdentity) throws ApisResourceAccessException, IOException {
		IdResponseDTO idResponseDto;
		List<String> pathsegments = new ArrayList<>();
		RequestDto requestDto = new RequestDto();

		if (documentInfo != null) {
			requestDto.setDocuments(documentInfo);
		}

		requestDto.setRegistrationId(regId);
		requestDto.setStatus(status);
		requestDto.setIdentity(demographicIdentity);

		// pathsegments.add(Long.toString(uin));

		IdRequestDto idRequestDTO = new IdRequestDto();
		idRequestDTO.setId(idRepoUpdate);
		idRequestDTO.setMetadata(null);
		idRequestDTO.setRequest(requestDto);
		idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
		idRequestDTO.setVersion(UINConstants.idRepoApiVersion);

		idResponseDto = (IdResponseDTO) registrationProcessorRestClientService.patchApi(ApiName.IDREPOSITORY,
				pathsegments, "", "", idRequestDTO, IdResponseDTO.class);

		return idResponseDto;
	}

	/**
	 * Re activate uin.
	 *
	 * @param regId
	 *            the reg id
	 * @param uin
	 *            the uin
	 * @param object
	 *            the object
	 * @param description
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 */
	private boolean reActivateUin(IdResponseDTO idResponseDTO, String regId, Long uin, MessageDTO object,
			JSONObject demographicIdentity, LogDescription description)
			throws ApisResourceAccessException, IOException {
		IdResponseDTO result = getIdRepoDataByUIN(uin, regId, description);
		List<String> pathsegments = new ArrayList<>();
		RequestDto requestDto = new RequestDto();
		boolean isTransactionSuccessful = Boolean.FALSE;

		if (result != null && result.getResponse() != null) {

			if ((RegistrationType.ACTIVATED.toString()).equalsIgnoreCase(result.getResponse().getStatus())) {

				description.setCode(RegistrationStatusCode.FAILED.toString());
				description.setStatusComment(StatusUtil.UIN_ALREADY_ACTIVATED.getMessage());
				description.setSubStatusCode(StatusUtil.UIN_ALREADY_ACTIVATED.getCode());
				description.setMessage(StatusUtil.UIN_ALREADY_ACTIVATED.getMessage()  + regId);
				object.setIsValid(Boolean.FALSE);
				return isTransactionSuccessful;

			} else {

				requestDto.setRegistrationId(regId);
				requestDto.setStatus(RegistrationType.ACTIVATED.toString());
				requestDto.setBiometricReferenceId(Long.toString(uin));
				requestDto.setIdentity(demographicIdentity);

				IdRequestDto idRequestDTO = new IdRequestDto();
				idRequestDTO.setId(idRepoUpdate);
				idRequestDTO.setRequest(requestDto);
				idRequestDTO.setMetadata(null);
				idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
				idRequestDTO.setVersion(UINConstants.idRepoApiVersion);

				result = (IdResponseDTO) registrationProcessorRestClientService.patchApi(ApiName.IDREPOSITORY,
						pathsegments, "", "", idRequestDTO, IdResponseDTO.class);

				if (result != null && result.getResponse() != null) {

					if ((RegistrationType.ACTIVATED.toString()).equalsIgnoreCase(result.getResponse().getStatus())) {
						isTransactionSuccessful = true;
						description.setCode(RegistrationStatusCode.PROCESSED.toString());
						description.setStatusComment(StatusUtil.UIN_ACTIVATED_SUCCESS.getMessage());
						description.setSubStatusCode(StatusUtil.UIN_ACTIVATED_SUCCESS.getCode());
						description.setMessage(StatusUtil.UIN_ACTIVATED_SUCCESS.getMessage()+ regId);
						object.setIsValid(Boolean.TRUE);
					} else {
						description.setCode(RegistrationStatusCode.FAILED.toString());
						description.setStatusComment(StatusUtil.UIN_ACTIVATED_FAILED.getMessage());
						description.setSubStatusCode(StatusUtil.UIN_ACTIVATED_FAILED.getCode());
						description.setMessage(StatusUtil.UIN_ACTIVATED_FAILED.getMessage() + regId);
						object.setIsValid(Boolean.FALSE);
					}
				} else {
					String statusComment = result != null && result.getErrors() != null
							? result.getErrors().get(0).getMessage()
							: UINConstants.NULL_IDREPO_RESPONSE;
					description.setCode(RegistrationStatusCode.FAILED.toString());
					description.setStatusComment(trimExceptionMessage.trimExceptionMessage(StatusUtil.UIN_REACTIVATION_FAILED.getMessage() + statusComment));
					description.setSubStatusCode(StatusUtil.UIN_REACTIVATION_FAILED.getCode());
					description.setMessage(
							UINConstants.UIN_FAILURE + regId + "::" + result != null && result.getErrors() != null
									? result.getErrors().get(0).getMessage()
									: UINConstants.NULL_IDREPO_RESPONSE);
					object.setIsValid(Boolean.FALSE);
				}

			}

		}

		return isTransactionSuccessful;
	}

	/**
	 * Deactivate uin.
	 *
	 * @param regId
	 *            the reg id
	 * @param uin
	 *            the uin
	 * @param object
	 *            the object
	 * @param description
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 * @throws IOException
	 */
	private IdResponseDTO deactivateUin(String regId, Long uin, MessageDTO object, JSONObject demographicIdentity,
			LogDescription description) throws ApisResourceAccessException, IOException {
		IdResponseDTO idResponseDto;
		List<String> pathsegments = new ArrayList<>();
		RequestDto requestDto = new RequestDto();
		String statusComment = "";

		idResponseDto = getIdRepoDataByUIN(uin, regId, description);

		if (idResponseDto.getResponse() != null
				&& idResponseDto.getResponse().getStatus().equalsIgnoreCase(RegistrationType.DEACTIVATED.toString())) {
			description.setCode(RegistrationStatusCode.FAILED.toString());
			description.setStatusComment(StatusUtil.UIN_ALREADY_DEACTIVATED.getMessage());
			description.setSubStatusCode(StatusUtil.UIN_ALREADY_DEACTIVATED.getCode());
			description.setMessage(StatusUtil.UIN_ALREADY_DEACTIVATED.getMessage() + regId);
			object.setIsValid(Boolean.FALSE);
			return idResponseDto;

		} else {
			requestDto.setRegistrationId(regId);
			requestDto.setStatus(RegistrationType.DEACTIVATED.toString());
			requestDto.setIdentity(demographicIdentity);
			requestDto.setBiometricReferenceId(Long.toString(uin));

			IdRequestDto idRequestDTO = new IdRequestDto();
			idRequestDTO.setId(idRepoUpdate);
			idRequestDTO.setMetadata(null);
			idRequestDTO.setRequest(requestDto);
			idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
			idRequestDTO.setVersion(UINConstants.idRepoApiVersion);

			idResponseDto = (IdResponseDTO) registrationProcessorRestClientService.patchApi(ApiName.IDREPOSITORY,
					pathsegments, "", "", idRequestDTO, IdResponseDTO.class);

			if (idResponseDto != null && idResponseDto.getResponse() != null) {
				if (idResponseDto.getResponse().getStatus().equalsIgnoreCase(RegistrationType.DEACTIVATED.toString())) {
					description.setCode(RegistrationStatusCode.PROCESSED.toString());
					description.setStatusComment(StatusUtil.UIN_DEACTIVATION_SUCCESS.getMessage());
					description.setSubStatusCode(StatusUtil.UIN_DEACTIVATION_SUCCESS.getCode());
					description.setMessage(StatusUtil.UIN_DEACTIVATION_SUCCESS.getMessage() + regId);
					object.setIsValid(Boolean.TRUE);
					statusComment = idResponseDto.getResponse().getStatus().toString();

				}
			} else {

				statusComment = idResponseDto != null && idResponseDto.getErrors() != null
						? idResponseDto.getErrors().get(0).getMessage()
						: UINConstants.NULL_IDREPO_RESPONSE;
				description.setCode(RegistrationStatusCode.FAILED.toString());
				description.setStatusComment(trimExceptionMessage.trimExceptionMessage(StatusUtil.UIN_DEACTIVATION_FAILED.getMessage() + statusComment));
				description.setSubStatusCode(StatusUtil.UIN_DEACTIVATION_FAILED.getCode());
				description.setMessage(statusComment);
				object.setIsValid(Boolean.FALSE);
			}

		}
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
				LoggerFileConstant.REGISTRATIONID.toString() + regId, "Updated Response from IdRepo API",
				"is : " + statusComment);

		return idResponseDto;
	}

	/**
	 * Gets the id repo data by UIN.
	 *
	 * @param uin
	 *            the uin
	 * @param description
	 * @return the id repo data by UIN
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 */
	private IdResponseDTO getIdRepoDataByUIN(Long uin, String regId, LogDescription description)
			throws ApisResourceAccessException, IOException {
		IdResponseDTO response;

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(Long.toString(uin));
		try {

			response = (IdResponseDTO) registrationProcessorRestClientService.getApi(ApiName.IDREPOGETIDBYUIN,
					pathsegments, "", "", IdResponseDTO.class);

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				description.setMessage(UINConstants.UIN_GENERATION_FAILED + regId + "::"
						+ httpClientException.getResponseBodyAsString());
				throw new ApisResourceAccessException(httpClientException.getResponseBodyAsString(),
						httpClientException);
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				description.setMessage(UINConstants.UIN_GENERATION_FAILED + regId + "::"
						+ httpServerException.getResponseBodyAsString());

				throw new ApisResourceAccessException(httpServerException.getResponseBodyAsString(),
						httpServerException);
			} else {
				description.setMessage(UINConstants.UIN_GENERATION_FAILED + regId + "::" + e.getMessage());
				throw e;
			}
		}
		return response;
	}

	/**
	 * Send response to uin generator.
	 *
	 * @param uin
	 *            the uin
	 * @param uinStatus
	 *            the uin status
	 * @throws ApisResourceAccessException
	 * @throws IOException
	 * @throws VidCreationException
	 */
	private void sendResponseToUinGenerator(String registrationId, String uin, String uinStatus)
			throws ApisResourceAccessException, IOException {
		UinRequestDto uinRequest = new UinRequestDto();
		UinResponseDto uinDto = new UinResponseDto();
		uinDto.setUin(uin);
		uinDto.setStatus(uinStatus);
		uinRequest.setRequest(uinDto);
		String jsonString = null;
		ObjectMapper objMapper = new ObjectMapper();
		try {
			jsonString = objMapper.writeValueAsString(uinRequest);
			String response;

			response = (String) registrationProcessorRestClientService.putApi(ApiName.UINGENERATOR, null, "", "",
					jsonString, String.class, MediaType.APPLICATION_JSON);

			Gson gsonValue = new Gson();
			UinDto uinresponse = gsonValue.fromJson(response, UinDto.class);

			if (uinresponse.getResponse() != null) {
				String uinSuccessDescription = "Kernel service called successfully to update the uin status as assigned";

				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString() + registrationId, "Success",
						uinSuccessDescription);
			} else {
				String uinErrorDescription = "Kernel service called successfully to update the uin status as unassigned";
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString() + registrationId, "Failure",
						"is : " + uinErrorDescription);
			}
		} catch (JsonProcessingException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw e;
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PVM_API_RESOUCE_ACCESS_FAILED.getMessage()
							+ e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw e;
		}

	}

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {

		mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.UIN_GENERATION_BUS_IN,
				MessageBusAddress.UIN_GENERATION_BUS_OUT);
	}

	@Override
	public void start() {
		router.setRoute(this.postUrl(mosipEventBus.getEventbus(), MessageBusAddress.UIN_GENERATION_BUS_IN,
				MessageBusAddress.UIN_GENERATION_BUS_OUT));
		this.createServer(router.getRouter(), Integer.parseInt(port));

	}

	private RegistrationProcessorIdentity getMappeedJSONIdentity() throws IOException {
		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorIdentityJson());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		return mapIdentityJsonStringToObject.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);

	}

	private JSONObject getDemoIdentity(String registrationId) throws IOException, PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		InputStream documentInfoStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + UINConstants.FILE_SEPARATOR + PacketFiles.ID.name());

		byte[] bytes = IOUtils.toByteArray(documentInfoStream);
		String demographicJsonString = new String(bytes);
		JSONObject demographicJson = JsonUtil.objectMapperReadValue(demographicJsonString, JSONObject.class);
		JSONObject demographicIdentity1 = JsonUtil.getJSONObject(demographicJson,
				utility.getGetRegProcessorDemographicIdentity());
		if (demographicIdentity1 == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		return demographicIdentity1;
	}

	@SuppressWarnings("unchecked")
	private void generateVid(String registrationId, String UIN)
			throws ApisResourceAccessException, IOException, VidCreationException {
		ObjectMapper mapper = new ObjectMapper();
		VidRequestDto vidRequestDto = new VidRequestDto();
		VidResponseDto vidResponseDto = new VidResponseDto();
		RequestWrapper<VidRequestDto> request = new RequestWrapper<>();
		ResponseWrapper<VidResponseDto> response = new ResponseWrapper<>();
		try {

			vidRequestDto.setUIN(UIN);
			vidRequestDto.setVidType(vidType);
			request.setId(env.getProperty(UINConstants.VID_CREATE_ID));
			request.setRequest(vidRequestDto);
			DateTimeFormatter format = DateTimeFormatter.ofPattern(env.getProperty(UINConstants.DATETIME_PATTERN));
			LocalDateTime localdatetime = LocalDateTime.parse(
					DateUtils.getUTCCurrentDateTimeString(env.getProperty(UINConstants.DATETIME_PATTERN)), format);
			request.setRequesttime(localdatetime);
			request.setVersion(env.getProperty(UINConstants.REG_PROC_APPLICATION_VERSION));

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					"UinGeneratorStage::generateVid():: post CREATEVID service call started with request data : "
							+ JsonUtil.objectMapperObjectToJson(vidRequestDto));

			response = (ResponseWrapper<VidResponseDto>) registrationProcessorRestClientService
					.postApi(ApiName.CREATEVID, "", "", request, ResponseWrapper.class);

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					"UinGeneratorStage::generateVid():: post CREATEVID service call ended successfully");

			if (!response.getErrors().isEmpty()) {
				throw new VidCreationException(PlatformErrorMessages.RPR_UGS_VID_EXCEPTION.getMessage(),
						"VID creation exception");

			}

		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_UGS_API_RESOURCE_EXCEPTION.getMessage() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw e;
		}
	}

	/**
	 * Link reg id wrt uin.
	 *
	 * @param lostPacketRegId
	 *            the lost packet reg id
	 * @param matchedRegId
	 *            the matched reg id
	 * @param object
	 *            the object
	 * @param description
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	private IdResponseDTO linkRegIdWrtUin(String lostPacketRegId, String matchedRegId, MessageDTO object,
			LogDescription description) throws ApisResourceAccessException, IOException {

		IdResponseDTO idResponse = null;
		Number number = idRepoService.getUinByRid(matchedRegId, utility.getGetRegProcessorDemographicIdentity());

		Long uinFieldValue = number != null ? number.longValue() : null;
		RequestDto requestDto = new RequestDto();
		String statusComment = "";

		if (uinFieldValue != null) {

			JSONObject identityObject = new JSONObject();
			identityObject.put(UINConstants.UIN, uinFieldValue);

			requestDto.setRegistrationId(lostPacketRegId);
			requestDto.setIdentity(identityObject);

			IdRequestDto idRequestDTO = new IdRequestDto();
			idRequestDTO.setId(idRepoUpdate);
			idRequestDTO.setRequest(requestDto);
			idRequestDTO.setMetadata(null);
			idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
			idRequestDTO.setVersion(UINConstants.idRepoApiVersion);

			idResponse = (IdResponseDTO) registrationProcessorRestClientService.patchApi(ApiName.IDREPOSITORY, null, "",
					"", idRequestDTO, IdResponseDTO.class);

			if (idResponse != null && idResponse.getResponse() != null) {
				description.setCode(RegistrationStatusCode.PROCESSED.toString());
				description.setStatusComment(StatusUtil.LINK_RID_FOR_LOST_PACKET_SUCCESS.getMessage());
				description.setSubStatusCode(StatusUtil.LINK_RID_FOR_LOST_PACKET_SUCCESS.getCode());
				description.setMessage(UinStatusMessage.PACKET_LOST_UIN_UPDATION_SUCCESS_MSG + lostPacketRegId);
				description.setTransactionStatusCode(RegistrationTransactionStatusCode.PROCESSED.toString());
				object.setIsValid(Boolean.TRUE);

				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString() + lostPacketRegId,
						" UIN LINKED WITH " + matchedRegId, "is : " + description);
			} else {

				statusComment = idResponse != null && idResponse.getErrors() != null
						&& idResponse.getErrors().get(0) != null ? idResponse.getErrors().get(0).getMessage()
								: UinStatusMessage.PACKET_LOST_UIN_UPDATION_FAILURE_MSG + "  "
										+ UINConstants.NULL_IDREPO_RESPONSE + "for lostPacketRegId " + lostPacketRegId;
				description.setCode(RegistrationStatusCode.PROCESSING.toString());
				description.setStatusComment(StatusUtil.LINK_RID_FOR_LOST_PACKET_SUCCESS.getMessage() + statusComment);
				description.setSubStatusCode(StatusUtil.LINK_RID_FOR_LOST_PACKET_SUCCESS.getCode());
				description.setTransactionStatusCode(registrationStatusMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.PACKET_UIN_GENERATION_ID_REPO_ERROR));
				if (UINConstants.UIN_FAILURE + lostPacketRegId + "::" + idResponse != null
						&& idResponse.getErrors() != null)
					description.setMessage(idResponse.getErrors().get(0).getMessage());
				else
					description.setMessage(UINConstants.NULL_IDREPO_RESPONSE);
				object.setIsValid(Boolean.FALSE);
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString() + lostPacketRegId,
						" UIN NOT LINKED WITH " + matchedRegId, "is : " + statusComment);
			}

		} else {
			statusComment = UinStatusMessage.PACKET_LOST_UIN_UPDATION_FAILURE_MSG + "  "
					+ UINConstants.NULL_IDREPO_RESPONSE + " UIN not available for matchedRegId " + matchedRegId;
			description.setStatusComment(StatusUtil.LINK_RID_FOR_LOST_PACKET_FAILED.getMessage());
			description.setSubStatusCode(StatusUtil.LINK_RID_FOR_LOST_PACKET_FAILED.getCode());
			description.setCode(RegistrationStatusCode.REJECTED.toString());
			description.setTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.PACKET_UIN_GENERATION_FAILED));
			description.setMessage(UinStatusMessage.PACKET_LOST_UIN_UPDATION_FAILURE_MSG + "  "
					+ UINConstants.NULL_IDREPO_RESPONSE + " UIN not available for matchedRegId " + matchedRegId);

			object.setIsValid(Boolean.FALSE);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString() + lostPacketRegId,
					" UIN NOT LINKED WITH " + matchedRegId, "is : " + statusComment);
		}

		return idResponse;
	}
}
