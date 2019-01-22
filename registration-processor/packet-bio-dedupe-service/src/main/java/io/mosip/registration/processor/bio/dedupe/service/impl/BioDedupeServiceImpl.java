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
import io.mosip.registration.processor.bio.dedupe.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.bio.dedupe.abis.dto.AbisInsertResponceDto;
import io.mosip.registration.processor.bio.dedupe.abis.dto.CandidatesDto;
import io.mosip.registration.processor.bio.dedupe.abis.dto.IdentityRequestDto;
import io.mosip.registration.processor.bio.dedupe.abis.dto.IdentityResponceDto;
import io.mosip.registration.processor.bio.dedupe.exception.ABISAbortException;
import io.mosip.registration.processor.bio.dedupe.exception.ABISInternalError;
import io.mosip.registration.processor.bio.dedupe.exception.UnableToServeRequestABISException;
import io.mosip.registration.processor.bio.dedupe.exception.UnexceptedError;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PacketStructure;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

/**
 * The Class BioDedupeServiceImpl.
 */
@RefreshScope
@Service
public class BioDedupeServiceImpl implements BioDedupeService {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeServiceImpl.class);

	/** The abis insert request dto. */
	private AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();

	/** The identify request dto. */
	private IdentityRequestDto identifyRequestDto = new IdentityRequestDto();

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The url. */
	@Value("${registration.processor.biometric.reference.url}")
	private String url;

	/** The max results. */
	@Value("${registration.processor.abis.maxResults}")
	private Integer maxResults;

	/** The target FPIR. */
	@Value("${registration.processor.abis.targetFPIR}")
	private Integer targetFPIR;

	/** The threshold. */
	@Value("${registration.processor.abis.threshold}")
	private Integer threshold;

	/** The filesystem ceph adapter impl. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> filesystemCephAdapterImpl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService#
	 * insertBiometrics(java.lang.String)
	 */
	@Override
	public String insertBiometrics(String registrationId) throws ApisResourceAccessException {

		String insertStatus = "failure";
		String requestId = uuidGenerator();
		String referenceId = uuidGenerator();
		abisInsertRequestDto.setId("insert");
		abisInsertRequestDto.setRequestId(requestId);
		abisInsertRequestDto.setReferenceId(referenceId);
		abisInsertRequestDto.setReferenceURL(url + registrationId);
		String timeStamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L);
		abisInsertRequestDto.setTimestamp(timeStamp);

		RegAbisRefDto regAbisRefDto = new RegAbisRefDto();
		regAbisRefDto.setAbis_ref_id(referenceId);
		regAbisRefDto.setReg_id(registrationId);

		packetInfoManager.saveAbisRef(regAbisRefDto);

		AbisInsertResponceDto authResponseDTO = (AbisInsertResponceDto) restClientService
				.postApi(ApiName.BIODEDUPEINSERT, "", "", abisInsertRequestDto, AbisInsertResponceDto.class);

		if (authResponseDTO.getReturnValue() == 1)
			insertStatus = "success";
		else
			throwException(authResponseDTO.getFailureReason(), referenceId, requestId);

		return insertStatus;

	}

	/**
	 * Throw exception.
	 *
	 * @param failureReason
	 *            the failure reason
	 * @param referenceId
	 *            the reference id
	 * @param requestId
	 *            the request id
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService#
	 * performDedupe(java.lang.String)
	 */
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

		if (responsedto != null) {

			if (responsedto.getReturnValue() == 2) {
				throwException(responsedto.getFailureReason(), referenceId, requestId);
			}

			if (responsedto.getCandidateList() != null) {
				getDuplicateCandidates(duplicates, abisResponseDuplicates, responsedto);
			}
		}

		return duplicates;
	}

	/**
	 * Gets the duplicate candidates.
	 *
	 * @param duplicates
	 *            the duplicates
	 * @param abisResponseDuplicates
	 *            the abis response duplicates
	 * @param responsedto
	 *            the responsedto
	 * @return the duplicate candidates
	 */
	private void getDuplicateCandidates(List<String> duplicates, List<String> abisResponseDuplicates,
			IdentityResponceDto responsedto) {
		CandidatesDto[] candidateList = responsedto.getCandidateList().getCandidates();

		for (CandidatesDto candidate : candidateList) {
			if (Integer.parseInt(candidate.getScaledScore()) >= threshold) {
				List<String> regIdList = packetInfoManager.getRidByReferenceId(candidate.getReferenceId());
				if (!regIdList.isEmpty()) {
					String regId = regIdList.get(0);
					abisResponseDuplicates.add(regId);
				}
			}
		}

		for (String duplicateReg : abisResponseDuplicates) {
			List<DemographicInfoDto> demoList = packetInfoManager.findDemoById(duplicateReg);
			if (!demoList.isEmpty()) {
				String uin = demoList.get(0).getUin();
				if (!uin.isEmpty()) {
					duplicates.add(duplicateReg);
				}
			}
		}
	}

	/**
	 * Uuid generator.
	 *
	 * @return the string
	 */
	private String uuidGenerator() {
		return UUID.randomUUID().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService#getFile(
	 * java.lang.String)
	 */
	@Override
	public byte[] getFile(String registrationId) {
		byte[] file = null;

		InputStream fileInStream = filesystemCephAdapterImpl.getFile(registrationId,
				PacketStructure.BIOMETRIC + PacketFiles.APPLICANT_BIO_CBEFF.name());
		try {
			file = IOUtils.toByteArray(fileInStream);
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage() + e.getMessage());
		}
		return file;
	}

}
