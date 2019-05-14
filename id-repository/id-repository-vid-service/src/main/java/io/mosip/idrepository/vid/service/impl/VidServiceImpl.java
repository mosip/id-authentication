package io.mosip.idrepository.vid.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

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
public class VidServiceImpl implements VidService<VidRequestDTO, VidResponseDTO> {

	private static final String EXPIRED = "Expired";

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

	/** The id. */
	@Resource
	private Map<String, String> id;
	
	@Autowired
	private VidPolicyProvider vidpolicyProvider;

	@Override
	public VidResponseDTO createVid(VidRequestDTO vidRequest) throws IdRepoAppException {
		try {
			checkUinStatus(vidRequest.getRequest().getUin());
			List<Vid> vidDetails = vidRepo.retrieveActiveVidByUin(vidRequest.getRequest().getUin(),
					env.getProperty(IdRepoConstants.MOSIP_IDREPO_VID_STATUS.getValue()),
					vidRequest.getRequest().getVidType());
			VidPolicy policy = policyProvider.getPolicy(vidRequest.getRequest().getVidType());
			if (Objects.isNull(vidDetails) || vidDetails.isEmpty()
					|| vidDetails.size() < policy.getAllowedInstances()) {
				ResponseDTO responseDTO= new ResponseDTO();
				LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime();
				String vidRefId = UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID,
						vidRequest.getRequest().getUin() + "_" + DateUtils.getUTCCurrentDateTime()
								.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
								.toInstant().toEpochMilli())
						.toString();
				Vid vid = vidRepo.save(new Vid(vidRefId, vidGenerator.generateId(),
						vidRequest.getRequest().getUin(), vidRequest.getRequest().getUin(),
						env.getProperty(IdRepoConstants.ACTIVE_STATUS.getValue()), currentTime,
						Objects.nonNull(policy.getValidForInMinutes())
								? DateUtils.getUTCCurrentDateTime().plusMinutes(policy.getValidForInMinutes())
								: LocalDateTime.MAX.withYear(9999),
						env.getProperty(IdRepoConstants.MOSIP_IDREPO_VID_STATUS.getValue()), "createdBy", currentTime,
						"updatedBy", currentTime, false, currentTime));
				responseDTO.setVidStatus(vid.getStatusCode());
				responseDTO.setVid(vid.getVid());
				return buildResponse(responseDTO , id.get("create"));
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.VID_CREATION_FAILED);
			}
		} catch (VidException e) {
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
			List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(
					e.getResponseBodyAsString().isPresent() ? e.getResponseBodyAsString().get() : null);
			if (errorList.get(0).getErrorCode().equals(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode())) {
				throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID);
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.UIN_RETRIEVAL_FAILED);
			}
		} catch (IdRepoDataValidationException e) {
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
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorCode(),
					IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorMessage());
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
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorCode(),
					IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorMessage());
		}
		checkStatus(vidObject.getStatusCode());
		checkExpiry(vidObject.getExpiryDTimes());

		VidPolicy policy = vidpolicyProvider.getPolicy(vidObject.getVidTypeCode());
		vidObject.setStatusCode(vidStatus);
		vidRepo.save(vidObject);
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
		
		return buildResponse(response, id.get("update"));

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
		if (!statusCode.equalsIgnoreCase(env.getProperty(IdRepoConstants.MOSIP_IDREPO_VID_STATUS.getValue()))) {
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
	private VidResponseDTO buildResponse(ResponseDTO response, String id) {
		VidResponseDTO responseDto = new VidResponseDTO();
		responseDto.setId(id);
		responseDto.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
		responseDto.setResponse(response);
		return responseDto;
	}

}
