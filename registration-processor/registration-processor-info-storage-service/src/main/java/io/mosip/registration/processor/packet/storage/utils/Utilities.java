package io.mosip.registration.processor.packet.storage.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.abis.queue.dto.AbisQueueDetails;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorUnCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.Documents;
import io.mosip.registration.processor.core.idrepo.dto.IdRequestDto;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO1;
import io.mosip.registration.processor.core.idrepo.dto.RequestDto;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.vid.VidResDTO;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;
import io.mosip.registration.processor.packet.storage.exception.QueueConnectionNotFound;
import io.mosip.registration.processor.packet.storage.exception.VidCreationException;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import lombok.Data;

/**
 *
 * @author Girish Yarru
 *
 */
@Component
@Data
public class Utilities {
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(Utilities.class);

	private static final String UIN = "UIN";
	public static final String FILE_SEPARATOR = "\\";
	private static final String RE_PROCESSING = "re-processing";
	private static final String HANDLER = "handler";
	private static final String NEW_PACKET = "New-packet";

	@Autowired
	private PacketManager adapter;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;

	/** The config server file storage URL. */
	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;

	@Value("${registration.processor.identityjson}")
	private String getRegProcessorIdentityJson;

	@Value("${registration.processor.demographic.identity}")
	private String getRegProcessorDemographicIdentity;

	@Value("${registration.processor.document.category}")
	private String getRegProcessorDocumentCategory;

	@Value("${registration.processor.applicant.type}")
	private String getRegProcessorApplicantType;

	@Value("${registration.processor.applicant.dob.format}")
	private String dobFormat;

	@Value("${registration.processor.reprocess.elapse.time}")
	private long elapseTime;

	@Value("${registration.processor.abis.json}")
	private String registrationProcessorAbisJson;

	@Value("${registration.processor.id.repo.update}")
	private String idRepoUpdate;

	@Value("${registration.processor.id.repo.vidVersion}")
	private String vidVersion;

	@Autowired
	private PacketInfoDao packetInfoDao;

	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	private static final String INBOUNDQUEUENAME = "inboundQueueName";
	private static final String OUTBOUNDQUEUENAME = "outboundQueueName";
	private static final String ABIS = "abis";
	private static final String USERNAME = "userName";
	private static final String PASSWORD = "password";
	private static final String BROKERURL = "brokerUrl";
	private static final String TYPEOFQUEUE = "typeOfQueue";
	private static final String NAME = "name";

	public static String getJson(String configServerFileStorageURL, String uri) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configServerFileStorageURL + uri, String.class);
	}

	/**
	 * get applicant age by registration id. Checks the id json if dob or age
	 * present, if yes returns age if both dob or age are not present then retrieves
	 * age from id repo
	 *
	 * @param registrationId
	 * @return
	 * @throws IOException
	 * @throws ApisResourceAccessException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws PacketDecryptionFailureException
	 */
	public int getApplicantAge(String registrationId) throws IOException, ApisResourceAccessException,
			PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				registrationId, "Utilities::getApplicantAge()::entry");

		RegistrationProcessorIdentity regProcessorIdentityJson = getRegistrationProcessorIdentityJson();
		String ageKey = regProcessorIdentityJson.getIdentity().getAge().getValue();
		String dobKey = regProcessorIdentityJson.getIdentity().getDob().getValue();

		JSONObject demographicIdentity = getDemographicIdentityJSONObject(registrationId);
		String applicantDob = JsonUtil.getJSONValue(demographicIdentity, dobKey);
		Integer applicantAge = JsonUtil.getJSONValue(demographicIdentity, ageKey);
		if (applicantDob != null) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
					registrationId, "Utilities::getApplicantAge()::exit when applicantDob is not null");
			return calculateAge(applicantDob);
		} else if (applicantAge != null) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
					registrationId, "Utilities::getApplicantAge()::exit when applicantAge is not null");
			return applicantAge;

		} else {
			Long uin = getUIn(registrationId);
			JSONObject identityJSONOject = retrieveIdrepoJson(uin);
			String idRepoApplicantDob = JsonUtil.getJSONValue(identityJSONOject, dobKey);
			if (idRepoApplicantDob != null) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
						registrationId, "Utilities::getApplicantAge()::exit when ID REPO applicantDob is not null");
				return calculateAge(idRepoApplicantDob);
			}
			Integer idRepoApplicantAge = JsonUtil.getJSONValue(demographicIdentity, ageKey);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
					registrationId, "Utilities::getApplicantAge()::exit when ID REPO applicantAge is not null");
			return idRepoApplicantAge != null ? idRepoApplicantAge : 0;

		}

	}

	/**
	 * retrieving identity json ffrom id repo by UIN
	 *
	 * @param uin
	 * @return
	 * @throws ApisResourceAccessException
	 * @throws IdRepoAppException
	 * @throws IOException
	 */
	public JSONObject retrieveIdrepoJson(Long uin) throws ApisResourceAccessException, IdRepoAppException, IOException {

		if (uin != null) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), "",
					"Utilities::retrieveIdrepoJson()::entry");
			List<String> pathSegments = new ArrayList<>();
			pathSegments.add(String.valueOf(uin));
			IdResponseDTO1 idResponseDto = new IdResponseDTO1();

			idResponseDto = (IdResponseDTO1) restClientService.getApi(ApiName.IDREPOGETIDBYUIN, pathSegments, "", "",
					IdResponseDTO1.class);
			if (idResponseDto == null) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), "",
						"Utilities::retrieveIdrepoJson()::exit idResponseDto is null");
				return null;
			}
			if (!idResponseDto.getErrors().isEmpty()) {
				List<ErrorDTO> error = idResponseDto.getErrors();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), "",
						"Utilities::retrieveIdrepoJson():: error with error message " + error.get(0).getMessage());
				throw new IdRepoAppException(error.get(0).getMessage());
			}

			idResponseDto.getResponse().getIdentity();
			ObjectMapper objMapper = new ObjectMapper();
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), "",
					"Utilities::retrieveIdrepoJson():: IDREPOGETIDBYUIN GET service call ended Successfully");
			return objMapper.convertValue(idResponseDto.getResponse().getIdentity(), JSONObject.class);

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), "",
				"Utilities::retrieveIdrepoJson()::exit UIN is null");
		return null;
	}

	/**
	 * Returns all the list of queue details(inbound/outbound address,name,url,pwd)
	 * from abisJson Also validates the abis json fileds(null or not)
	 *
	 * @return
	 * @throws RegistrationProcessorCheckedException
	 */
	public List<AbisQueueDetails> getAbisQueueDetails() throws RegistrationProcessorCheckedException {
		List<AbisQueueDetails> abisQueueDetailsList = new ArrayList<>();
		String registrationProcessorAbis = Utilities.getJson(configServerFileStorageURL, registrationProcessorAbisJson);
		JSONObject regProcessorAbisJson;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"Utilities::getAbisQueueDetails()::entry");

		try {
			regProcessorAbisJson = JsonUtil.objectMapperReadValue(registrationProcessorAbis, JSONObject.class);

			JSONArray regProcessorAbisArray = JsonUtil.getJSONArray(regProcessorAbisJson, ABIS);

			for (Object jsonObject : regProcessorAbisArray) {
				AbisQueueDetails abisQueueDetails = new AbisQueueDetails();
				JSONObject json = new JSONObject((Map) jsonObject);
				String userName = validateAbisQueueJsonAndReturnValue(json, USERNAME);
				String password = validateAbisQueueJsonAndReturnValue(json, PASSWORD);
				String brokerUrl = validateAbisQueueJsonAndReturnValue(json, BROKERURL);
				String typeOfQueue = validateAbisQueueJsonAndReturnValue(json, TYPEOFQUEUE);
				String inboundQueueName = validateAbisQueueJsonAndReturnValue(json, INBOUNDQUEUENAME);
				String outboundQueueName = validateAbisQueueJsonAndReturnValue(json, OUTBOUNDQUEUENAME);
				String queueName = validateAbisQueueJsonAndReturnValue(json, NAME);
				MosipQueue mosipQueue = mosipConnectionFactory.createConnection(typeOfQueue, userName, password,
						brokerUrl);
				if (mosipQueue == null)
					throw new QueueConnectionNotFound(
							PlatformErrorMessages.RPR_PIS_ABIS_QUEUE_CONNECTION_NULL.getMessage());

				abisQueueDetails.setMosipQueue(mosipQueue);
				abisQueueDetails.setInboundQueueName(inboundQueueName);
				abisQueueDetails.setOutboundQueueName(outboundQueueName);
				abisQueueDetails.setName(queueName);
				abisQueueDetailsList.add(abisQueueDetails);

			}
		} catch (IOException e) {
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"Utilities::getAbisQueueDetails()::exit");

		return abisQueueDetailsList;

	}

	/**
	 * Gets registration processor mapping json from config and maps to
	 * RegistrationProcessorIdentity java class
	 *
	 * @return
	 * @throws IOException
	 */
	public RegistrationProcessorIdentity getRegistrationProcessorIdentityJson() throws IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"Utilities::getRegistrationProcessorIdentityJson()::entry");

		String getIdentityJsonString = Utilities.getJson(configServerFileStorageURL, getRegProcessorIdentityJson);
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"Utilities::getRegistrationProcessorIdentityJson()::exit");
		return mapIdentityJsonStringToObject.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);

	}

	/**
	 * Retrieves the identity json from HDFS by registrationId
	 *
	 * @param registrationId
	 * @return
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws ApisResourceAccessException
	 * @throws PacketDecryptionFailureException
	 */
	public JSONObject getDemographicIdentityJSONObject(String registrationId) throws IOException,
			PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getDemographicIdentityJSONObject()::entry");

		InputStream idJsonStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
		byte[] bytearray = IOUtils.toByteArray(idJsonStream);
		String jsonString = new String(bytearray);
		JSONObject demographicIdentityJson = (JSONObject) JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
		JSONObject demographicIdentity = JsonUtil.getJSONObject(demographicIdentityJson,
				getRegProcessorDemographicIdentity);

		if (demographicIdentity == null) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Utilities::getDemographicIdentityJSONObject():: error with error message "
							+ PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getDemographicIdentityJSONObject()::exit");

		return demographicIdentity;

	}

	/**
	 * Get UIN from identity json (used only for update/res update/activate/de
	 * activate packets)
	 *
	 * @param registrationId
	 * @return
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws ApisResourceAccessException
	 * @throws PacketDecryptionFailureException
	 */
	public Long getUIn(String registrationId) throws IOException, PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getUIn()::entry");

		JSONObject demographicIdentity = getDemographicIdentityJSONObject(registrationId);
		if (demographicIdentity == null) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Utilities::getUIn():: error with error message "
							+ PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		}
		Number number = JsonUtil.getJSONValue(demographicIdentity, UIN);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getUIn()::exit");

		return number != null ? number.longValue() : null;

	}

	public String getElapseStatus(InternalRegistrationStatusDto registrationStatusDto, String transactionType) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"Utilities::getElapseStatus()::entry");

		if (registrationStatusDto.getLatestTransactionTypeCode().equalsIgnoreCase(transactionType)) {
			LocalDateTime createdDateTime = registrationStatusDto.getCreateDateTime();
			LocalDateTime currentDateTime = LocalDateTime.now();
			Duration duration = Duration.between(createdDateTime, currentDateTime);
			long secondsDiffernce = duration.getSeconds();
			if (secondsDiffernce > elapseTime) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
						"Utilities::getElapseStatus()::exit and value is:  " + RE_PROCESSING);

				return RE_PROCESSING;
			} else {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
						"Utilities::getElapseStatus()::exit and value is:  " + HANDLER);

				return HANDLER;
			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"Utilities::getElapseStatus()::exit and value is:  " + NEW_PACKET);

		return NEW_PACKET;
	}

	public String getLatestTransactionId(String registrationId) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getLatestTransactionId()::entry");
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getLatestTransactionId()::exit");
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

	/**
	 * retrieve UIN from IDRepo by registration id
	 *
	 * @param regId
	 * @return
	 * @throws ApisResourceAccessException
	 * @throws IdRepoAppException
	 * @throws IOException
	 */
	public JSONObject retrieveUIN(String regId) throws ApisResourceAccessException, IdRepoAppException, IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "Utilities::retrieveUIN()::entry");

		if (regId != null) {
			List<String> pathSegments = new ArrayList<>();
			pathSegments.add(regId);
			IdResponseDTO1 idResponseDto = new IdResponseDTO1();
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, "Utilities::retrieveUIN():: RETRIEVEIDENTITYFROMRID GET service call Started");

			idResponseDto = (IdResponseDTO1) restClientService.getApi(ApiName.RETRIEVEIDENTITYFROMRID, pathSegments, "",
					"", IdResponseDTO1.class);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), "",
					"Utilities::retrieveUIN():: RETRIEVEIDENTITYFROMRID GET service call ended successfully");

			if (!idResponseDto.getErrors().isEmpty()) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), regId,
						"Utilities::retrieveUIN():: error with error message "
								+ PlatformErrorMessages.RPR_PVM_INVALID_UIN.getMessage() + " "
								+ idResponseDto.getErrors().toString());
				throw new IdRepoAppException(
						PlatformErrorMessages.RPR_PVM_INVALID_UIN.getMessage() + idResponseDto.getErrors().toString());
			}
			ObjectMapper objMapper = new ObjectMapper();
			return objMapper.convertValue(idResponseDto.getResponse().getIdentity(), JSONObject.class);

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "Utilities::retrieveUIN()::exit regId is null");

		return null;
	}

	public PacketMetaInfo getPacketMetaInfo(String registrationId) throws PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException, IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getPacketMetaInfo():: entry");
		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKET_META_INFO.name());
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getPacketMetaInfo():: exit");
		return (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream, PacketMetaInfo.class);
	}

	public List<Documents> getAllDocumentsByRegId(String regId) throws IOException, PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "Utilities::getAllDocumentsByRegId():: entry");

		List<Documents> applicantDocuments = new ArrayList<>();
		JSONObject idJson = null;
		idJson = getDemographicIdentityJSONObject(regId);
		regProcessorIdentityJson = getRegistrationProcessorIdentityJson();
		String proofOfAddressLabel = regProcessorIdentityJson.getIdentity().getPoa().getValue();
		String proofOfDateOfBirthLabel = regProcessorIdentityJson.getIdentity().getPob().getValue();
		String proofOfIdentityLabel = regProcessorIdentityJson.getIdentity().getPoi().getValue();
		String proofOfRelationshipLabel = regProcessorIdentityJson.getIdentity().getPor().getValue();
		String applicantBiometricLabel = regProcessorIdentityJson.getIdentity().getIndividualBiometrics().getValue();

		JSONObject proofOfAddress = JsonUtil.getJSONObject(idJson, proofOfAddressLabel);
		JSONObject proofOfDateOfBirth = JsonUtil.getJSONObject(idJson, proofOfDateOfBirthLabel);
		JSONObject proofOfIdentity = JsonUtil.getJSONObject(idJson, proofOfIdentityLabel);
		JSONObject proofOfRelationship = JsonUtil.getJSONObject(idJson, proofOfRelationshipLabel);
		JSONObject applicantBiometric = JsonUtil.getJSONObject(idJson, applicantBiometricLabel);
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
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "Utilities::getAllDocumentsByRegId():: exit");

		return applicantDocuments;
	}

	private Documents getIdDocumnet(String registrationId, String folderPath, JSONObject idDocObj, String idDocLabel)
			throws IOException, PacketDecryptionFailureException, ApisResourceAccessException,
			io.mosip.kernel.core.exception.IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getIdDocumnet():: entry");

		Documents documentsInfoDto = new Documents();
		InputStream poiStream = adapter.getFile(registrationId, folderPath + FILE_SEPARATOR + idDocObj.get("value"));
		documentsInfoDto.setValue(CryptoUtil.encodeBase64(IOUtils.toByteArray(poiStream)));
		documentsInfoDto.setCategory(idDocLabel);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Utilities::getIdDocumnet():: exit");

		return documentsInfoDto;
	}

	private int calculateAge(String applicantDob) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"Utilities::calculateAge():: entry");

		DateFormat sdf = new SimpleDateFormat(dobFormat);
		Date birthDate = null;
		try {
			birthDate = sdf.parse(applicantDob);

		} catch (ParseException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "Utilities::calculateAge():: error with error message "
							+ PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getMessage());
			throw new ParsingException(PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getCode(), e);
		}
		LocalDate ld = new java.sql.Date(birthDate.getTime()).toLocalDate();
		Period p = Period.between(ld, LocalDate.now());
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"Utilities::calculateAge():: exit");

		return p.getYears();

	}

	private String validateAbisQueueJsonAndReturnValue(JSONObject jsonObject, String key) {

		String value = JsonUtil.getJSONValue(jsonObject, key);
		if (value == null) {

			throw new RegistrationProcessorUnCheckedException(
					PlatformErrorMessages.ABIS_QUEUE_JSON_VALIDATION_FAILED.getCode(),
					PlatformErrorMessages.ABIS_QUEUE_JSON_VALIDATION_FAILED.getMessage() + "::" + key);
		}

		return value;
	}

	@SuppressWarnings("unchecked")
	public String getUinByVid(String vid) throws ApisResourceAccessException, VidCreationException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"Utilities::getUinByVid():: entry");
		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(vid);
		String uin = null;
		ResponseWrapper<VidResDTO> response = new ResponseWrapper<>();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"Stage::methodname():: RETRIEVEIUINBYVID GET service call Started");

		response = (ResponseWrapper<VidResDTO>) restClientService.getApi(ApiName.GETUINBYVID, pathSegments, "", "",
				ResponseWrapper.class);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), "",
				"Utilities::getUinByVid():: RETRIEVEIUINBYVID GET service call ended successfully");

		if (!response.getErrors().isEmpty()) {
			throw new VidCreationException(PlatformErrorMessages.RPR_PGS_VID_EXCEPTION.getMessage(),
					"VID creation exception");

		} else {
			uin = response.getResponse().getUin().toString();
		}
		return uin;
	}

	@SuppressWarnings("unchecked")
	public boolean linkRegIdWrtUin(String registrationID, String uin) throws ApisResourceAccessException {

		IdResponseDTO idResponse = null;
		RequestDto requestDto = new RequestDto();
		if (uin != null) {

			JSONObject identityObject = new JSONObject();
			identityObject.put(UIN, uin);

			requestDto.setRegistrationId(registrationID);
			requestDto.setIdentity(identityObject);

			IdRequestDto idRequestDTO = new IdRequestDto();
			idRequestDTO.setId(idRepoUpdate);
			idRequestDTO.setRequest(requestDto);
			idRequestDTO.setMetadata(null);
			idRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
			idRequestDTO.setVersion(vidVersion);

			idResponse = (IdResponseDTO) restClientService.patchApi(ApiName.IDREPOSITORY, null, "", "", idRequestDTO,
					IdResponseDTO.class);

			if (idResponse != null && idResponse.getResponse() != null) {

				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationID, " UIN Linked with the RegID");

				return true;
			} else {

				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationID,
						" UIN not Linked with the RegID ");
				return false;
			}

		} else {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationID, " UIN is null ");
		}

		return false;
	}

}
