package io.mosip.idrepository.vid.service.impl;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.vid.dto.ResponseDto;
import io.mosip.idrepository.vid.dto.VidResponseDTO;
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
	 * @see io.mosip.idrepository.core.spi.VidService#retrieveUinByVid(java.lang.String)
	 */
	@Override
	public VidResponseDTO retrieveUinByVid(String vid) throws IdRepoAppException {
		String uin=vidRepo.retrieveUinByVid(vid);
		VidResponseDTO responseDto=new VidResponseDTO();
		responseDto.setId(MOSIP_VID_READ);
		responseDto.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
		responseDto.setResponseTime(DateUtils.getUTCCurrentDateTime()
								.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		ResponseDto response=new ResponseDto();
		response.setUIN(uin);
		responseDto.setResponse(response);
		return responseDto;
	}

}
