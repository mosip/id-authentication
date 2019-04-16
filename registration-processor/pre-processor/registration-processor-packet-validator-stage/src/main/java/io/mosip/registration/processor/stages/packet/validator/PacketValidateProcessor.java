package io.mosip.registration.processor.stages.packet.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.idjson.Document;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.MainRequestDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.MainResponseDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ReverseDataSyncRequestDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ReverseDatasyncReponseDTO;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.utils.ApplicantDocumentValidation;
import io.mosip.registration.processor.stages.utils.CheckSumValidation;
import io.mosip.registration.processor.stages.utils.DocumentUtility;
import io.mosip.registration.processor.stages.utils.FilesValidation;
import io.mosip.registration.processor.stages.utils.MandatoryValidation;
import io.mosip.registration.processor.stages.utils.MasterDataValidation;
import io.mosip.registration.processor.stages.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Service
@Transactional
public class PacketValidateProcessor {

	/** The identity iterator util. */
	IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketValidateProcessor.class);

	/** The adapter. */
	@Autowired
	private FileSystemAdapter adapter;
	/** Validator stage */

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant APPLICANT_TYPE. */
	public static final String APPLICANT_TYPE = "applicantType";

	private static final String VALIDATIONFALSE = "false";

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private Environment env;

	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	/** The core audit request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	DocumentUtility documentUtility;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	JsonValidator jsonValidator;

	@Autowired
	private Utilities utility;

	/** The registration id. */
	private String registrationId = "";

	/** The description. */
	private String description;

	/** the Error Code */
	private String code;

	/** The flag check for reg_type. */
	private boolean regTypeCheck;

	private static final String VALIDATESCHEMA = "registration.processor.validateSchema";

	private static final String VALIDATEFILE = "registration.processor.validateFile";

	private static final String VALIDATECHECKSUM = "registration.processor.validateChecksum";

	private static final String VALIDATEAPPLICANTDOCUMENT = "registration.processor.validateApplicantDocument";

	private static final String VALIDATEMASTERDATA = "registration.processor.validateMasterData";
	
	private static final String VALIDATEMANDATORY= "registration-processor.validatemandotary";

	/** The is transaction successful. */
	private boolean isTransactionSuccessful;
	private static final String PRE_REG_ID = "mosip.pre-registration.datasync";
	private static final String VERSION = "1.0";
	private static final String CREATED_BY = "MOSIP_SYSTEM";
	boolean isSchemaValidated = false;
	boolean isCheckSumValidated = false;
	boolean isApplicantDocumentValidation = false;
	boolean isFilesValidated = false;
	boolean isMasterDataValidation = false;
	RegistrationExceptionMapperUtil registrationStatusMapperUtil = new RegistrationExceptionMapperUtil();

	public MessageDTO process(MessageDTO object, String stageName) {
		String preRegId = null;
		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		try {

			object.setMessageBusAddress(MessageBusAddress.PACKET_VALIDATOR_BUS_IN);
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.FALSE);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "PacketValidatorStage::process()::entry");
			this.registrationId = object.getRid();
			description = "";
			isTransactionSuccessful = false;

			registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);
			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.VALIDATE_PACKET.toString());
			registrationStatusDto.setRegistrationStageName(stageName);
			InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKET_META_INFO.name());
			PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketMetaInfo.class);
			Boolean isValid = validate(registrationStatusDto, packetMetaInfo, object);
			Boolean mandatoryValidationResult=true;
			if(RegistrationType.NEW.name().equalsIgnoreCase(registrationStatusDto.getRegistrationType())) {
				mandatoryValidationResult =  mandatoryValidation(registrationStatusDto);
			}
			
			if (isValid && mandatoryValidationResult) {
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_STRUCTURAL_VALIDATION_SUCCESS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_SUCCESS.toString());
				// ReverseDataSync
				preRegId = identityIteratorUtil.getFieldValue(packetMetaInfo.getIdentity().getMetaData(),
						JsonConstant.PREREGISTRATIONID);
				reverseDataSync(preRegId);

				object.setRid(registrationStatusDto.getRegistrationId());
				isTransactionSuccessful = true;
				description = PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getMessage() + " -- " + registrationId;
				code = PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getCode();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId,
						"PacketValidatorStage::process()::exit");
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId, description);

			} else {
				registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.PACKET_STRUCTURAL_VALIDATION_FAILED));
				object.setIsValid(Boolean.FALSE);
				int retryCount = registrationStatusDto.getRetryCount() != null
						? registrationStatusDto.getRetryCount() + 1
						: 1;
				description = "File validation(" + isFilesValidated + ")/Checksum validation(" + isCheckSumValidated
						+ ")/Applicant Document Validation(" + isApplicantDocumentValidation
						+ ")/Master Data Validation(" + isMasterDataValidation + ") failed for registrationId "
						+ registrationId;
				isTransactionSuccessful = false;
				registrationStatusDto.setRetryCount(retryCount);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED.toString());
				registrationStatusDto.setStatusComment(description);

				description = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + " -- " + description;
				code = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId,
						"PacketValidatorStage::process()::exit");
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), code + " -- " + registrationId, description);

			}

			registrationStatusDto.setUpdatedBy(USER);

		} catch (FSAdapterException e) {
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
			isTransactionSuccessful = false;
			description = PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getMessage();
			code = PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getCode();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					code + " -- " + registrationId,
					PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getMessage() + e.getMessage());
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());
		} catch (ApisResourceAccessException e) {
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			code = PlatformErrorMessages.RPR_PVM_API_RESOUCE_ACCESS_FAILED.getCode();
			description = PlatformErrorMessages.RPR_PVM_API_RESOUCE_ACCESS_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
		} catch (DataAccessException e) {
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.DATA_ACCESS_EXCEPTION));
			isTransactionSuccessful = false;
			description = PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage();
			code = PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					code + " -- " + registrationId,
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());
		} catch (IdentityNotFoundException | IOException exc) {
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			isTransactionSuccessful = false;
			description = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage();
			code = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					code + " -- " + registrationId, PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage()
							+ exc.getMessage() + ExceptionUtils.getStackTrace(exc));
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());

		} catch (TablenotAccessibleException e) {
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.TABLE_NOT_ACCESSIBLE_EXCEPTION));
			object.setInternalError(Boolean.TRUE);
			description = PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage();
			code = PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code + " -- " + registrationId,
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e.toString());

		} catch (BaseUncheckedException e) {
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.BASE_UNCHECKED_EXCEPTION));
			isTransactionSuccessful = false;
			description = PlatformErrorMessages.RPR_PVM_BASE_UNCHECKED_EXCEPTION.getMessage();
			code = PlatformErrorMessages.RPR_PVM_BASE_UNCHECKED_EXCEPTION.getCode();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					code + " -- " + registrationId, PlatformErrorMessages.RPR_PVM_BASE_UNCHECKED_EXCEPTION.getMessage()
							+ e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());

		} catch (Exception ex) {
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			isTransactionSuccessful = false;
			description = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage();
			code = PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					code + " -- " + registrationId, PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage()
							+ ex.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());

		} finally {

			
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, description);
			if (object.getInternalError()) {
				registrationStatusDto.setUpdatedBy(USER);
				int retryCount = registrationStatusDto.getRetryCount() != null
						? registrationStatusDto.getRetryCount() + 1
						: 1;

				registrationStatusDto.setRetryCount(retryCount);
				registrationStatusDto.setStatusComment(description);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED.toString());
			}
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			description = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getMessage()
					: description;
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Succes/Error code */
			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getCode()
					: code;
			String moduleName = ModuleName.PACKET_VALIDATOR.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}

		return object;

	}


	private boolean validate(InternalRegistrationStatusDto registrationStatusDto, PacketMetaInfo packetMetaInfo,
			MessageDTO object) throws IOException, JsonValidationProcessingException, JsonIOException,
			JsonSchemaIOException, FileIOException, ApisResourceAccessException {

		InputStream idJsonStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
		byte[] bytearray = IOUtils.toByteArray(idJsonStream);
		String jsonString = new String(bytearray);

		Identity identity = packetMetaInfo.getIdentity();

		if (!schemaValidation(jsonString)) {
			return false;
		}

		if (!fileValidation(identity, registrationStatusDto))
			return false;

		if (!checkSumValidation(identity, registrationStatusDto))
			return false;

		List<FieldValue> metadataList = identity.getMetaData();
		object.setReg_type(identityIteratorUtil.getFieldValue(metadataList, JsonConstant.REGISTRATIONTYPE));
		regTypeCheck = (object.getReg_type().equalsIgnoreCase(RegistrationType.ACTIVATED.toString())
				|| object.getReg_type().equalsIgnoreCase(RegistrationType.DEACTIVATED.toString()));

		if (!regTypeCheck) {
			if (!applicantDocumentValidation(identity, registrationStatusDto))
				return false;
			if (!masterDataValidation(jsonString, registrationStatusDto))
				return false;

		}

		return true;

	}

	
	


	private boolean mandatoryValidation(InternalRegistrationStatusDto registrationStatusDto) throws IOException {
		if (env.getProperty(VALIDATEMANDATORY).trim().equalsIgnoreCase(VALIDATIONFALSE))
			return true;
		MandatoryValidation mandatoryValidation = new MandatoryValidation(adapter, registrationStatusDto,utility);
		return mandatoryValidation.mandatoryFieldValidation(registrationStatusDto.getRegistrationId());
	}


	private boolean schemaValidation(String jsonString)
			throws JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {

		if (env.getProperty(VALIDATESCHEMA).trim().equalsIgnoreCase(VALIDATIONFALSE))
			return true;

		ValidationReport validationReport = jsonValidator.validateJson(jsonString);

		if (validationReport.isValid())
			isSchemaValidated = true;

		return isSchemaValidated;

	}

	private boolean fileValidation(Identity identity, InternalRegistrationStatusDto registrationStatusDto) {
		if (env.getProperty(VALIDATEFILE).trim().equalsIgnoreCase(VALIDATIONFALSE))
			return true;
		FilesValidation filesValidation = new FilesValidation(adapter, registrationStatusDto);
		isFilesValidated = filesValidation.filesValidation(registrationId, identity);

		return isFilesValidated;

	}

	private boolean checkSumValidation(Identity identity, InternalRegistrationStatusDto registrationStatusDto)
			throws IOException {
		if (env.getProperty(VALIDATECHECKSUM).trim().equalsIgnoreCase(VALIDATIONFALSE))
			return true;
		CheckSumValidation checkSumValidation = new CheckSumValidation(adapter, registrationStatusDto);
		isCheckSumValidated = checkSumValidation.checksumvalidation(registrationId, identity);

		return isCheckSumValidated;

	}

	private boolean applicantDocumentValidation(Identity identity, InternalRegistrationStatusDto registrationStatusDto)
			throws IOException {
		if (env.getProperty(VALIDATEAPPLICANTDOCUMENT).trim().equalsIgnoreCase(VALIDATIONFALSE))
			return true;
		InputStream documentInfoStream = null;
		List<Document> documentList = null;
		documentInfoStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
		byte[] bytes = null;
		bytes = IOUtils.toByteArray(documentInfoStream);
		documentList = documentUtility.getDocumentList(bytes);

		ApplicantDocumentValidation applicantDocumentValidation = new ApplicantDocumentValidation(
				registrationStatusDto);
		isApplicantDocumentValidation = applicantDocumentValidation.validateDocument(identity, documentList,
				registrationId);

		return isApplicantDocumentValidation;

	}

	private boolean masterDataValidation(String jsonString, InternalRegistrationStatusDto registrationStatusDto)
			throws ApisResourceAccessException, IOException {
		if (env.getProperty(VALIDATEMASTERDATA).trim().equalsIgnoreCase(VALIDATIONFALSE))
			return true;

		MasterDataValidation masterDataValidation = new MasterDataValidation(env, registrationProcessorRestService,
				utility);
		isMasterDataValidation = masterDataValidation.validateMasterData(jsonString);

		return isMasterDataValidation;

	}

	private void reverseDataSync(String preRegId) {
		try {
			if (this.registrationId != null) {
				isTransactionSuccessful = false;
				MainResponseDTO<ReverseDatasyncReponseDTO> mainResponseDto = null;
				if (preRegId != null && !preRegId.trim().isEmpty()) {
					MainRequestDTO<ReverseDataSyncRequestDTO> mainRequestDto = new MainRequestDTO<>();
					mainRequestDto.setId(PRE_REG_ID);
					mainRequestDto.setVer(VERSION);
					mainRequestDto.setReqTime(new Date());
					ReverseDataSyncRequestDTO reverseDataSyncRequestDto = new ReverseDataSyncRequestDTO();
					reverseDataSyncRequestDto.setCreatedBy(CREATED_BY);
					reverseDataSyncRequestDto.setLangCode("eng");
					reverseDataSyncRequestDto.setPreRegistrationIds(Arrays.asList(preRegId));
					reverseDataSyncRequestDto.setCreatedDateTime(new Date());
					reverseDataSyncRequestDto.setUpdateDateTime(new Date());
					reverseDataSyncRequestDto.setUpdateBy(CREATED_BY);
					mainRequestDto.setRequest(reverseDataSyncRequestDto);

					mainResponseDto = (MainResponseDTO) restClientService.postApi(ApiName.REVERSEDATASYNC, "", "",
							mainRequestDto, MainResponseDTO.class);
					isTransactionSuccessful = true;

				}
				if (mainResponseDto != null && mainResponseDto.getErr() != null) {
					regProcLogger.error(LoggerFileConstant.REGISTRATIONID.toString(), registrationId.toString(),
							PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
							mainResponseDto.getErr().toString());
					isTransactionSuccessful = false;
					description = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage();

				}

			}

		} catch (ApisResourceAccessException e) {

			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId.toString(),
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
						httpClientException.getResponseBodyAsString() + ExceptionUtils.getStackTrace(e));
				description = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage();
				code = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getCode();
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId.toString(),
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
						httpServerException.getResponseBodyAsString() + ExceptionUtils.getStackTrace(e));
				description = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage();
				code = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getCode();
			} else {
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId.toString(),
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(), e.getMessage());
				description = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage();
				code = PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getCode();
			}

		} finally {
			description = isTransactionSuccessful ? "Reverse data sync of Pre-RegistrationIds sucessful" : description;
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Succes/Error code */
			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getCode()
					: code;
			String moduleName = ModuleName.PACKET_VALIDATOR.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}

	}

}
