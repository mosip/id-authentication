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
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
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
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.RequestWrapper;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.Documents;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.idreposervice.IdRepoService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.RegLostUinDetEntity;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
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

	private static final String VID_CREATE_ID = "registration.processor.id.repo.generate";

	private static final String REG_PROC_APPLICATION_VERSION = "registration.processor.id.repo.vidVersion";

	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	public static final String VID_ACTIVE = "Active";

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;

	/** The cluster address. */
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.id.repo.vidType}")
	private String vidType;

	/** The localhost. */
	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

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
	private FileSystemAdapter adapter;

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
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The demographic dedupe repository. */
	@Autowired
	private BasePacketRepository<RegLostUinDetEntity, String> regLostUinDetEntity;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The registration id. */
	private String registrationId = "";

	/** The id response DTO. */
	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The id response DTO. */
	IdResponseDTO idResponseDTO = new IdResponseDTO();

	/** The id request DTO. */
	IdRequestDto idRequestDTO = new IdRequestDto();

	/** The identity json. */
	JSONObject identityJson = null;

	/** The demographic identity. */
	JSONObject demographicIdentity = null;

	JSONObject idJSON = null;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The id repo api version. */
	private String idRepoApiVersion = "v1";

	/** The reg processor identity json. */
	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The utility. */
	@Autowired
	private Utilities utility;

	/** The identity iterator util. */
	IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();

	/** The Constant UIN_FAILURE. */
	private static final String UIN_FAILURE = "UIN updation failure for registrationId ";

	/** The Constant NULL_IDREPO_RESPONSE. */
	private static final String NULL_IDREPO_RESPONSE = "Response from IdRepo is null";

	/** The description. */
	private String description = "";

	/** The is transaction successful. */
	private boolean isTransactionSuccessful = false;

	/** The Constant UIN_GENERATION_FAILED. */
	private static final String UIN_GENERATION_FAILED = "UIN Generation failed :";

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto = null;

	/** The Constant UIN. */
	private static final String UIN = "UIN";

	/** The Constant UIN_ASSIGNED. */
	private static final String UIN_ASSIGNED = "ASSIGNED";

	/** The Constant UIN_UNASSIGNED. */
	private static final String UIN_UNASSIGNED = "UNASSIGNED";

	RegistrationExceptionMapperUtil registrationStatusMapperUtil = new RegistrationExceptionMapperUtil();

	@Autowired
	ABISHandlerUtil aBISHandlerUtil;

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

		object.setMessageBusAddress(MessageBusAddress.UIN_GENERATION_BUS_IN);
		object.setInternalError(Boolean.FALSE);
		object.setIsValid(true);
		this.registrationId = object.getRid();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "UinGeneratorStage::process()::entry");
		UinGenResponseDto uinResponseDto = null;
		registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);
		try {
			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.UIN_GENERATOR.toString());
			registrationStatusDto.setRegistrationStageName(this.getClass().getSimpleName());

			if ((RegistrationType.LOST.toString()).equalsIgnoreCase(object.getReg_type().name())) {
				String lostPacketRegId = object.getRid();
				String matchedRegId = regLostUinDetEntity.getLostUinMatchedRegId(lostPacketRegId);
				if (matchedRegId != null) {
					linkRegIdWrtUin(lostPacketRegId, matchedRegId, object);
				}

			} else {

				InputStream idJsonStream = adapter.getFile(registrationId,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
				byte[] idJsonBytes = IOUtils.toByteArray(idJsonStream);
				String getJsonStringFromBytes = new String(idJsonBytes);
				identityJson = (JSONObject) JsonUtil.objectMapperReadValue(getJsonStringFromBytes, JSONObject.class);
				demographicIdentity = JsonUtil.getJSONObject(identityJson,
						utility.getGetRegProcessorDemographicIdentity());
				Number number = JsonUtil.getJSONValue(demographicIdentity, UIN);
				Long uinFieldCheck = number != null ? number.longValue() : null;
				if (uinFieldCheck == null) {
					String test = (String) registrationProcessorRestClientService.getApi(ApiName.UINGENERATOR, null, "",
							"", String.class);
					Gson gsonObj = new Gson();
					uinResponseDto = gsonObj.fromJson(test, UinGenResponseDto.class);
					long uinInLong = Long.parseLong(uinResponseDto.getResponse().getUin());
					demographicIdentity.put("UIN", uinInLong);
					idResponseDTO = sendIdRepoWithUin(registrationId, uinResponseDto.getResponse().getUin());
					if (idResponseDTO != null && idResponseDTO.getResponse() != null) {
						generateVid(uinResponseDto.getResponse().getUin());
						registrationStatusDto.setStatusComment(UinStatusMessage.PACKET_UIN_UPDATION_SUCCESS_MSG);

						sendResponseToUinGenerator(uinResponseDto.getResponse().getUin(), UIN_ASSIGNED);
						isTransactionSuccessful = true;
						registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSED.toString());
						description = "UIN updated successfully for registrationId " + registrationId;
						registrationStatusDto
								.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.PROCESSED.toString());
						regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
								"UinGeneratorStage::process()::exit");
						regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description);
					} else {
						String statusComment = idResponseDTO.getErrors() != null
								? idResponseDTO.getErrors().get(0).getMessage()
								: NULL_IDREPO_RESPONSE;
						registrationStatusDto.setStatusComment(statusComment);
						object.setInternalError(Boolean.TRUE);
						registrationStatusDto.setStatusCode(RegistrationStatusCode.REJECTED.toString());
						registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
								.getStatusCode(RegistrationExceptionTypeCode.PACKET_UIN_GENERATION_FAILED));
						sendResponseToUinGenerator(uinResponseDto.getResponse().getUin(), UIN_UNASSIGNED);
						isTransactionSuccessful = false;
						description = UIN_FAILURE + registrationId + "::" + statusComment;
						String idres = idResponseDTO != null ? idResponseDTO.toString() : NULL_IDREPO_RESPONSE;

						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
								statusComment + "  :  " + idres);
					}
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString() + registrationId, "Response from IdRepo API",
							"is :" + idResponseDTO.toString());
				} else {
					if ((RegistrationType.ACTIVATED.toString()).equalsIgnoreCase(object.getReg_type().toString())) {
						idResponseDTO = reActivateUin(registrationId, uinFieldCheck, object);
					} else if ((RegistrationType.DEACTIVATED.toString())
							.equalsIgnoreCase(object.getReg_type().toString())) {
						idResponseDTO = deactivateUin(registrationId, uinFieldCheck, object);
					} else if (RegistrationType.UPDATE.toString().equalsIgnoreCase(object.getReg_type().toString())
							|| (RegistrationType.RES_UPDATE.toString()
									.equalsIgnoreCase(object.getReg_type().toString()))) {
						idResponseDTO = uinUpdate(registrationId, uinFieldCheck, object);
					}
				}

			}
			registrationStatusDto.setUpdatedBy(USER);
		} catch (FSAdapterException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto
					.setStatusComment(PlatformErrorMessages.RPR_UGS_PACKET_STORE_NOT_ACCESSIBLE.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_UGS_PACKET_STORE_NOT_ACCESSIBLE.getMessage() + e.getMessage());
			object.setInternalError(Boolean.TRUE);
			isTransactionSuccessful = false;
			description = "FileSytem is not accessible for packet " + registrationId + "::" + e.getMessage();
			object.setIsValid(Boolean.FALSE);
			object.setRid(registrationId);
		} catch (ApisResourceAccessException ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_API_RESOURCE_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					RegistrationStatusCode.FAILED.toString() + ex.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
			description = "Internal error occurred in UINGenerator stage while processing registrationId "
					+ registrationId + "::" + ex.getMessage();

		} catch (IOException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage() + e.getMessage());
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
			description = "Internal error in UINGenerator stage while processing registrationId " + registrationId
					+ e.getMessage();
		} catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					RegistrationStatusCode.FAILED.toString() + ex.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
			description = "Internal error occurred in UINGenerator stage while processing registrationId "
					+ registrationId + ex.getMessage();
		} finally {
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, registrationId,
					ApiName.AUDIT);

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
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws VidCreationException
	 * @throws Exception
	 */
	private IdResponseDTO sendIdRepoWithUin(String regId, String uin) throws ApisResourceAccessException,
			JsonParseException, JsonMappingException, IOException, VidCreationException {

		List<Documents> documentInfo = getAllDocumentsByRegId(regId);
		RequestDto requestDto = new RequestDto();
		requestDto.setIdentity(demographicIdentity);
		requestDto.setDocuments(documentInfo);
		requestDto.setRegistrationId(regId);
		requestDto.setStatus(RegistrationType.ACTIVATED.toString());
		requestDto.setBiometricReferenceId(uin);

		IdResponseDTO result = null;
		idRequestDTO.setId(idRepoCreate);
		idRequestDTO.setRequest(requestDto);
		idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
		idRequestDTO.setVersion(idRepoApiVersion);
		idRequestDTO.setMetadata(null);
		Gson gson = new GsonBuilder().create();
		String idRequest = gson.toJson(idRequestDTO);

		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
				LoggerFileConstant.REGISTRATIONID.toString() + regId, "Request to IdRepo API", "is: " + idRequest);
		try {
			result = (IdResponseDTO) registrationProcessorRestClientService.postApi(ApiName.IDREPOSITORY, "", "",
					idRequestDTO, IdResponseDTO.class);

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString() + regId, "Response from IdRepo API",
					"is : " + result.toString());

		} catch (ApisResourceAccessException e) {

			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				description = UIN_GENERATION_FAILED + registrationId + "::"
						+ httpClientException.getResponseBodyAsString();
				throw new ApisResourceAccessException(httpClientException.getResponseBodyAsString(),
						httpClientException);
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				description = UIN_GENERATION_FAILED + registrationId + "::"
						+ httpServerException.getResponseBodyAsString();

				throw new ApisResourceAccessException(httpServerException.getResponseBodyAsString(),
						httpServerException);
			} else {
				description = UIN_GENERATION_FAILED + registrationId + "::" + e.getMessage();
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
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private List<Documents> getAllDocumentsByRegId(String regId) throws IOException {
		List<Documents> applicantDocuments = new ArrayList<>();

		idJSON = getDemoIdentity(registrationId);
		regProcessorIdentityJson = getMappeedJSONIdentity();
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
			applicantDocuments.add(
					getIdDocumnet(registrationId, PacketFiles.DEMOGRAPHIC.name(), proofOfAddress, proofOfAddressLabel));
		}
		if (proofOfDateOfBirth != null) {
			applicantDocuments.add(getIdDocumnet(registrationId, PacketFiles.DEMOGRAPHIC.name(), proofOfDateOfBirth,
					proofOfDateOfBirthLabel));
		}
		if (proofOfIdentity != null) {
			applicantDocuments.add(getIdDocumnet(registrationId, PacketFiles.DEMOGRAPHIC.name(), proofOfIdentity,
					proofOfIdentityLabel));
		}
		if (proofOfRelationship != null) {
			applicantDocuments.add(getIdDocumnet(registrationId, PacketFiles.DEMOGRAPHIC.name(), proofOfRelationship,
					proofOfRelationshipLabel));
		}
		if (applicantBiometric != null) {
			applicantDocuments.add(getIdDocumnet(registrationId, PacketFiles.BIOMETRIC.name(), applicantBiometric,
					applicantBiometricLabel));
		}
		return applicantDocuments;
	}

	private Documents getIdDocumnet(String registrationId, String folderPath, JSONObject idDocObj, String idDocLabel)
			throws IOException {
		Documents documentsInfoDto = new Documents();
		;
		InputStream poiStream = adapter.getFile(registrationId, folderPath + FILE_SEPARATOR + idDocObj.get("value"));
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
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RegistrationProcessorCheckedException
	 */
	private IdResponseDTO uinUpdate(String regId, Long uin, MessageDTO object)
			throws ApisResourceAccessException, IOException, RegistrationProcessorCheckedException {
		IdResponseDTO result;
		List<Documents> documentInfo = utility.getAllDocumentsByRegId(regId);
		result = idRepoRequestBuilder(RegistrationType.ACTIVATED.toString().toUpperCase(), regId, uin, documentInfo);
		if (result != null && result.getResponse() != null) {

			if ((RegistrationType.ACTIVATED.toString().toUpperCase())
					.equalsIgnoreCase(result.getResponse().getStatus())) {
				isTransactionSuccessful = true;
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSED.toString());
				registrationStatusDto
						.setStatusComment(UinStatusMessage.UIN_UPDATION_SUCCESS + " for registration Id:  " + regId);
				description = UinStatusMessage.UIN_UPDATION_SUCCESS + " for registration Id:  " + regId;
				object.setIsValid(Boolean.TRUE);
			}
		} else {
			String statusComment = result != null && result.getErrors() != null ? result.getErrors().get(0).getMessage()
					: NULL_IDREPO_RESPONSE;
			registrationStatusDto.setStatusComment(statusComment);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());

			description = UIN_FAILURE + regId + "::" + result != null && result.getErrors() != null
					? result.getErrors().get(0).getMessage()
					: NULL_IDREPO_RESPONSE;
			object.setIsValid(Boolean.FALSE);
		}
		return result;
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
	 */
	private IdResponseDTO idRepoRequestBuilder(String status, String regId, Long uin, List<Documents> documentInfo)
			throws ApisResourceAccessException {
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
		idRequestDTO.setId(idRepoUpdate);
		idRequestDTO.setMetadata(null);
		idRequestDTO.setRequest(requestDto);
		idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
		idRequestDTO.setVersion(idRepoApiVersion);

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
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private IdResponseDTO reActivateUin(String regId, Long uin, MessageDTO object) throws ApisResourceAccessException {
		IdResponseDTO result = getIdRepoDataByUIN(uin);
		List<String> pathsegments = new ArrayList<>();
		RequestDto requestDto = new RequestDto();

		if (result != null && result.getResponse() != null) {

			if ((RegistrationType.ACTIVATED.toString()).equalsIgnoreCase(result.getResponse().getStatus())) {

				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				registrationStatusDto.setStatusComment(
						UinStatusMessage.UIN_UPDATION_ALREADY_ACTIVATED + " for registration Id:  " + regId);
				description = UinStatusMessage.UIN_UPDATION_ALREADY_ACTIVATED + " for registration Id:  " + regId;
				object.setIsValid(Boolean.FALSE);
				return result;

			} else {

				requestDto.setRegistrationId(regId);
				requestDto.setStatus(RegistrationType.ACTIVATED.toString());
				requestDto.setBiometricReferenceId(Long.toString(uin));

				idRequestDTO.setId(idRepoUpdate);
				idRequestDTO.setRequest(requestDto);
				idRequestDTO.setMetadata(null);
				idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
				idRequestDTO.setVersion(idRepoApiVersion);
				Gson gson = new GsonBuilder().create();
				String idReq = gson.toJson(idResponseDTO);

				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString() + regId, "Update Request to IdRepo API",
						"is : " + idReq);

				result = (IdResponseDTO) registrationProcessorRestClientService.patchApi(ApiName.IDREPOSITORY,
						pathsegments, "", "", idRequestDTO, IdResponseDTO.class);

				if (result != null && result.getResponse() != null) {

					if ((RegistrationType.ACTIVATED.toString()).equalsIgnoreCase(result.getResponse().getStatus())) {
						isTransactionSuccessful = true;
						registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSED.toString());
						registrationStatusDto.setStatusComment(
								UinStatusMessage.UIN_UPDATION_ACTIVATED + " for registration Id:  " + regId);
						description = UinStatusMessage.UIN_UPDATION_ACTIVATED + " for registration Id:  " + regId;
						object.setIsValid(Boolean.TRUE);
					} else {

						registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
						registrationStatusDto.setStatusComment(UinStatusMessage.UIN_UPDATION_RE_ACTIVATION_FAILURE
								+ " for registration Id:  " + regId);
						description = UinStatusMessage.UIN_UPDATION_RE_ACTIVATION_FAILURE + " for registration Id:  "
								+ regId;
						object.setIsValid(Boolean.FALSE);
					}
				} else {
					String statusComment = result != null && result.getErrors() != null
							? result.getErrors().get(0).getMessage()
							: NULL_IDREPO_RESPONSE;
					registrationStatusDto.setStatusComment(statusComment);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());

					description = UIN_FAILURE + regId + "::" + result != null && result.getErrors() != null
							? result.getErrors().get(0).getMessage()
							: NULL_IDREPO_RESPONSE;
					object.setIsValid(Boolean.FALSE);
				}

			}

		}

		return result;
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
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 */
	private IdResponseDTO deactivateUin(String regId, Long uin, MessageDTO object) throws ApisResourceAccessException {
		IdResponseDTO idResponseDto;
		List<String> pathsegments = new ArrayList<>();
		RequestDto requestDto = new RequestDto();
		String statusComment = "";

		idResponseDto = getIdRepoDataByUIN(uin);

		if (idResponseDto.getResponse() != null
				&& idResponseDto.getResponse().getStatus().equalsIgnoreCase(RegistrationType.DEACTIVATED.toString())) {

			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(UinStatusMessage.UIN_DEACTIVATE_FAILURE + regId);
			description = UinStatusMessage.UIN_DEACTIVATE_FAILURE + regId;
			object.setIsValid(Boolean.FALSE);
			return idResponseDto;

		} else {
			requestDto.setRegistrationId(regId);
			requestDto.setStatus(RegistrationType.DEACTIVATED.toString());

			requestDto.setBiometricReferenceId(Long.toString(uin));
			idRequestDTO.setId(idRepoUpdate);
			idRequestDTO.setMetadata(null);
			idRequestDTO.setRequest(requestDto);
			idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
			idRequestDTO.setVersion(idRepoApiVersion);

			idResponseDto = (IdResponseDTO) registrationProcessorRestClientService.patchApi(ApiName.IDREPOSITORY,
					pathsegments, "", "", idRequestDTO, IdResponseDTO.class);

			if (idResponseDto != null && idResponseDto.getResponse() != null) {
				if (idResponseDto.getResponse().getStatus().equalsIgnoreCase(RegistrationType.DEACTIVATED.toString())) {
					registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSED.toString());
					registrationStatusDto.setStatusComment(UinStatusMessage.UIN_DEACTIVATE_SUCCESS + regId);
					description = UinStatusMessage.UIN_DEACTIVATE_SUCCESS + regId;
					object.setIsValid(Boolean.TRUE);
					statusComment = idResponseDto.getResponse().getStatus().toString();

				}
			} else {

				statusComment = idResponseDto != null && idResponseDto.getErrors() != null
						? idResponseDto.getErrors().get(0).getMessage()
						: NULL_IDREPO_RESPONSE;
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				registrationStatusDto.setStatusComment(statusComment);
				description = statusComment;
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
	 * @return the id repo data by UIN
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private IdResponseDTO getIdRepoDataByUIN(Long uin) throws ApisResourceAccessException {
		IdResponseDTO response;

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(Long.toString(uin));
		try {
			response = (IdResponseDTO) registrationProcessorRestClientService.getApi(ApiName.IDREPOGETIDBYUIN,
					pathsegments, "", "", IdResponseDTO.class);

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				description = UIN_GENERATION_FAILED + registrationId + "::"
						+ httpClientException.getResponseBodyAsString();
				throw new ApisResourceAccessException(httpClientException.getResponseBodyAsString(),
						httpClientException);
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				description = UIN_GENERATION_FAILED + registrationId + "::"
						+ httpServerException.getResponseBodyAsString();

				throw new ApisResourceAccessException(httpServerException.getResponseBodyAsString(),
						httpServerException);
			} else {
				description = UIN_GENERATION_FAILED + registrationId + "::" + e.getMessage();
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
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws VidCreationException
	 */
	private void sendResponseToUinGenerator(String uin, String uinStatus)
			throws ApisResourceAccessException, JsonProcessingException {
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

	private RegistrationProcessorIdentity getMappeedJSONIdentity()
			throws JsonParseException, JsonMappingException, IOException {
		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorIdentityJson());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		RegistrationProcessorIdentity regProcessorIdentityJson = mapIdentityJsonStringToObject
				.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);
		return regProcessorIdentityJson;
	}

	private JSONObject getDemoIdentity(String registrationId) throws IOException {
		InputStream documentInfoStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());

		byte[] bytes = IOUtils.toByteArray(documentInfoStream);
		String demographicJsonString = new String(bytes);
		JSONObject demographicJson = (JSONObject) JsonUtil.objectMapperReadValue(demographicJsonString,
				JSONObject.class);
		JSONObject demographicIdentity = JsonUtil.getJSONObject(demographicJson,
				utility.getGetRegProcessorDemographicIdentity());
		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		return demographicIdentity;
	}

	@SuppressWarnings("unchecked")
	private void generateVid(String UIN) throws ApisResourceAccessException, IOException, VidCreationException {
		ObjectMapper mapper = new ObjectMapper();
		VidRequestDto vidRequestDto = new VidRequestDto();
		VidResponseDto vidResponseDto = new VidResponseDto();
		RequestWrapper<VidRequestDto> request = new RequestWrapper<>();
		ResponseWrapper<VidResponseDto> response;
		try {

			vidRequestDto.setUIN(UIN);
			vidRequestDto.setVidType(vidType);
			request.setId(env.getProperty(VID_CREATE_ID));
			request.setRequest(vidRequestDto);
			DateTimeFormatter format = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));
			LocalDateTime localdatetime = LocalDateTime
					.parse(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)), format);
			request.setRequesttime(localdatetime);
			request.setVersion(env.getProperty(REG_PROC_APPLICATION_VERSION));

			response = (ResponseWrapper<VidResponseDto>) registrationProcessorRestClientService
					.postApi(ApiName.CREATEVID, "", "", request, ResponseWrapper.class);

			vidResponseDto = mapper.readValue(mapper.writeValueAsString(response.getResponse()), VidResponseDto.class);

			if (!response.getErrors().isEmpty()) {
				throw new VidCreationException(PlatformErrorMessages.RPR_UGS_VID_EXCEPTION.getMessage(),
						"VID creation exception");

			}

		} catch (JsonProcessingException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_UGS_JSON__PARSER_ERROR.getMessage() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw e;
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_UGS_API_RESOURCE_EXCEPTION.getMessage() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw e;
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_UGS_IO_EXCEPTION.getMessage() + e.getMessage()
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
	 * @return the id response DTO
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	private IdResponseDTO linkRegIdWrtUin(String lostPacketRegId, String matchedRegId, MessageDTO object)
			throws ApisResourceAccessException, IOException {

		IdResponseDTO idResponse = null;
		Number number = idRepoService.getUinFromIDRepo(matchedRegId, utility.getGetRegProcessorDemographicIdentity());

		Long uinFieldValue = number != null ? number.longValue() : null;
		RequestDto requestDto = new RequestDto();
		String statusComment = "";

		if (uinFieldValue != null) {

			JSONObject identityObject = new JSONObject();
			identityObject.put(UIN, uinFieldValue);

			requestDto.setRegistrationId(lostPacketRegId);
			requestDto.setIdentity(identityObject);
			idRequestDTO.setId(idRepoUpdate);
			idRequestDTO.setRequest(requestDto);
			idRequestDTO.setMetadata(null);
			idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
			idRequestDTO.setVersion(idRepoApiVersion);
			Gson gson = new GsonBuilder().create();
			String idReq = gson.toJson(idRequestDTO);

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString() + lostPacketRegId, "Update Request to IdRepo API",
					"is : " + idReq);

			idResponse = (IdResponseDTO) registrationProcessorRestClientService.patchApi(ApiName.IDREPOSITORY, null, "",
					"", idRequestDTO, IdResponseDTO.class);

			if (idResponse != null && idResponse.getResponse() != null) {
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSED.toString());
				registrationStatusDto
						.setStatusComment(UinStatusMessage.PACKET_LOST_UIN_UPDATION_SUCCESS_MSG + lostPacketRegId);
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.PROCESSED.toString());
				description = UinStatusMessage.PACKET_LOST_UIN_UPDATION_SUCCESS_MSG + lostPacketRegId;
				object.setIsValid(Boolean.TRUE);
				statusComment = idResponse.getResponse().getEntity().toString();

			} else {

				statusComment = idResponse != null && idResponse.getErrors() != null
						? idResponse.getErrors().get(0).getMessage()
						: UinStatusMessage.PACKET_LOST_UIN_UPDATION_FAILURE_MSG + "  " + NULL_IDREPO_RESPONSE
								+ "for lostPacketRegId " + lostPacketRegId;
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
				registrationStatusDto.setStatusComment(statusComment);

				registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.PACKET_UIN_GENERATION_ID_REPO_ERROR));
				description = UIN_FAILURE + lostPacketRegId + "::" + idResponse != null
						&& idResponse.getErrors() != null ? idResponse.getErrors().get(0).getMessage()
								: NULL_IDREPO_RESPONSE;
				object.setIsValid(Boolean.FALSE);
			}

		} else {
			statusComment = UinStatusMessage.PACKET_LOST_UIN_UPDATION_FAILURE_MSG + "  " + NULL_IDREPO_RESPONSE
					+ " UIN not available for matchedRegId " + matchedRegId;
			registrationStatusDto.setStatusComment(statusComment);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.REJECTED.toString());
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.PACKET_UIN_GENERATION_FAILED));
			description = UinStatusMessage.PACKET_LOST_UIN_UPDATION_FAILURE_MSG + "  " + NULL_IDREPO_RESPONSE
					+ " UIN not available for matchedRegId " + matchedRegId;

			object.setIsValid(Boolean.FALSE);
		}

		return idResponse;
	}
}
