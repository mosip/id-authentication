package io.mosip.idrepository.vid.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.vid.dto.ResponseDto;
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
public class VidServiceImpl implements VidService<Object, VidResponseDTO> {

	private static final String MOSIP_VID_READ = "mosip.vid.read";
	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private VidRepo vidRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.idrepository.core.spi.VidService#retrieveUinByVid(java.lang.String)
	 */
	@Override
	public VidResponseDTO retrieveUinByVid(String vid) throws IdRepoAppException {

		Vid id = vidRepo.retrieveVid(vid);
		if (id != null) {
			checkExpiry(id.getExpiryDTimes());
			checkStatus(id.getStatusCode());
			String uin = vidRepo.retrieveUinByVid(vid);
			ResponseDto response = new ResponseDto();
			response.setUIN(uin);
			return buildResponse(response, MOSIP_VID_READ);
		} else {
			// TODO Throw an proper error.
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorCode(),
					IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorMessage());
		}
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

}
