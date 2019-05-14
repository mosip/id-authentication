package io.mosip.idrepository.vid.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.constant.RestServicesConstants;
import io.mosip.idrepository.core.dto.IdResponseDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.vid.dto.ResponseDTO;
import io.mosip.idrepository.vid.dto.VidPolicy;
import io.mosip.idrepository.vid.dto.VidRequestDTO;
import io.mosip.idrepository.vid.dto.VidResponseDTO;
import io.mosip.idrepository.vid.entity.Vid;
import io.mosip.idrepository.vid.provider.VidPolicyProvider;
import io.mosip.idrepository.vid.repository.VidRepo;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.UUIDUtils;
import io.mosip.kernel.idgenerator.vid.exception.VidException;

/**
 * 
 * @author Manoj SP
 * @author Prem Kumar
 *
 */
@Component
@ConfigurationProperties("mosip.idrepo.vid")
@Transactional
public class VidServiceImpl implements VidService<VidRequestDTO, VidResponseDTO> {
	
	private Logger mosipLogger = IdRepoLogger.getLogger(VidServiceImpl.class);

	private static final String EXPIRED = "Expired";

	private static final String ID_REPO_VID_SERVICE = "VidService";

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private VidRepo vidRepo;

	@Autowired
	private VidGenerator<String> vidGenerator;

	@Autowired
	private RestRequestBuilder restBuilder;

	@Autowired
	private RestHelper restHelper;

	@Autowired
	private VidPolicyProvider policyProvider;
	
	@Autowired
	private IdRepoSecurityManager securityManager;

	/** The id. */
	@Resource
	private Map<String, String> id;

	@Override
	public VidResponseDTO createVid(VidRequestDTO vidRequest) throws IdRepoAppException {
		try {
			String uinHash = securityManager.hash(vidRequest.getRequest().getUin().getBytes());
			checkUinStatus(vidRequest.getRequest().getUin());
			List<Vid> vidDetails = vidRepo.findByUinHashAndStatusCodeAndVidTypeCode(uinHash,
					env.getProperty(IdRepoConstants.VID_ACTIVE_STATUS.getValue()),
					vidRequest.getRequest().getVidType());
			VidPolicy policy = policyProvider.getPolicy(vidRequest.getRequest().getVidType());
			if (Objects.isNull(vidDetails) || vidDetails.isEmpty()
					|| vidDetails.size() < policy.getAllowedInstances()) {
				LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime();
				String vidRefId = UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID,
						vidRequest.getRequest().getUin() + "_" + DateUtils.getUTCCurrentDateTime()
								.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
								.toInstant().toEpochMilli())
						.toString();
				Vid vid = vidRepo.save(new Vid(vidRefId, vidGenerator.generateId(),
						uinHash, vidRequest.getRequest().getUin(),
						vidRequest.getRequest().getVidType(), currentTime,
						Objects.nonNull(policy.getValidForInMinutes())
								? DateUtils.getUTCCurrentDateTime().plusMinutes(policy.getValidForInMinutes())
								: LocalDateTime.MAX.withYear(9999),
						env.getProperty(IdRepoConstants.VID_ACTIVE_STATUS.getValue()), "createdBy", currentTime,
						"updatedBy", currentTime, false, currentTime));
				return buildResponse(null, vid.getVid(), vid.getStatusCode(), id.get("create"));
			} else {
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "createVid",
						"throwing vid creation failed");
				throw new IdRepoAppException(IdRepoErrorConstants.VID_CREATION_FAILED);
			}
		} catch (VidException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "createVid", e.getErrorText());
			throw new IdRepoAppException(IdRepoErrorConstants.VID_GENERATION_FAILED, e);
		}
	}

	private void checkUinStatus(String uin) throws IdRepoAppException {
		try {
			RestRequestDTO request = restBuilder.buildRequest(RestServicesConstants.IDREPO_IDENTITY_SERVICE, null,
					IdResponseDTO.class);
			request.setPathVariables(Collections.singletonMap("uin", uin));
			IdResponseDTO identityResponse = restHelper.requestSync(request);
			String uinStatus = identityResponse.getResponse().getStatus();
			if (!uinStatus.equals(env.getProperty(IdRepoConstants.ACTIVE_STATUS.getValue()))) {
				throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN.getErrorCode(),
						String.format(IdRepoErrorConstants.INVALID_UIN.getErrorMessage(), uinStatus));
			}
		} catch (RestServiceException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus",
					"\n" + ExceptionUtils.getStackTrace(e));
			List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(
					e.getResponseBodyAsString().isPresent() ? e.getResponseBodyAsString().get() : null);
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus",
					"\n" + errorList);
			if (Objects.nonNull(errorList) && !errorList.isEmpty()
					&& errorList.get(0).getErrorCode().equals(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode())) {
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus",
						"throwing no record found");
				throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID);
			} else {
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus",
						"throwing UIN_RETRIEVAL_FAILED");
				throw new IdRepoAppException(IdRepoErrorConstants.UIN_RETRIEVAL_FAILED);
			}
		} catch (IdRepoDataValidationException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
					e.getErrorText());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.idrepository.core.spi.VidService#retrieveUinByVid(java.lang.String)
	 */
	@Override
	public VidResponseDTO retrieveUinByVid(String vid) throws IdRepoAppException {

		Vid vidObject = retrieveVidEntity(vid);
		if (vidObject != null) {
			checkExpiry(vidObject.getExpiryDTimes());
			checkStatus(vidObject.getStatusCode());
			return buildResponse(vidObject.getUin(), null, null, id.get("read"));
		} else {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "retrieveUinByVid",
					"throwing NO_RECORD_FOUND_VID");
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.idrepository.core.spi.VidService#updateVid(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public VidResponseDTO updateVid(String vid, VidRequestDTO request) throws IdRepoAppException {
		Vid vidObject = retrieveVidEntity(vid);
		String vidStatus = request.getRequest().getVidStatus();
		if (Objects.isNull(vidObject)) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "updateVid",
					"throwing NO_RECORD_FOUND_VID");
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID);
		}
		if (vidObject.getStatusCode().equals(env.getProperty(IdRepoConstants.VID_ACTIVE_STATUS.getValue()))) {
			vidObject.setStatusCode(vidStatus);
			vidRepo.save(vidObject);
			return buildResponse(null, null, vidStatus, id.get("update"));
		} else {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "updateVid",
					"throwing INVALID_VID");
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), vidObject.getStatusCode()));
		}
	}

	/**
	 * This Method will accepts vid as parameter and will return Vid Object from DB.
	 * 
	 * @param Vid
	 * @return The Vid Object
	 */
	private Vid retrieveVidEntity(String vid) {
		return vidRepo.findByVid(vid);
	}

	/**
	 * This method will check expiry date of the vid, if vid is expired then it will
	 * throw IdRepoAppException.
	 * 
	 * @param expiryDTimes
	 * @throws IdRepoAppException
	 */
	private void checkExpiry(LocalDateTime expiryDTimes) throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime();
		if (!DateUtils.after(expiryDTimes, currentTime)) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkExpiry",
					"throwing Expired VID");
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), EXPIRED));
		}
	}

	/**
	 * This method will check Status of the vid.
	 * 
	 * @param statusCode
	 * @throws IdRepoAppException
	 */
	private void checkStatus(String statusCode) throws IdRepoAppException {
		if (!statusCode.equalsIgnoreCase(env.getProperty(IdRepoConstants.VID_ACTIVE_STATUS.getValue()))) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkStatus",
					"throwing INVALID_VID with status - " + statusCode);
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), statusCode));
		}
	}

	/**
	 * This Method will build the Vid Response
	 * 
	 * @param response
	 * @param id
	 * @return The Vid Response
	 */
	private VidResponseDTO buildResponse(String uin, String vid, String vidStatus, String id) {
		VidResponseDTO responseDto = new VidResponseDTO();
		responseDto.setId(id);
		responseDto.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
		ResponseDTO response = new ResponseDTO();
		response.setUin(uin);
		response.setVid(vid);
		response.setVidStatus(vidStatus);
		responseDto.setResponse(response);
		return responseDto;
	}

}
