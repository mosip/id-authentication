package io.mosip.idrepository.vid.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.vid.dto.ResponseDto;
import io.mosip.idrepository.vid.dto.VidRequestDTO;
import io.mosip.idrepository.vid.dto.VidResponseDTO;
import io.mosip.idrepository.vid.entity.Vid;
import io.mosip.idrepository.vid.repository.VidRepo;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * @author Prem Kumar
 *
 */
@Component
@ConfigurationProperties("mosip.idrepo.vid")
public class VidServiceImpl implements VidService<VidRequestDTO, VidResponseDTO> {

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private VidRepo vidRepo;

	/** The id. */
	@Resource
	private Map<String, String> id;

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
			String uin = vidRepo.retrieveUinByVid(vid);
			ResponseDto response = new ResponseDto();
			response.setUin(uin);
			return buildResponse(response, id.get("read"));
		} else {
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorCode(),
					IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorMessage());
		}
	}

	private Vid retrieveVidEntity(String vid) {
		return vidRepo.retrieveVid(vid);
	}

	private void checkExpiry(LocalDateTime expiryDTimes) throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime();
		if (!DateUtils.after(expiryDTimes, currentTime)) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), "Expired"));
		}
	}

	private void checkStatus(String statusCode) throws IdRepoAppException {
		if (!statusCode.equalsIgnoreCase(env.getProperty(IdRepoConstants.MOSIP_IDREPO_VID_STATUS.getValue()))) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), statusCode));
		}
	}

	private VidResponseDTO buildResponse(ResponseDto response, String id) {
		VidResponseDTO responseDto = new VidResponseDTO();
		responseDto.setId(id);
		responseDto.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
		responseDto.setResponseTime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		responseDto.setResponse(response);
		return responseDto;
	}

	@Override
	public VidResponseDTO updateVid(String vid, VidRequestDTO request) throws IdRepoAppException {
		Vid vidObject = retrieveVidEntity(vid);
		String vidStatus = Optional.ofNullable(request.getRequest()).get().getVidStatus();
		ResponseDto response = new ResponseDto();
		if (!Objects.nonNull(vidObject)) {
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorCode(),
					IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorMessage());
		}
		if (vidObject.getStatusCode().equals(env.getProperty(IdRepoConstants.MOSIP_IDREPO_VID_STATUS.getValue()))) {

			response.setVidStatus(vidStatus);
			vidObject.setStatusCode(vidStatus);
			vidRepo.save(vidObject);
			return buildResponse(response, id.get("update"));
		} else {
			// TODO throw proper error
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorCode(),
					IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorMessage());
		}
	}

}
