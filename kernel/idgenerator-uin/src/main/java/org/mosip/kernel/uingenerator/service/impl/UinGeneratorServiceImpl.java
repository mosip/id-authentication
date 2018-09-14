package org.mosip.kernel.uingenerator.service.impl;

import javax.transaction.Transactional;

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
	public UinResponseDto getId() {
		UinResponseDto dto = new UinResponseDto();

		UinBean uinBean = uinDao.findUnusedUin(false);

		if (uinBean != null) {
			uinBean.setUsed(true);
			uinDao.save(uinBean);
			dto.setUin(uinBean.getUin());
		} else {
			throw new UinNotFoundException("code","Id not found");
		}
		return dto;
	}
}
