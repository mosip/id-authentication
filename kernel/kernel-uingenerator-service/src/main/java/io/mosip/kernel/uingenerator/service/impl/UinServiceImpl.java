/**
 * 
 */
package io.mosip.kernel.uingenerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.dto.UinStatusUpdateReponseDto;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
import io.mosip.kernel.uingenerator.exception.UinNotIssuedException;
import io.mosip.kernel.uingenerator.exception.UinStatusNotFoundException;
import io.mosip.kernel.uingenerator.repository.UinRepository;
import io.mosip.kernel.uingenerator.service.UinService;
import io.mosip.kernel.uingenerator.util.MetaDataUtil;

/**
 * @author Dharmesh Khandelwal
 * @author Megha Tanga
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@Component
public class UinServiceImpl implements UinService {

	/**
	 * Field for {@link #uinRepository}
	 */
	@Autowired
	UinRepository uinRepository;

	/**
	 * instance of {@link MetaDataUtil}
	 */
	@Autowired
	private MetaDataUtil metaDataUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.uingenerator.service.UinGeneratorService#getId()
	 */
	@Override
	public UinResponseDto getUin() {
		UinResponseDto uinResponseDto = new UinResponseDto();
		UinEntity uinBean = uinRepository.findFirstByStatus(UinGeneratorConstant.UNUSED);
		if (uinBean != null) {
			uinBean.setStatus(UinGeneratorConstant.ISSUED);
			metaDataUtil.setUpdateMetaData(uinBean);
			uinRepository.save(uinBean);
			uinResponseDto.setUin(uinBean.getUin());
		} else {
			throw new UinNotFoundException(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
		}
		return uinResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.uingenerator.service.UinGeneratorService#updateUinStatus(io.
	 * vertx.core.json.JsonObject)
	 */
	@Override
	public UinStatusUpdateReponseDto updateUinStatus(UinEntity uinAck) {
		UinStatusUpdateReponseDto uinResponseDto = new UinStatusUpdateReponseDto();
		UinEntity existingUin = uinRepository.findByUin(uinAck.getUin());
		if (existingUin != null) {
			if (UinGeneratorConstant.ISSUED.equals(existingUin.getStatus())) {
				metaDataUtil.setUpdateMetaData(existingUin);
				if (UinGeneratorConstant.ASSIGNED.equals(uinAck.getStatus())) {
					existingUin.setStatus(UinGeneratorConstant.ASSIGNED);
					uinRepository.save(existingUin);
				} else if (UinGeneratorConstant.UNASSIGNED.equals(uinAck.getStatus())) {
					existingUin.setStatus(UinGeneratorConstant.UNUSED);
					uinRepository.save(existingUin);
				} else {
					throw new UinStatusNotFoundException(UinGeneratorErrorCode.UIN_STATUS_NOT_FOUND.getErrorCode(),
							UinGeneratorErrorCode.UIN_STATUS_NOT_FOUND.getErrorMessage());
				}
			} else {
				throw new UinNotIssuedException(UinGeneratorErrorCode.UIN_NOT_ISSUED.getErrorCode(),
						UinGeneratorErrorCode.UIN_NOT_ISSUED.getErrorMessage());
			}
		} else {
			throw new UinNotFoundException(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
		}
		uinResponseDto.setUin(existingUin.getUin());
		uinResponseDto.setStatus(existingUin.getStatus());
		return uinResponseDto;
	}

}
