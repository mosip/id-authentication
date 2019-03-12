/**
 * 
 */
package io.mosip.kernel.uingenerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.dto.UinStatusUpdateReponseDto;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
import io.mosip.kernel.uingenerator.exception.UinNotIssuedException;
import io.mosip.kernel.uingenerator.exception.UinStatusNotFoundException;
import io.mosip.kernel.uingenerator.repository.UinRepository;
import io.mosip.kernel.uingenerator.service.UinGeneratorService;
import io.mosip.kernel.uingenerator.util.MetaDataUtil;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Dharmesh Khandelwal
 * @author Megha Tanga
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

	/**
	 * instance of {@link MetaDataUtil}
	 */
	@Autowired
	private MetaDataUtil metaDataUtil;

	@Value("${mosip.kernel.uin.status.unused}")
	private String unused;

	@Value("${mosip.kernel.uin.status.issued}")
	private String issued;

	@Value("${mosip.kernel.uin.status.assigned}")
	private String assigned;

	@Value("${mosip.kernel.uin.status.unassigned}")
	private String unassigned;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.uingenerator.service.UinGeneratorService#getId()
	 */
	@Override
	public UinResponseDto getUin() {
		UinResponseDto uinResponseDto = new UinResponseDto();
		UinEntity uinBean = uinRepository.findFirstByStatus(unused);
		if (uinBean != null) {
			uinBean.setStatus(issued);
			metaDataUtil.setMetaDataUpdate(uinBean);
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
	public UinStatusUpdateReponseDto updateUinStatus(JsonObject uin) {
		UinStatusUpdateReponseDto uinResponseDto = new UinStatusUpdateReponseDto();
		final UinEntity uinEntity = Json.decodeValue(uin.toString(), UinEntity.class);
		UinEntity uinBean = uinRepository.findByUin(uinEntity.getUin());
		if (uinBean != null) {
			if (uinBean.getStatus().equals(issued)) {
				metaDataUtil.setMetaDataUpdate(uinBean);
				if (assigned.equals(uinEntity.getStatus())) {
					uinBean.setStatus(assigned);
					uinRepository.save(uinBean);
				} else if (unassigned.equals(uinEntity.getStatus())) {
					uinBean.setStatus(unused);
					uinRepository.save(uinBean);
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

		uinResponseDto.setUin(uinBean.getUin());
		uinResponseDto.setStatus(uinBean.getStatus());
		return uinResponseDto;
	}

}
