package io.mosip.kernel.idgenerator.uin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.idgenerator.uin.constant.UinGeneratorErrorCode;
import io.mosip.kernel.idgenerator.uin.dto.UinResponseDto;
import io.mosip.kernel.idgenerator.uin.entity.UinEntity;
import io.mosip.kernel.idgenerator.uin.exception.UinNotFoundException;
import io.mosip.kernel.idgenerator.uin.repository.UinRepository;
import io.mosip.kernel.idgenerator.uin.service.UinGeneratorService;

/**
 * This class have function to fetch a unused uin
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
@Transactional
public class UinGeneratorServiceImpl implements UinGeneratorService {

	/**
	 * Field for {@link #uinDao}
	 */
	@Autowired
	UinRepository uinDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.uingenerator.service.UinGeneratorService#getId()
	 */
	@Override
	public UinResponseDto getUin() {
		UinResponseDto uinResponseDto = new UinResponseDto();
		UinEntity uinBean = uinDao.findUnusedUin();
		if (uinBean != null) {
			uinBean.setUsed(true);
			uinDao.save(uinBean);
			uinResponseDto.setUin(uinBean.getUin());
		} else {
			throw new UinNotFoundException(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
		}
		return uinResponseDto;
	}
}
