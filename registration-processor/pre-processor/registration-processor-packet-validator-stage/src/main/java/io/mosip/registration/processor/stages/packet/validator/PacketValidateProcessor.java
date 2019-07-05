package io.mosip.registration.processor.stages.packet.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.mosip.kernel.core.exception.BaseCheckedException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
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
import io.mosip.registration.processor.core.constant.RegistrationType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.applicantcategory.ApplicantTypeDocument;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.MainRequestDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.MainResponseDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ReverseDataSyncRequestDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ReverseDatasyncReponseDTO;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.idreposervice.IdRepoService;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.dto.PacketValidationDto;
import io.mosip.registration.processor.stages.utils.ApplicantDocumentValidation;
import io.mosip.registration.processor.stages.utils.CheckSumValidation;
import io.mosip.registration.processor.stages.utils.DocumentUtility;
import io.mosip.registration.processor.stages.utils.FilesValidation;
import io.mosip.registration.processor.stages.utils.IdObjectsSchemaValidationOperationMapper;
import io.mosip.registration.processor.stages.utils.MandatoryValidation;
import io.mosip.registration.processor.stages.utils.MasterDataValidation;
import io.mosip.registration.processor.stages.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Service
@Transactional
public class PacketValidateProcessor {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketValidateProcessor.class);

	@Autowired
	private PacketManager fileSystemManager;

	@Autowired
	private RegistrationRepositary<SyncRegistrationEntity, String> registrationRepositary;

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant APPLICANT_TYPE. */
	public static final String APPLICANT_TYPE = "applicantType";

	private static final String VALIDATIONFALSE = "false";

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	RegistrationProcessorIdentity regProcessorIdentityJson;

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
	IdObjectsSchemaValidationOperationMapper idObjectsSchemaValidationOperationMapper;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	IdObjectValidator idObjectValidator;

	@Autowired
	private Utilities utility;

	@Autowired
	ApplicantTypeDocument applicantTypeDocument;

	@Autowired
	private IdRepoService idRepoService;

	private static final String INDIVIDUALBIOMETRICS = "individualBiometrics";

	private static final String VALUE = "value";

	private static final String VALIDATESCHEMA = "registration.processor.validateSchema";

	private static final String VALIDATEFILE = "registration.processor.validateFile";

	private static final String VALIDATECHECKSUM = "registration.processor.validateChecksum";

	private static final String VALIDATEAPPLICANTDOCUMENT = "registration.processor.validateApplicantDocument";

	private static final String VALIDATEMASTERDATA = "registration.processor.validateMasterData";

	private static final String VALIDATEMANDATORY = "registration-processor.validatemandotary";

	private static final String PRE_REG_ID = "mosip.pre-registration.datasync.store";
	private static final String VERSION = "1.0";
	private static final String CREATED_BY = "MOSIP_SYSTEM";


	@Autowired
	RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	public MessageDTO process(MessageDTO object, String stageName) {
		LogDescription description = new LogDescription();
		PacketValidationDto packetValidationDto = new PacketValidationDto(); 
		String preRegId = null;
		String registrationId = null;
		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		try {

			object.setMessageBusAddress(MessageBusAddress.PACKET_VALIDATOR_BUS_IN);
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.FALSE);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "PacketValidatorStage::process()::entry");
			registrationId = object.getRid();
			packetValidationDto.setTransactionSuccessful(false);

			registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);
			registrationStatusDto
			.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.VALIDATE_PACKET.toString());
			registrationStatusDto.setRegistrationStageName(stageName);
			InputStream packetMetaInfoStream = fileSystemManager.getFile(registrationId, PacketFiles.PACKET_META_INFO.name());
			PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketMetaInfo.class);
			IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
			Boolean isValid = validate(registrationStatusDto, packetMetaInfo, object, identityIteratorUtil, packetValidationDto);
			if (isValid) {
				registrationStatusDto
				.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_STRUCTURAL_VALIDATION_SUCCESS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
				// ReverseDataSync

				preRegId = identityIteratorUtil.getFieldValue(packetMetaInfo.getIdentity().getMetaData(),
						JsonConstant.PREREGISTRATIONID);
				reverseDataSync(preRegId, registrationId, description, packetValidationDto);

				object.setRid(registrationStatusDto.getRegistrationId());
				packetValidationDto.setTransactionSuccessful(true);
				description.setMessage(PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getMessage() + " -- " + registrationId);
				description.setCode(PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getCode());
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), description.getCode()
						+ " -- " + registrationId,
						"PacketValidatorStage::process()::exit");
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), description.getCode() + " -- " + registrationId, description.getMessage());

			} else {
				registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.PACKET_STRUCTURAL_VALIDATION_FAILED));
				object.setIsValid(Boolean.FALSE);
				int retryCount = registrationStatusDto.getRetryCount() != null
						? registrationStatusDto.getRetryCount() + 1
						: 1;
				description.setMessage("File validation(" + packetValidationDto.isFilesValidated()+ ")/Checksum validation(" + packetValidationDto.isCheckSumValidated()
						+ ")" + "/Applicant Document Validation(" + packetValidationDto.isApplicantDocumentValidation() + ")"
						+ "/Schema Validation(" + packetValidationDto.isSchemaValidated() + ")" + "/Master Data Validation("
						+ packetValidationDto.isMasterDataValidation() + ")" + "/MandatoryField Validation(" + packetValidationDto.isMandatoryValidation() + ")"
						+ "/isRidAndType Sync Validation(" + packetValidationDto.isRIdAndTypeSynched() + ")" + " failed for registrationId "
						+ registrationId);
				packetValidationDto.setTransactionSuccessful(false);
				registrationStatusDto.setRetryCount(retryCount);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				registrationStatusDto.setStatusComment(packetValidationDto.getPacketValidaionFailure());

				description.setMessage(PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + " -- " + description.getMessage());
				description.setCode(PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode());
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), description.getCode() + " -- " + registrationId,
						"PacketValidatorStage::process()::exit");
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), description.getCode() + " -- " + registrationId, description.getMessage());

			}

			registrationStatusDto.setUpdatedBy(USER);

		} catch (FSAdapterException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
			registrationStatusDto
			.setStatusComment(PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));
			packetValidationDto.setTransactionSuccessful(false);
			description.setMessage(PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getMessage());
			description.setCode(PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					description.getCode() + " -- " + registrationId,
					PlatformErrorMessages.RPR_PVM_PACKET_STORE_NOT_ACCESSIBLE.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());
		} catch (ApisResourceAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
			registrationStatusDto
					.setStatusComment(PlatformErrorMessages.RPR_PVM_API_RESOUCE_ACCESS_FAILED.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			description.setCode(PlatformErrorMessages.RPR_PVM_API_RESOUCE_ACCESS_FAILED.getCode());
			description.setMessage(PlatformErrorMessages.RPR_PVM_API_RESOUCE_ACCESS_FAILED.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), description.getCode()
					, registrationId,
					description + e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.TRUE);
		} catch (DataAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_DATA_ACCESS_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.DATA_ACCESS_EXCEPTION));
			packetValidationDto.setTransactionSuccessful(false);
			description.setMessage(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage());
			description.setCode(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					description.getCode() + " -- " + registrationId,
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage() + e.getMessage()
					+ ExceptionUtils.getStackTrace(e));
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());
		} catch (IdentityNotFoundException | IOException exc) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			packetValidationDto.setTransactionSuccessful(false);
			description.setMessage(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
			description.setCode(PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					description.getCode() + " -- " + registrationId, PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage()
							+ exc.getMessage() + ExceptionUtils.getStackTrace(exc));
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());

		} catch (ParsingException exc) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.PARSE_EXCEPTION));
			packetValidationDto.setTransactionSuccessful(false);
			description.setMessage(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage());
			description.setCode(PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					description.getCode() + " -- " + registrationId, PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage()
							+ exc.getMessage() + ExceptionUtils.getStackTrace(exc));
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());

		} catch (TablenotAccessibleException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
			registrationStatusDto
			.setStatusComment(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.TABLE_NOT_ACCESSIBLE_EXCEPTION));
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.TRUE);
			description.setMessage(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage());
			description.setCode(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), description.getCode() + " -- " + registrationId,
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), ExceptionUtils.getStackTrace(e));

		} catch (BaseCheckedException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_PVM_BASE_CHECKED_EXCEPTION.getMessage()+":"+e.getClass().getSimpleName());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.BASE_CHECKED_EXCEPTION));
			packetValidationDto.setTransactionSuccessful(false);
			description.setMessage(PlatformErrorMessages.RPR_PVM_BASE_CHECKED_EXCEPTION.getMessage());
			description.setCode(PlatformErrorMessages.RPR_PVM_BASE_CHECKED_EXCEPTION.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					description.getCode() + " -- " + registrationId,
					PlatformErrorMessages.RPR_PVM_BASE_CHECKED_EXCEPTION.getMessage() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());
		} catch (BaseUncheckedException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_PVM_BASE_UNCHECKED_EXCEPTION.getMessage()+"-"+e.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.BASE_UNCHECKED_EXCEPTION));
			packetValidationDto.setTransactionSuccessful(false);
			description.setMessage(PlatformErrorMessages.RPR_PVM_BASE_UNCHECKED_EXCEPTION.getMessage());
			description.setCode(PlatformErrorMessages.RPR_PVM_BASE_UNCHECKED_EXCEPTION.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					description.getCode() + " -- " + registrationId, PlatformErrorMessages.RPR_PVM_BASE_UNCHECKED_EXCEPTION.getMessage()
							+ e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());

		}catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			packetValidationDto.setTransactionSuccessful(false);
			description.setMessage(PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage());
			description.setCode(PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					description.getCode() + " -- " + registrationId, PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage()
							+ ex.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setIsValid(Boolean.FALSE);
			object.setInternalError(Boolean.TRUE);
			object.setRid(registrationStatusDto.getRegistrationId());

		} finally {

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, description.getMessage());
			if (object.getInternalError()) {
				registrationStatusDto.setUpdatedBy(USER);
				int retryCount = registrationStatusDto.getRetryCount() != null
						? registrationStatusDto.getRetryCount() + 1
						: 1;
				registrationStatusDto.setRetryCount(retryCount);
			}
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			if(packetValidationDto.isTransactionSuccessful())
				description.setMessage(PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getMessage());
			String eventId = packetValidationDto.isTransactionSuccessful() ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = packetValidationDto.isTransactionSuccessful() ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = packetValidationDto.isTransactionSuccessful() ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Succes/Error code */
			String moduleId = packetValidationDto.isTransactionSuccessful() ? PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getCode()
					: description.getCode();
			String moduleName = ModuleName.PACKET_VALIDATOR.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}

		return object;

	}

	private boolean validate(InternalRegistrationStatusDto registrationStatusDto, PacketMetaInfo packetMetaInfo,
			MessageDTO object, IdentityIteratorUtil identityIteratorUtil, PacketValidationDto packetValidationDto) throws IOException, ApisResourceAccessException, JSONException,
			org.json.simple.parser.ParseException, RegistrationProcessorCheckedException,
			IdObjectValidationFailedException, IdObjectIOException , PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		Long uin = null;
		JSONObject demographicIdentity= null;
		String registrationId = registrationStatusDto.getRegistrationId();
		Identity identity = packetMetaInfo.getIdentity();

		if (!fileValidation(identity, registrationStatusDto, packetValidationDto)) {
			return false;
		}

		InputStream idJsonStream = fileSystemManager.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());

		byte[] bytearray = IOUtils.toByteArray(idJsonStream);
		String jsonString = new String(bytearray);
		ObjectMapper mapper=new ObjectMapper();
		JSONObject idObject=mapper.readValue(bytearray, JSONObject.class);

		if (!schemaValidation(idObject, registrationStatusDto, packetValidationDto)) {
			return false;
		}

		if (!checkSumValidation(identity, registrationStatusDto, packetValidationDto))
			return false;

		demographicIdentity = utility.getDemographicIdentityJSONObject(registrationId);
		if (!individualBiometricsValidation(registrationStatusDto, demographicIdentity, packetValidationDto))
			return false;

		List<FieldValue> metadataList = identity.getMetaData();
		if (object.getReg_type().toString().equalsIgnoreCase(RegistrationType.ACTIVATED.toString())
				|| object.getReg_type().toString().equalsIgnoreCase(RegistrationType.DEACTIVATED.toString())
				|| object.getReg_type().toString().equalsIgnoreCase(RegistrationType.UPDATE.toString())
				|| object.getReg_type().toString().equalsIgnoreCase(RegistrationType.RES_UPDATE.toString())) {
			uin = utility.getUIn(registrationId);
			if (uin == null)
				throw new IdRepoAppException(PlatformErrorMessages.RPR_PVM_INVALID_UIN.getMessage());
			JSONObject jsonObject = utility.retrieveIdrepoJson(uin);
			if (jsonObject == null)
				throw new IdRepoAppException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		}
		boolean regTypeCheck = object.getReg_type().toString().equalsIgnoreCase(RegistrationType.ACTIVATED.toString())
				|| object.getReg_type().toString().equalsIgnoreCase(RegistrationType.DEACTIVATED.toString());

		if (!regTypeCheck) {
			if (!applicantDocumentValidation(jsonString, registrationId, packetValidationDto)) {
				packetValidationDto.setPacketValidaionFailure(" applicant document validation failed ");
				return false;
			}
			if (!masterDataValidation(jsonString, packetValidationDto)) {
				packetValidationDto.setPacketValidaionFailure(" master data validation failed ");
				return false;
			}

		} else {
			if (!activateDeactivatePacketValidation(demographicIdentity)) {
				packetValidationDto.setPacketValidaionFailure("activate/deactivate packet validation failed ");
				return false;
			}
		}
		// check if uin is in idrepisitory
		if (RegistrationType.UPDATE.name().equalsIgnoreCase(object.getReg_type().name())
				|| RegistrationType.RES_UPDATE.name().equalsIgnoreCase(object.getReg_type().name())
				||RegistrationType.ACTIVATED.name().equalsIgnoreCase(object.getReg_type().name())
				|| RegistrationType.DEACTIVATED.name().equalsIgnoreCase(object.getReg_type().name())) {

			if (!uinPresentInIdRepo(String.valueOf(uin))) {
				packetValidationDto.setPacketValidaionFailure("uin is not present in idrepo");
				return false;
			}
		}

		if (RegistrationType.NEW.name().equalsIgnoreCase(registrationStatusDto.getRegistrationType())
				&& !mandatoryValidation(registrationStatusDto, packetValidationDto)) {
			packetValidationDto.setPacketValidaionFailure("mandatory Validation failed");
			return false;
		}
		// Check RegId & regType are same or not From PacketMetaInfo by comparing with
				// Sync list table
				return validateRegIdAndTypeFromSyncTable(metadataList,identityIteratorUtil, packetValidationDto);
			}


	private boolean uinPresentInIdRepo(String uin) throws ApisResourceAccessException, IOException {
		return idRepoService.findUinFromIdrepo(uin, utility.getGetRegProcessorDemographicIdentity()) != null;

	}

	private boolean validateRegIdAndTypeFromSyncTable(List<FieldValue> metadataList, IdentityIteratorUtil identityIteratorUtil, PacketValidationDto packetValidationDto) {
		String regId = identityIteratorUtil.getFieldValue(metadataList, JsonConstant.REGISTRATIONID);
		String regType = identityIteratorUtil.getFieldValue(metadataList, JsonConstant.REGISTRATIONTYPE);
		List<SyncRegistrationEntity> syncRecordList = registrationRepositary.getSyncRecordsByRegIdAndRegType(regId,
				regType.toUpperCase());

		if (syncRecordList != null && !syncRecordList.isEmpty()) {
			packetValidationDto.setRIdAndTypeSynched(true);
			return packetValidationDto.isRIdAndTypeSynched();
		}
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), regId,
				PlatformErrorMessages.RPR_PVM_RECORD_NOT_MATCHED_FROM_SYNC_TABLE.getCode(),
				PlatformErrorMessages.RPR_PVM_RECORD_NOT_MATCHED_FROM_SYNC_TABLE.getMessage());
		return packetValidationDto.isRIdAndTypeSynched();
	}

	private boolean mandatoryValidation(InternalRegistrationStatusDto registrationStatusDto, PacketValidationDto packetValidationDto)
			throws IOException, JSONException, PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		if (env.getProperty(VALIDATEMANDATORY).trim().equalsIgnoreCase(VALIDATIONFALSE))
			return true;
		MandatoryValidation mandatoryValidation = new MandatoryValidation(fileSystemManager, registrationStatusDto, utility);
		packetValidationDto.setMandatoryValidation(mandatoryValidation.mandatoryFieldValidation(registrationStatusDto.getRegistrationId()));
		return packetValidationDto.isMandatoryValidation();
	}

	private boolean schemaValidation(JSONObject idObject,InternalRegistrationStatusDto registrationStatusDto, PacketValidationDto packetValidationDto)
			throws ApisResourceAccessException, IOException, IdObjectValidationFailedException, IdObjectIOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException
			 {

		if (env.getProperty(VALIDATESCHEMA).trim().equalsIgnoreCase(VALIDATIONFALSE)) {
			packetValidationDto.setSchemaValidated(true);
			return packetValidationDto.isSchemaValidated();
		}
		IdObjectValidatorSupportedOperations operation= idObjectsSchemaValidationOperationMapper.
				getOperation(registrationStatusDto.getRegistrationId());
		packetValidationDto.setSchemaValidated(idObjectValidator.validateIdObject(idObject, operation));

		if (!packetValidationDto.isSchemaValidated()) {
			packetValidationDto.setPacketValidaionFailure(" Schema validation failed ");

		}

		return packetValidationDto.isSchemaValidated();

	}

	private boolean fileValidation(Identity identity, InternalRegistrationStatusDto registrationStatusDto, PacketValidationDto packetValidationDto) throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		if (env.getProperty(VALIDATEFILE).trim().equalsIgnoreCase(VALIDATIONFALSE)) {
			packetValidationDto.setFilesValidated(true);
			return packetValidationDto.isFilesValidated();
		}
		FilesValidation filesValidation = new FilesValidation(fileSystemManager, registrationStatusDto);
		packetValidationDto.setFilesValidated(filesValidation.filesValidation(registrationStatusDto.getRegistrationId(), identity));
		if (!packetValidationDto.isFilesValidated())
			packetValidationDto.setPacketValidaionFailure(" fileValidation failed ");
		return packetValidationDto.isFilesValidated();

	}

	private boolean checkSumValidation(Identity identity, InternalRegistrationStatusDto registrationStatusDto, PacketValidationDto packetValidationDto)
			throws IOException, PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		if (env.getProperty(VALIDATECHECKSUM).trim().equalsIgnoreCase(VALIDATIONFALSE)) {
			packetValidationDto.setCheckSumValidated(true);
			return packetValidationDto.isCheckSumValidated();
		}
		CheckSumValidation checkSumValidation = new CheckSumValidation(fileSystemManager, registrationStatusDto);
		packetValidationDto.setCheckSumValidated(checkSumValidation.checksumvalidation(registrationStatusDto.getRegistrationId(), identity));
		if (!packetValidationDto.isCheckSumValidated())
			packetValidationDto.setPacketValidaionFailure(" ChecksumValidation falied ");

		return packetValidationDto.isCheckSumValidated();

	}

	private boolean activateDeactivatePacketValidation(JSONObject demographicIdentity) {
		String[] activateDeactivate = "UIN,IDSchemaVersion".split(",");
		return CollectionUtils.isEqualCollection(demographicIdentity.keySet(), Arrays.asList(activateDeactivate));

	}

	private boolean individualBiometricsValidation(InternalRegistrationStatusDto registrationStatusDto, JSONObject demographicIdentity, PacketValidationDto packetValidationDto) throws RegistrationProcessorCheckedException {
		try {
			String registrationId = registrationStatusDto.getRegistrationId();

			JSONObject identityJsonObject = JsonUtil.getJSONObject(demographicIdentity, INDIVIDUALBIOMETRICS);
			if (identityJsonObject != null) {
				String cbefFile = (String) identityJsonObject.get(VALUE);
				if (cbefFile == null) {
					packetValidationDto.setPacketValidaionFailure(" individualBiometricsValidation failed ");
					return false;
				}
				InputStream idJsonStream = fileSystemManager.getFile(registrationId,
						PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR + cbefFile);
				if (idJsonStream != null)
					return true;

			}
		} catch (IOException | PacketDecryptionFailureException | ApisResourceAccessException | io.mosip.kernel.core.exception.IOException e) {
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}

		return true;
	}

	private boolean applicantDocumentValidation(String jsonString, String registrationId, PacketValidationDto packetValidationDto)
			throws IOException, ApisResourceAccessException, org.json.simple.parser.ParseException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		if (env.getProperty(VALIDATEAPPLICANTDOCUMENT).trim().equalsIgnoreCase(VALIDATIONFALSE)) {
			packetValidationDto.setApplicantDocumentValidation(true); 
			return packetValidationDto.isApplicantDocumentValidation();
		}
		ApplicantDocumentValidation applicantDocumentValidation = new ApplicantDocumentValidation(utility, env,
				applicantTypeDocument);
		packetValidationDto.setApplicantDocumentValidation(applicantDocumentValidation.validateDocument(registrationId, jsonString));
		if (!packetValidationDto.isApplicantDocumentValidation())
			packetValidationDto.setPacketValidaionFailure("Applicant Document validation failed");
		return packetValidationDto.isApplicantDocumentValidation();

	}

	private boolean masterDataValidation(String jsonString, PacketValidationDto packetValidationDto) throws ApisResourceAccessException, IOException {
		if (env.getProperty(VALIDATEMASTERDATA).trim().equalsIgnoreCase(VALIDATIONFALSE)) {
			packetValidationDto.setMasterDataValidation(true);
			return packetValidationDto.isMasterDataValidation();
		}
		MasterDataValidation masterDataValidation = new MasterDataValidation(env, registrationProcessorRestService,
				utility);
		packetValidationDto.setMasterDataValidation(masterDataValidation.validateMasterData(jsonString));
		if (!packetValidationDto.isMasterDataValidation())
			packetValidationDto.setPacketValidaionFailure("Master data validation failed");
		return packetValidationDto.isMasterDataValidation();

	}

	private void reverseDataSync(String preRegId, String registrationId, LogDescription description, PacketValidationDto packetValidationDto) {
		try {
			if (registrationId != null) {
				packetValidationDto.setTransactionSuccessful(false);
				MainResponseDTO<ReverseDatasyncReponseDTO> mainResponseDto = null;
				if (preRegId != null && !preRegId.trim().isEmpty()) {
					MainRequestDTO<ReverseDataSyncRequestDTO> mainRequestDto = new MainRequestDTO<>();
					mainRequestDto.setId(PRE_REG_ID);
					mainRequestDto.setVersion(VERSION);
					mainRequestDto.setRequesttime(new Date());
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
					packetValidationDto.setTransactionSuccessful(true);

				}
				if (mainResponseDto != null && mainResponseDto.getErrors() != null &&  mainResponseDto.getErrors().size() > 0) {
					regProcLogger.error(LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
							PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
							mainResponseDto.getErrors().toString());
					packetValidationDto.setTransactionSuccessful(false);
					description.setMessage(PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage());

				} else if (mainResponseDto == null) {
					packetValidationDto.setTransactionSuccessful(false);
					description.setMessage(PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage()
							+ " null response from rest client ");
				} else {
					packetValidationDto.setTransactionSuccessful(true);
					regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
							PlatformErrorMessages.REVERSE_DATA_SYNC_SUCCESS.getMessage(), "");
				}

			}

		} catch (ApisResourceAccessException e) {

			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
						httpClientException.getResponseBodyAsString() + ExceptionUtils.getStackTrace(e));
				description.setMessage(PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage());
				description.setCode(PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getCode());
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(),
						httpServerException.getResponseBodyAsString() + ExceptionUtils.getStackTrace(e));
				description.setMessage(PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage());
				description.setCode(PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getCode());
			} else {
				regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage(), e.getMessage());
				description.setMessage(PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getMessage());
				description.setCode(PlatformErrorMessages.REVERSE_DATA_SYNC_FAILED.getCode());
			}

		} finally {
			if(packetValidationDto.isTransactionSuccessful())
				description.setMessage("Reverse data sync of Pre-RegistrationIds sucessful");
			String eventId = packetValidationDto.isTransactionSuccessful() ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = packetValidationDto.isTransactionSuccessful() ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = packetValidationDto.isTransactionSuccessful() ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Success/Error code */
			String moduleId = packetValidationDto.isTransactionSuccessful() ? PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getCode()
					: description.getCode();
			String moduleName = ModuleName.PACKET_VALIDATOR.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}

	}

}
