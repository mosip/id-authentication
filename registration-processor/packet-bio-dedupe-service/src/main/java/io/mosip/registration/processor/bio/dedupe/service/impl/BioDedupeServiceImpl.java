package io.mosip.registration.processor.bio.dedupe.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponceDto;
import io.mosip.registration.processor.abis.dto.CandidatesDto;
import io.mosip.registration.processor.abis.dto.IdentityRequestDto;
import io.mosip.registration.processor.abis.dto.IdentityResponceDto;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

@RefreshScope
@Service
public class BioDedupeServiceImpl implements BioDedupeService {
	private AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();
	private IdentityRequestDto identityRequestDto = new IdentityRequestDto();

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Value("${registration.processor.abis.url}")
	private String url;

	@Value("${registration.processor.abis.maxResults}")
	private String maxResults;

	@Value("${registration.processor.abis.targetFPIR}")
	private String targetFPIR;

	@Value("${registration.processor.abis.threshold}")
	private Integer threshold;

	@Override
	public String insertBiometrics(String registrationId) throws ApisResourceAccessException {

		String requestId = uuidGenerator();
		String referenceId = uuidGenerator();

		abisInsertRequestDto.setRequestId(requestId);
		abisInsertRequestDto.setReferenceId(referenceId);
		abisInsertRequestDto.setReferenceURL(url + registrationId);

		RegAbisRefDto regAbisRefDto = new RegAbisRefDto();
		regAbisRefDto.setAbis_ref_id(referenceId);
		regAbisRefDto.setReg_id(registrationId);

		packetInfoManager.saveAbisRef(regAbisRefDto);
		// chk
		AbisInsertResponceDto authResponseDTO = (AbisInsertResponceDto) restClientService
				.postApi(ApiName.BIODEDUPEINSERT, "", "", abisInsertRequestDto, AbisInsertResponceDto.class);

		return authResponseDTO.getReturnValue();

	}

	@Override
	public List<String> performDedupe(String registrationId) throws ApisResourceAccessException {
		List<String> duplicates = new ArrayList<>();
		List<String> abisResponseDuplicates = new ArrayList<>();

		String requestId = uuidGenerator();

		String referenceId = packetInfoManager.getReferenceIdByRid(registrationId).get(0);

		identityRequestDto.setId("Identify");
		identityRequestDto.setVer("1.0");
		identityRequestDto.setRequestId(requestId);
		identityRequestDto.setReferenceId(referenceId);
		
		String timeStamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L);
		identityRequestDto.setTimestamp(timeStamp);
		identityRequestDto.setMaxResults(maxResults);
		identityRequestDto.setTargetFPIR(targetFPIR);

		// call Identify Api to get duplicate ids
		IdentityResponceDto responsedto = (IdentityResponceDto) restClientService.postApi(ApiName.BIODEDUPEPOTENTIAL,
				"", "", identityRequestDto, IdentityResponceDto.class);

		if (responsedto.getReturnValue() == "2") {
			// throw exception with failure reasons
		}

		CandidatesDto[] candidateList = responsedto.getCandidateList().getCandidates();

		for (CandidatesDto candidate : candidateList) {
			if (Integer.parseInt(candidate.getScaledScore()) >= threshold) {
				String regId = packetInfoManager.getRidByReferenceId(candidate.getReferenceId()).get(0);
				abisResponseDuplicates.add(regId);
			}
		}

		for (String duplicateReg : abisResponseDuplicates) {
			String uin = packetInfoManager.findDemoById(duplicateReg).get(0).getUin();
			if (!uin.isEmpty()) {
				duplicates.add(duplicateReg);
			}
		}

		return duplicates;
	}

	private String uuidGenerator() {
		return UUID.randomUUID().toString();
	}

}
