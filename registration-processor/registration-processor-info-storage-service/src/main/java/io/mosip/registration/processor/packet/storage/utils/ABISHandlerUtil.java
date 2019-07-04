package io.mosip.registration.processor.packet.storage.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.constant.AbisConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.manager.idreposervice.IdRepoService;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;

/**
 * The Class ABISHandlerUtil.
 * 
 * @author Nagalakshmi
 * @author Horteppa
 */
@Component
public class ABISHandlerUtil {

	/** The utilities. */
	@Autowired
	Utilities utilities;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The packet info dao. */
	@Autowired
	private PacketInfoDao packetInfoDao;

	@Autowired
	private IdRepoService idRepoService;

	/**
	 * Gets the unique reg ids.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param registrationType
	 *            the registration type
	 * @return the unique reg ids
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	public List<String> getUniqueRegIds(String registrationId, String registrationType)
			throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {

		String latestTransactionId = utilities.getLatestTransactionId(registrationId);

		List<String> regBioRefIds = packetInfoDao.getAbisRefMatchedRefIdByRid(registrationId);

		List<String> machedRefIds = new ArrayList<>();
		List<String> uniqueRIDs = new ArrayList<>();
		List<AbisResponseDetDto> abisResponseDetDtoList = new ArrayList<>();

		if (!regBioRefIds.isEmpty()) {
			List<AbisResponseDto> abisResponseDtoList = packetInfoManager.getAbisResponseRecords(regBioRefIds.get(0),
					latestTransactionId, AbisConstant.IDENTIFY);
			for (AbisResponseDto abisResponseDto : abisResponseDtoList) {
				abisResponseDetDtoList.addAll(packetInfoManager.getAbisResponseDetails(abisResponseDto.getId()));
			}
			if (!abisResponseDetDtoList.isEmpty()) {
				for (AbisResponseDetDto abisResponseDetDto : abisResponseDetDtoList) {
					machedRefIds.add(abisResponseDetDto.getMatchedBioRefId());
				}
				List<String> matchedRegIds = packetInfoDao.getAbisRefRegIdsByMatchedRefIds(machedRefIds);
				List<String> processingRegIds = packetInfoDao.getProcessedOrProcessingRegIds(matchedRegIds,
						RegistrationTransactionStatusCode.IN_PROGRESS.toString());
				List<String> processedRegIds = packetInfoDao.getProcessedOrProcessingRegIds(matchedRegIds,
						RegistrationTransactionStatusCode.PROCESSED.toString());
				uniqueRIDs = getUniqueRegIds(processedRegIds, registrationId, registrationType);
				uniqueRIDs.addAll(processingRegIds);
			}
		}

		return uniqueRIDs;

	}

	/**
	 * Gets the packet status.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 * @return the packet status
	 */
	public String getPacketStatus(InternalRegistrationStatusDto registrationStatusDto) {
		if (getMatchedRegIds(registrationStatusDto.getRegistrationId()).isEmpty()) {
			return AbisConstant.PRE_ABIS_IDENTIFICATION;
		}
		return AbisConstant.POST_ABIS_IDENTIFICATION;
	}

	/**
	 * Gets the matched reg ids.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the matched reg ids
	 */
	private List<AbisRequestDto> getMatchedRegIds(String registrationId) {
		String latestTransactionId = utilities.getLatestTransactionId(registrationId);

		List<String> regBioRefIds = packetInfoDao.getAbisRefMatchedRefIdByRid(registrationId);

		List<AbisRequestDto> abisRequestDtoList = new ArrayList<>();

		if (!regBioRefIds.isEmpty()) {
			abisRequestDtoList = packetInfoManager.getInsertOrIdentifyRequest(regBioRefIds.get(0), latestTransactionId);
		}

		return abisRequestDtoList;
	}

	/**
	 * Gets the unique reg ids.
	 *
	 * @param matchedRegistrationIds
	 *            the matched registration ids
	 * @param registrationId
	 *            the registration id
	 * @param registrationType
	 *            the registration type
	 * @return the unique reg ids
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	private List<String> getUniqueRegIds(List<String> matchedRegistrationIds, String registrationId,
			String registrationType) throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {

		Map<String, String> filteredRegMap = new LinkedHashMap<>();
		List<String> filteredRIds = new ArrayList<>();

		for (String machedRegId : matchedRegistrationIds) {

			Number matchedUin = idRepoService.getUinByRid(machedRegId,
					utilities.getGetRegProcessorDemographicIdentity());

			if (registrationType.equalsIgnoreCase(SyncTypeDto.UPDATE.toString())) {
				Number packetUin = utilities.getUIn(registrationId);
				if (matchedUin != null && !packetUin.equals(matchedUin)) {
					filteredRegMap.put(matchedUin.toString(), machedRegId);
				}
			}
			if (registrationType.equalsIgnoreCase(SyncTypeDto.NEW.toString()) && matchedUin != null) {
				filteredRegMap.put(matchedUin.toString(), machedRegId);
			}

			if (registrationType.equalsIgnoreCase(SyncTypeDto.LOST.toString()) && matchedUin != null) {
				filteredRegMap.put(matchedUin.toString(), machedRegId);
			}
			if (!filteredRegMap.isEmpty()) {
				filteredRIds = new ArrayList<>(filteredRegMap.values());
			}

		}

		return filteredRIds;

	}

}
