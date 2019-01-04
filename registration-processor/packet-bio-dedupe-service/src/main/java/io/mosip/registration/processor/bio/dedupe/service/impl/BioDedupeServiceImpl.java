package io.mosip.registration.processor.bio.dedupe.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponceDto;
import io.mosip.registration.processor.abis.dto.CandidatesDto;
import io.mosip.registration.processor.abis.dto.IdentityRequestDto;
import io.mosip.registration.processor.abis.dto.IdentityResponceDto;
import io.mosip.registration.processor.bio.dedupe.exception.ABISAbortException;
import io.mosip.registration.processor.bio.dedupe.exception.ABISInternalError;
import io.mosip.registration.processor.bio.dedupe.exception.UnableToServeRequestABISException;
import io.mosip.registration.processor.bio.dedupe.exception.UnexceptedError;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PacketStructure;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

@RefreshScope
@Service
public class BioDedupeServiceImpl implements BioDedupeService {

	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeServiceImpl.class);

	private AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();

	private IdentityRequestDto identifyRequestDto = new IdentityRequestDto();

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Value("${registration.processor.biometric.reference.url}")
	private String url;

	@Value("${registration.processor.abis.maxResults}")
	private Integer maxResults;

	@Value("${registration.processor.abis.targetFPIR}")
	private Integer targetFPIR;

	@Value("${registration.processor.abis.threshold}")
	private Integer threshold;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> filesystemCephAdapterImpl;

	@Override
	public String insertBiometrics(String registrationId) throws ApisResourceAccessException {

		String insertStatus = "failure";
		String requestId = uuidGenerator();
		String referenceId = uuidGenerator();

		abisInsertRequestDto.setRequestId(requestId);
		abisInsertRequestDto.setReferenceId(referenceId);
		abisInsertRequestDto.setReferenceURL(url + registrationId);
		String timeStamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L);
		abisInsertRequestDto.setTimestamp(timeStamp);

		RegAbisRefDto regAbisRefDto = new RegAbisRefDto();
		regAbisRefDto.setAbis_ref_id(referenceId);
		regAbisRefDto.setReg_id(registrationId);

		packetInfoManager.saveAbisRef(regAbisRefDto);
		// chk
		AbisInsertResponceDto authResponseDTO = (AbisInsertResponceDto) restClientService
				.postApi(ApiName.BIODEDUPEINSERT, "", "", abisInsertRequestDto, AbisInsertResponceDto.class);

		if (authResponseDTO.getReturnValue() == "1")
			insertStatus = "success";
		else
			throwException(authResponseDTO.getFailureReason(), referenceId, requestId);

		return insertStatus;

	}

	private void throwException(int failureReason, String referenceId, String requestId) {

		if (failureReason == 1)
			throw new ABISInternalError(
					PlatformErrorMessages.RPR_BDD_ABIS_INTERNAL_ERROR.getMessage() + referenceId + " " + requestId);

		else if (failureReason == 2)
			throw new ABISAbortException(
					PlatformErrorMessages.RPR_BDD_ABIS_ABORT.getMessage() + referenceId + " " + requestId);

		else if (failureReason == 3)
			throw new UnexceptedError(
					PlatformErrorMessages.RPR_BDD_UNEXCEPTED_ERROR.getMessage() + referenceId + " " + requestId);

		else if (failureReason == 4)
			throw new UnableToServeRequestABISException(
					PlatformErrorMessages.RPR_BDD_UNABLE_TO_SERVE_REQUEST.getMessage() + referenceId + " " + requestId);

	}

	@Override
	public List<String> performDedupe(String registrationId) throws ApisResourceAccessException {
		List<String> duplicates = new ArrayList<>();
		List<String> abisResponseDuplicates = new ArrayList<>();

		String requestId = uuidGenerator();

		String referenceId = packetInfoManager.getReferenceIdByRid(registrationId).get(0);

		identifyRequestDto.setId("Identify");
		identifyRequestDto.setVer("1.0");
		identifyRequestDto.setRequestId(requestId);
		identifyRequestDto.setReferenceId(referenceId);

		String timeStamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L);
		identifyRequestDto.setTimestamp(timeStamp);
		identifyRequestDto.setMaxResults(maxResults);
		identifyRequestDto.setTargetFPIR(targetFPIR);

		// call Identify Api to get duplicate ids
		IdentityResponceDto responsedto = (IdentityResponceDto) restClientService.postApi(ApiName.BIODEDUPEPOTENTIAL,
				"", "", identifyRequestDto, IdentityResponceDto.class);

		if (responsedto.getReturnValue() == 2) {
			throwException(responsedto.getFailureReason(), referenceId, requestId);
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

	@Override
	public byte[] getFile(String registrationId) {
		byte[] file = null;

		// To do provide CBEF file name
		InputStream fileInStream = filesystemCephAdapterImpl.getFile(registrationId, PacketStructure.PACKETMETAINFO);
		try {
			file = IOUtils.toByteArray(fileInStream);
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage() + e.getMessage());
		}
		return file;
	}

}
