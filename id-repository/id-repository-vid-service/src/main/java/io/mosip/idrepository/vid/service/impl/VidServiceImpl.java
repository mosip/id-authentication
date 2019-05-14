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
import io.mosip.idrepository.vid.dto.RequestDTO;
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
 * The Class VidServiceImpl.
 *
 * @author Manoj SP
 * @author Prem Kumar
 */
@Component
@ConfigurationProperties("mosip.idrepo.vid")
@Transactional
public class VidServiceImpl implements VidService<VidRequestDTO, VidResponseDTO> {
	
	/** The mosip logger. */
	private Logger mosipLogger = IdRepoLogger.getLogger(VidServiceImpl.class);

	/** The Constant EXPIRED. */
	private static final String EXPIRED = "Expired";

	/** The Constant ID_REPO_VID_SERVICE. */
	private static final String ID_REPO_VID_SERVICE = "VidService";

	/** The env. */
	@Autowired
	private Environment env;

	/** The vid repo. */
	@Autowired
	private VidRepo vidRepo;

	/** The vid generator. */
	@Autowired
	private VidGenerator<String> vidGenerator;

	/** The rest builder. */
	@Autowired
	private RestRequestBuilder restBuilder;

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The policy provider. */
	@Autowired
	private VidPolicyProvider policyProvider;
	
	/** The security manager. */
	@Autowired
	private IdRepoSecurityManager securityManager;

	/** The id. */
	@Resource
	private Map<String, String> id;

	/* (non-Javadoc)
	 * @see io.mosip.idrepository.core.spi.VidService#createVid(java.lang.Object)
	 */
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
				ResponseDTO responseDTO = new ResponseDTO();
				responseDTO.setVid(vid.getVid());
				responseDTO.setVidStatus(vid.getStatusCode());
				return buildResponse(responseDTO, id.get("create"));
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

	/**
	 * Check uin status.
	 *
	 * @param uin the uin
	 * @throws IdRepoAppException the id repo app exception
	 */
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
			ResponseDTO resDTO=new ResponseDTO();
			resDTO.setUin(vidObject.getUin());
			return buildResponse(resDTO, id.get("read"));
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
		checkStatus(vidObject.getStatusCode());
		checkExpiry(vidObject.getExpiryDTimes());
		VidPolicy policy = policyProvider.getPolicy(vidObject.getVidTypeCode());
		ResponseDTO response = new ResponseDTO();
		response.setVidStatus(vidStatus);
		if (policy.getAutoRestoreAllowed() && policy.getRestoreOnAction().equals(vidStatus)) {
			// create
			RequestDTO reqDTO=new RequestDTO();
			reqDTO.setUin(vidObject.getUin());
			reqDTO.setVidType(vidObject.getVidTypeCode());
			reqDTO.setVidStatus(vidStatus);
			request.setRequest(reqDTO);
			VidResponseDTO createVidResponse = createVid(request);
			response.setUpdatedVid(createVidResponse.getResponse().getVid());
			response.setUpdatedVidStatus(createVidResponse.getResponse().getVidStatus());
		}
		
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setVidStatus(vidStatus);
		return buildResponse(responseDTO, id.get("update"));
	}

	/**
	 * This Method will accepts vid as parameter and will return Vid Object from DB.
	 *
	 * @param vid the vid
	 * @return The Vid Object
	 */
	private Vid retrieveVidEntity(String vid) {
		return vidRepo.findByVid(vid);
	}

	/**
	 * This method will check expiry date of the vid, if vid is expired then it will
	 * throw IdRepoAppException.
	 *
	 * @param expiryDTimes the expiry D times
	 * @throws IdRepoAppException the id repo app exception
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
	 * @param statusCode the status code
	 * @throws IdRepoAppException the id repo app exception
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
	 * This Method will build the Vid Response.
	 *
	 * @param response the response
	 * @param id the id
	 * @return The Vid Response
	 */
	private VidResponseDTO buildResponse(ResponseDTO response, String id) {
		VidResponseDTO responseDto = new VidResponseDTO();
		responseDto.setId(id);
		responseDto.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
		responseDto.setResponse(response);
		return responseDto;
	}

}
