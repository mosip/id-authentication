/**
 * 
 */
package io.mosip.kernel.uingenerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.uingenerator.service.UinGeneratorService#getId()
	 */
	@Override
	public UinResponseDto getUin() {
		UinResponseDto uinResponseDto = new UinResponseDto();
		//UinEntity uinBean = uinRepository.findFirstByStatus(UinGeneratorConstant.UNUSED);
		UinEntity uinBean = uinRepository.findFirstByStatus(unused);
		if (uinBean != null) {
			//uinBean.setStatus(UinGeneratorConstant.ISSUED);
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

	@Override
	public UinResponseDto updateUinStatus(JsonObject uin) {
		System.out.println("======updateUinStatus==== in service");
		UinResponseDto uinResponseDto = new UinResponseDto();
		final UinEntity uinEntity = Json.decodeValue(uin.toString(), UinEntity.class);
		System.out.println("====uinEntity====" + uinEntity);
		if (uinEntity != null) {
			metaDataUtil.setMetaData(uinEntity);
			uinEntity.setStatus(UinGeneratorConstant.ASSIGNED);
			metaDataUtil.setMetaDataUpdate(uinEntity);
			uinRepository.save(uinEntity);
			uinResponseDto.setUin(uinEntity.getUin());
		} else {
			throw new UinNotFoundException(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
		}
		return uinResponseDto;
	}

}
