package org.mosip.kernel.uingenerator.service.impl;

import javax.transaction.Transactional;

import org.mosip.kernel.uingenerator.constants.UinGeneratorErrorCodes;
import org.mosip.kernel.uingenerator.dto.UinResponseDto;
import org.mosip.kernel.uingenerator.exception.UinNotFoundException;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.mosip.kernel.uingenerator.repository.UinDao;
import org.mosip.kernel.uingenerator.service.UinGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UinGeneratorServiceImpl implements UinGeneratorService {

	@Autowired
	UinDao uinDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.kernel.core.uingenerator.service.UinGeneratorService#getId()
	 */
	@Override
	@Transactional
	public UinResponseDto getUin() {
		UinResponseDto uinResponseDto = new UinResponseDto();
		UinBean uinBean = uinDao.findUnusedUin();
		if (uinBean != null) {
			uinBean.setUsed(true);
			uinDao.save(uinBean);
			uinResponseDto.setUin(uinBean.getUin());
		} else {
			throw new UinNotFoundException(UinGeneratorErrorCodes.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCodes.UIN_NOT_FOUND.getErrorMessage());
		}
		return uinResponseDto;
	}
}
