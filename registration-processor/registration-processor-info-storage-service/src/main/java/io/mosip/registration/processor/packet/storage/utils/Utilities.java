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

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO1;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.QueueConnectionNotFound;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
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

	/*@Value("${registration.processor.document.category}")
	private String getRegProcessorDocumentCategory;

	@Value("${registration.processor.applicant.type}")
	private String getRegProcessorApplicantType;*/

	@Value("${registration.processor.applicant.dob.format}")
	private String dobFormat;

	@Value("${registration.processor.reprocess.elapse.time}")
	private long elapseTime;

	@Value("${registration.processor.abis.json}")
	private String registrationProcessorAbisJson  ;


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

	public List<List<String>> getMInboundOutBoundAddressList() throws IOException {
		String registrationProcessorAbis = Utilities.getJson(getRegistrationProcessorAbisJson(),
				getGetRegProcessorIdentityJson());
		List<String> inBoundAddressList = new ArrayList<>();
		List<String> outBountAddressList = new ArrayList<>();

		List<List<String>> inboundOutBoundList = new ArrayList<>();
		JSONObject regProcessorAbisJson = JsonUtil.objectMapperReadValue(registrationProcessorAbis, JSONObject.class);
		JSONArray regProcessorAbisArray = JsonUtil.getJSONArray(regProcessorAbisJson, ABIS);
		for (Object jsonObject : regProcessorAbisArray) {
			if (jsonObject instanceof JSONObject) {
				inBoundAddressList.add(JsonUtil.getJSONValue((JSONObject) jsonObject, INBOUNDQUEUENAME));
				outBountAddressList.add(JsonUtil.getJSONValue((JSONObject) jsonObject, OUTBOUNDQUEUENAME));
				inboundOutBoundList.add(inBoundAddressList);
				inboundOutBoundList.add(outBountAddressList);

			}

		}
		return inboundOutBoundList;
	}

	public List<MosipQueue> getMosipQueuesForAbis() throws IOException {
		String registrationProcessorAbis = Utilities.getJson(getRegistrationProcessorAbisJson(),
				getGetRegProcessorIdentityJson());
		JSONObject regProcessorAbisJson = JsonUtil.objectMapperReadValue(registrationProcessorAbis, JSONObject.class);
		JSONArray regProcessorAbisArray = JsonUtil.getJSONArray(regProcessorAbisJson, ABIS);
		List<MosipQueue> mosipQueueList = new ArrayList<>();

		for (Object jsonObject : regProcessorAbisArray) {
			if (jsonObject instanceof JSONObject) {
				String userName = JsonUtil.getJSONValue((JSONObject) jsonObject, USERNAME);
				String password = JsonUtil.getJSONValue((JSONObject) jsonObject, PASSWORD);
				String brokerUrl = JsonUtil.getJSONValue((JSONObject) jsonObject, BROKERURL);
				String typeOfQueue = JsonUtil.getJSONValue((JSONObject) jsonObject, TYPEOFQUEUE);
				MosipQueue mosipQueue = mosipConnectionFactory.createConnection(typeOfQueue, userName, password,
						brokerUrl);
				if (mosipQueue == null)
					throw new QueueConnectionNotFound(
							PlatformErrorMessages.RPR_PIS_QUEUE_ABIS_QUEUE_CONNECTION_NULL.getMessage());
				mosipQueueList.add(mosipQueue);

			}

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

}
