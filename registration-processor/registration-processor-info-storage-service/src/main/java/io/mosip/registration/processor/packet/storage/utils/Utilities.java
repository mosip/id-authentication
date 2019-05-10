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
import java.util.LinkedHashMap;
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
import com.google.gson.Gson;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO1;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.QueueConnectionNotFound;
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

	private static final String UIN = "UIN";
	public static final String FILE_SEPARATOR = "\\";
	private static final String RE_PROCESSING = "re-processing";
	private static final String HANDLER = "handler";
	private static final String NEW_PACKET = "New-packet";

	@Autowired
	private FileSystemAdapter adapter;

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

	/*
	 * @Value("${registration.processor.document.category}") private String
	 * getRegProcessorDocumentCategory;
	 * 
	 * @Value("${registration.processor.applicant.type}") private String
	 * getRegProcessorApplicantType;
	 */

	@Value("${registration.processor.applicant.dob.format}")
	private String dobFormat;

	@Value("${registration.processor.reprocess.elapse.time}")
	private long elapseTime;

	@Value("${registration.processor.abis.json}")
	private String registrationProcessorAbisJson;

	@Autowired
	private PacketInfoDao packetInfoDao;

	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	private static final String REG_TYPE_NEW = "New";
	private static final String REG_TYPE_UPDATE = "Update";
	private static final String IDENTIFY = "IDENTIFY";

	private static final String INBOUNDQUEUENAME = "inboundQueueName";
	private static final String OUTBOUNDQUEUENAME = "outboundQueueName";
	private static final String ABIS = "abis";
	private static final String USERNAME = "userName";
	private static final String PASSWORD = "password";
	private static final String BROKERURL = "brokerUrl";
	private static final String TYPEOFQUEUE = "typeOfQueue";

	public static String getJson(String configServerFileStorageURL, String uri) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configServerFileStorageURL + uri, String.class);
	}

	public List<List<String>> getInboundOutBoundAddressList() throws RegistrationProcessorCheckedException {
		String registrationProcessorAbis = Utilities.getJson(configServerFileStorageURL, registrationProcessorAbisJson);
		List<String> inBoundAddressList = new ArrayList<>();
		List<String> outBountAddressList = new ArrayList<>();

		List<List<String>> inboundOutBoundList = new ArrayList<>();
		try {
			JSONObject regProcessorAbisJson;

			regProcessorAbisJson = JsonUtil.objectMapperReadValue(registrationProcessorAbis, JSONObject.class);

			JSONArray regProcessorAbisArray = JsonUtil.getJSONArray(regProcessorAbisJson, ABIS);
			for (Object object : regProcessorAbisArray) {
				JSONObject jsonObject = new JSONObject((Map) object);
				inBoundAddressList.add(JsonUtil.getJSONValue((JSONObject) jsonObject, INBOUNDQUEUENAME));
				outBountAddressList.add(JsonUtil.getJSONValue((JSONObject) jsonObject, OUTBOUNDQUEUENAME));
				inboundOutBoundList.add(inBoundAddressList);
				inboundOutBoundList.add(outBountAddressList);

			}
		} catch (IOException e) {
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}
		return inboundOutBoundList;
	}

	public List<MosipQueue> getMosipQueuesForAbis() throws RegistrationProcessorCheckedException {
		String registrationProcessorAbis = Utilities.getJson(configServerFileStorageURL, registrationProcessorAbisJson);
		List<MosipQueue> mosipQueueList = new ArrayList<>();

		JSONObject regProcessorAbisJson;
		try {
			regProcessorAbisJson = JsonUtil.objectMapperReadValue(registrationProcessorAbis, JSONObject.class);

			JSONArray regProcessorAbisArray = JsonUtil.getJSONArray(regProcessorAbisJson, ABIS);

			for (Object jsonObject : regProcessorAbisArray) {
				JSONObject json = new JSONObject((Map) jsonObject);
				String userName = JsonUtil.getJSONValue((JSONObject) json, USERNAME);
				String password = JsonUtil.getJSONValue((JSONObject) json, PASSWORD);
				String brokerUrl = JsonUtil.getJSONValue((JSONObject) json, BROKERURL);
				String typeOfQueue = JsonUtil.getJSONValue((JSONObject) json, TYPEOFQUEUE);
				MosipQueue mosipQueue = mosipConnectionFactory.createConnection(typeOfQueue, userName, password,
						brokerUrl);
				if (mosipQueue == null)
					throw new QueueConnectionNotFound(
							PlatformErrorMessages.RPR_PIS_ABIS_QUEUE_CONNECTION_NULL.getMessage());
				mosipQueueList.add(mosipQueue);

			}
		} catch (IOException e) {
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}
		return mosipQueueList;

	}

	public int getApplicantAge(String registrationId)
			throws IOException, ApisResourceAccessException, ParseException, IdRepoAppException {
		RegistrationProcessorIdentity regProcessorIdentityJson = getRegistrationProcessorIdentityJson();
		String ageKey = regProcessorIdentityJson.getIdentity().getAge().getValue();
		String dobKey = regProcessorIdentityJson.getIdentity().getDob().getValue();

		JSONObject demographicIdentity = getDemographicIdentityJSONObject(registrationId);
		String applicantDob = JsonUtil.getJSONValue(demographicIdentity, dobKey);
		Integer applicantAge = JsonUtil.getJSONValue(demographicIdentity, ageKey);
		if (applicantDob != null) {
			return calculateAge(applicantDob);
		} else if (applicantAge != null) {
			return applicantAge;

		} else {
			Long uin = getUIn(registrationId);
			JSONObject identityJSONOject = retrieveIdrepoJson(uin);
			String idRepoApplicantDob = JsonUtil.getJSONValue(identityJSONOject, dobKey);
			if (idRepoApplicantDob != null)
				return calculateAge(idRepoApplicantDob);
			Integer idRepoApplicantAge = JsonUtil.getJSONValue(demographicIdentity, ageKey);
			return idRepoApplicantAge != null ? idRepoApplicantAge : 0;

		}

	}

	public RegistrationProcessorIdentity getRegistrationProcessorIdentityJson() throws IOException {
		String getIdentityJsonString = Utilities.getJson(configServerFileStorageURL, getRegProcessorIdentityJson);
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		return mapIdentityJsonStringToObject.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);
	}

	public JSONObject getDemographicIdentityJSONObject(String registrationId) throws IOException {

		InputStream idJsonStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
		byte[] bytearray = IOUtils.toByteArray(idJsonStream);
		String jsonString = new String(bytearray);
		JSONObject demographicIdentityJson = (JSONObject) JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
		JSONObject demographicIdentity = JsonUtil.getJSONObject(demographicIdentityJson,
				getRegProcessorDemographicIdentity);

		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

		return demographicIdentity;

	}

	private int calculateAge(String applicantDob) throws ParseException {
		DateFormat sdf = new SimpleDateFormat(dobFormat);
		Date birthDate = sdf.parse(applicantDob);
		LocalDate ld = new java.sql.Date(birthDate.getTime()).toLocalDate();
		Period p = Period.between(ld, LocalDate.now());
		return p.getYears();
	}

	public Long getUIn(String registrationId) throws IOException {
		JSONObject demographicIdentity = getDemographicIdentityJSONObject(registrationId);
		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		Number number = JsonUtil.getJSONValue(demographicIdentity, UIN);
		return number != null ? number.longValue() : null;

	}

	public JSONObject retrieveIdrepoJson(Long uin) throws ApisResourceAccessException, IdRepoAppException {

		if (uin != null) {
			List<String> pathSegments = new ArrayList<>();
			pathSegments.add(String.valueOf(uin));
			IdResponseDTO1 idResponseDto = (IdResponseDTO1) restClientService.getApi(ApiName.RETRIEVEIDENTITY,
					pathSegments, "", "", IdResponseDTO1.class);
			if (!idResponseDto.getErrors().isEmpty())
				throw new IdRepoAppException(
						PlatformErrorMessages.RPR_PVM_INVALID_UIN.getMessage() + idResponseDto.getErrors().toString());

			idResponseDto.getResponse().getIdentity();
			ObjectMapper objMapper = new ObjectMapper();
			return objMapper.convertValue(idResponseDto.getResponse().getIdentity(), JSONObject.class);

		}

		return null;
	}

	public String getElapseStatus(InternalRegistrationStatusDto registrationStatusDto, String transactionType) {

		if (registrationStatusDto.getLatestTransactionTypeCode().equalsIgnoreCase(transactionType)) {
			LocalDateTime createdDateTime = registrationStatusDto.getCreateDateTime();
			LocalDateTime currentDateTime = LocalDateTime.now();
			Duration duration = Duration.between(createdDateTime, currentDateTime);
			long secondsDiffernce = duration.getSeconds();
			if (secondsDiffernce > elapseTime)
				return RE_PROCESSING;
			else
				return HANDLER;
		}
		return NEW_PACKET;
	}

	public String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

	public List<String> getMatchedRegistrationIds(InternalRegistrationStatusDto registrationStatusDto, String status)
			throws ApisResourceAccessException, IOException {

		String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());
		Map<String, String> filteredRegMap = new LinkedHashMap<>();
		List<String> machedRefIds = new ArrayList<>();
		List<String> filteredRIds = new ArrayList<>();
		List<AbisResponseDetDto> abisResponseDetDtoList = new ArrayList<>();

		List<String> regBioRefIds = packetInfoDao
				.getAbisRefMatchedRefIdByRid(registrationStatusDto.getRegistrationId());
		if (!regBioRefIds.isEmpty()) {
			List<AbisResponseDto> abisResponseDtoList = packetInfoManager.getAbisResponseRecords(regBioRefIds.get(0),
					latestTransactionId, IDENTIFY);
			for (AbisResponseDto abisResponseDto : abisResponseDtoList) {
				abisResponseDetDtoList.addAll(packetInfoManager.getAbisResponseDetails(abisResponseDto.getId()));
			}
			if (!abisResponseDetDtoList.isEmpty()) {
				for (AbisResponseDetDto abisResponseDetDto : abisResponseDetDtoList) {
					machedRefIds.add(abisResponseDetDto.getMatchedBioRefId());
				}
				List<String> matchedRegistrationIds = packetInfoDao.getAbisRefRegIdsByMatchedRefIds(machedRefIds);

				for (String machedRegId : matchedRegistrationIds) {
					List<String> pathSegments = new ArrayList<>();
					pathSegments.add(machedRegId);
					@SuppressWarnings("unchecked")
					ResponseWrapper<IdResponseDTO> response = (ResponseWrapper<IdResponseDTO>) restClientService
							.getApi(ApiName.IDREPOSITORY, pathSegments, "type", "all", ResponseWrapper.class);
					Gson gsonObj = new Gson();
					String jsonString = gsonObj.toJson(response.getResponse());
					JSONObject identityJson = (JSONObject) JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
					JSONObject demographicIdentity = JsonUtil.getJSONObject(identityJson,
							getGetRegProcessorDemographicIdentity());
					Number matchedUin = JsonUtil.getJSONValue(demographicIdentity, "UIN");

					if (status.equalsIgnoreCase(REG_TYPE_UPDATE)) {
						Number packetUin = getUIn(registrationStatusDto.getRegistrationId());
						if (matchedUin != null && packetUin != matchedUin) {
							filteredRegMap.put(matchedUin.toString(), machedRegId);
						}
					}
					if (status.equalsIgnoreCase(REG_TYPE_NEW) && matchedUin != null) {
						filteredRegMap.put(matchedUin.toString(), machedRegId);
					}

					if (!filteredRegMap.isEmpty()) {
						filteredRIds = new ArrayList<>(filteredRegMap.values());
					}

				}
			}
		}

		return filteredRIds;

	}

}
