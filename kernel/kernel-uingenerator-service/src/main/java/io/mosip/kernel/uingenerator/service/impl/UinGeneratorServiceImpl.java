/**
 * 
 */
package io.mosip.kernel.uingenerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
import io.mosip.kernel.uingenerator.repository.UinRepository;
import io.mosip.kernel.uingenerator.service.UinGeneratorService;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinGeneratorServiceImpl implements UinGeneratorService {

	/**
	 * Field for {@link #uinRepository}
	 */
	@Autowired
	UinRepository uinRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.uingenerator.service.UinGeneratorService#getId()
	 */
	@Override
	public UinResponseDto getUin() {
		UinResponseDto uinResponseDto = new UinResponseDto();
		UinEntity uinBean = uinRepository.findFirstByUsedIsFalse();
		if (uinBean != null) {
			uinBean.setUsed(true);
			uinRepository.save(uinBean);
			uinResponseDto.setUin(uinBean.getUin());
		} else {
			throw new UinNotFoundException(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
		}
		return uinResponseDto;
	}

}
