package io.mosip.registration.processor.bio.dedupe.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.bio.dedupe.exception.ABISAbortException;
import io.mosip.registration.processor.bio.dedupe.exception.ABISInternalError;
import io.mosip.registration.processor.bio.dedupe.exception.UnableToServeRequestABISException;
import io.mosip.registration.processor.bio.dedupe.exception.UnexceptedError;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PacketStructure;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.CandidatesDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class BioDedupeServiceImpl.
 *
 * @author Alok
 * @author Nagalakshmi
 *
 */
@Service
public class BioDedupeServiceImpl implements BioDedupeService {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeServiceImpl.class);

	/** The abis insert request dto. */
	private AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();

	/** The identify request dto. */
	private AbisIdentifyRequestDto identifyRequestDto = new AbisIdentifyRequestDto();

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private RegistrationStatusService registrationStatusService;

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

	/** The filesystem adapter impl. */
	@Autowired
	private PacketManager filesystemCephAdapterImpl;

	/** The identity iterator util. */
	IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();

	private static final String ABIS_INSERT = "mosip.abis.insert";

	private static final String ABIS_IDENTIFY = "mosip.abis.identify";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService#
	 * insertBiometrics(java.lang.String)
	 */
	@Override
	public String insertBiometrics(String registrationId) throws ApisResourceAccessException {

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				registrationId, "BioDedupeServiceImpl::insertBiometrics()::entry");
		String insertStatus = "failure";
		String requestId = uuidGenerator();
		String referenceId = uuidGenerator();
		abisInsertRequestDto.setId(ABIS_INSERT);
		abisInsertRequestDto.setRequestId(requestId);
		abisInsertRequestDto.setReferenceId(referenceId);
		abisInsertRequestDto.setReferenceURL(url + registrationId);
		String timeStamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L);
		abisInsertRequestDto.setTimestamp(timeStamp);

		RegAbisRefDto regAbisRefDto = new RegAbisRefDto();
		regAbisRefDto.setAbis_ref_id(referenceId);
		regAbisRefDto.setReg_id(registrationId);

		packetInfoManager.saveAbisRef(regAbisRefDto);

		AbisInsertResponseDto authResponseDTO = (AbisInsertResponseDto) restClientService
				.postApi(ApiName.BIODEDUPEINSERT, "", "", abisInsertRequestDto, AbisInsertResponseDto.class);

		if (authResponseDTO.getReturnValue() == 1)
			insertStatus = "success";
		else
			throwException(authResponseDTO.getFailureReason(), referenceId, requestId);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				registrationId, "BioDedupeServiceImpl::insertBiometrics()::exit");

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
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				registrationId, "BioDedupeServiceImpl::performDedupe()::entry");
		List<String> duplicates = new ArrayList<>();
		List<String> abisResponseDuplicates = new ArrayList<>();

		String requestId = uuidGenerator();

		String referenceId = packetInfoManager.getReferenceIdByRid(registrationId).get(0);

		identifyRequestDto.setId(ABIS_IDENTIFY);
		identifyRequestDto.setVer("1.0");
		identifyRequestDto.setRequestId(requestId);
		identifyRequestDto.setReferenceId(referenceId);

		String timeStamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L);
		identifyRequestDto.setTimestamp(timeStamp);
		identifyRequestDto.setMaxResults(maxResults);
		identifyRequestDto.setTargetFPIR(targetFPIR);

		// call Identify Api to get duplicate ids
		AbisIdentifyResponseDto responsedto = (AbisIdentifyResponseDto) restClientService
				.postApi(ApiName.BIODEDUPEPOTENTIAL, "", "", identifyRequestDto, AbisIdentifyResponseDto.class);

		if (responsedto != null) {

			if (responsedto.getReturnValue() == 2) {
				throwException(responsedto.getFailureReason(), referenceId, requestId);
			}

			if (responsedto.getCandidateList() != null) {
				getDuplicateCandidates(duplicates, abisResponseDuplicates, responsedto);
			}
		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				registrationId, "BioDedupeServiceImpl::performDedupe()::exit");
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
			AbisIdentifyResponseDto responsedto) {
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
				if(registrationStatusService.checkUinAvailabilityForRid(demoList.get(0).getRegId())) {
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
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				registrationId, "BioDedupeServiceImpl::getFile()::entry");
		byte[] file = null;
		try {
		InputStream packetMetaInfoStream = filesystemCephAdapterImpl.getFile(registrationId,
				PacketFiles.PACKET_META_INFO.name());
		PacketMetaInfo packetMetaInfo = null;
		String applicantBiometricFileName = "";

			packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketMetaInfo.class);

			List<FieldValueArray> hashSequence = packetMetaInfo.getIdentity().getHashSequence1();
			List<String> hashList = identityIteratorUtil.getHashSequence(hashSequence,
					JsonConstant.APPLICANTBIOMETRICSEQUENCE);
			if (hashList != null)
				applicantBiometricFileName = hashList.get(0);
			InputStream fileInStream = filesystemCephAdapterImpl.getFile(registrationId,
					PacketStructure.BIOMETRIC + applicantBiometricFileName.toUpperCase());

			file = IOUtils.toByteArray(fileInStream);

		} catch (UnsupportedEncodingException exp) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.UNSUPPORTED_ENCODING.getMessage() + exp.getMessage());
		} catch (IOException | io.mosip.kernel.core.exception.IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(e));
		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.UNSUPPORTED_ENCODING.getMessage() +ExceptionUtils.getStackTrace(e));
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				registrationId, "BioDedupeServiceImpl::getFile()::exit");
		return file;
	}

}
