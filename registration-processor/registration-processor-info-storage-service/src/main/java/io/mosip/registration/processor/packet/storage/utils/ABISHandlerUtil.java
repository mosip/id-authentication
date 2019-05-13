package io.mosip.registration.processor.packet.storage.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;

@Component
public class ABISHandlerUtil {

	@Autowired
	Utilities utilities;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	private PacketInfoDao packetInfoDao;

	private static final String IDENTIFY = "IDENTIFY";

	private static final String TYPE = "type";

	private static final String ALL = "ALL";

	private static final String UIN = "UIN";

	private static final String NEW = "NEW";

	private static final String POST_API_PROCESS = "POST_API_PROCESS";

	public List<String> getUniqueRegIds(String registrationId, String status)
			throws ApisResourceAccessException, IOException {

		String latestTransactionId = utilities.getLatestTransactionId(registrationId);

		List<String> regBioRefIds = packetInfoDao.getAbisRefMatchedRefIdByRid(registrationId);

		List<String> machedRefIds = new ArrayList<>();
		List<String> uniqueRIDs = new ArrayList<>();
		List<AbisResponseDetDto> abisResponseDetDtoList = new ArrayList<>();

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

				uniqueRIDs = getUniqueRegIds(packetInfoDao.getAbisRefRegIdsByMatchedRefIds(machedRefIds),
						registrationId, status);
			}
		}

		return uniqueRIDs;

	}

	public String getPacketStatus(InternalRegistrationStatusDto registrationStatusDto, String transactionType) {
		if (getMatchedRegIds(registrationStatusDto.getRegistrationId()).isEmpty()) {
			return NEW;
		}
		return POST_API_PROCESS;
	}

	private List<AbisRequestDto> getMatchedRegIds(String registrationId) {
		String latestTransactionId = utilities.getLatestTransactionId(registrationId);

		List<String> regBioRefIds = packetInfoDao.getAbisRefMatchedRefIdByRid(registrationId);

		List<AbisRequestDto> abisRequestDtoList = new ArrayList<>();

		if (!regBioRefIds.isEmpty()) {
			abisRequestDtoList = packetInfoManager.getInsertOrIdentifyRequest(regBioRefIds.get(0), latestTransactionId);
		}

		return abisRequestDtoList;
	}

	private List<String> getUniqueRegIds(List<String> matchedRegistrationIds, String registrationId, String status)
			throws ApisResourceAccessException, IOException {

		Map<String, String> filteredRegMap = new LinkedHashMap<>();
		List<String> filteredRIds = new ArrayList<>();

		for (String machedRegId : matchedRegistrationIds) {

			Number matchedUin = getUinFromIDRepo(machedRegId);

			if (status.equalsIgnoreCase(SyncTypeDto.UPDATE.toString())) {
				Number packetUin = utilities.getUIn(registrationId);
				if (matchedUin != null && packetUin != matchedUin) {
					filteredRegMap.put(matchedUin.toString(), machedRegId);
				}
			}
			if (status.equalsIgnoreCase(SyncTypeDto.NEW.toString()) && matchedUin != null) {
				filteredRegMap.put(matchedUin.toString(), machedRegId);
			}

			if (!filteredRegMap.isEmpty()) {
				filteredRIds = new ArrayList<>(filteredRegMap.values());
			}

		}

		return filteredRIds;

	}

	@SuppressWarnings("unchecked")
	private Number getUinFromIDRepo(String machedRegId) throws IOException, ApisResourceAccessException {
		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(machedRegId);
		Number uin = null;

		@SuppressWarnings("unchecked")
		ResponseWrapper<IdResponseDTO> response;

		response = (ResponseWrapper<IdResponseDTO>) restClientService.getApi(ApiName.IDREPOSITORY, pathSegments, TYPE,
				ALL, ResponseWrapper.class);

		if (response.getResponse() != null) {
			Gson gsonObj = new Gson();
			String jsonString = gsonObj.toJson(response.getResponse());
			JSONObject identityJson = JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
			JSONObject demographicIdentity = JsonUtil.getJSONObject(identityJson,
					utilities.getGetRegProcessorDemographicIdentity());
			uin = JsonUtil.getJSONValue(demographicIdentity, UIN);
		}
		return uin;
	}
}
