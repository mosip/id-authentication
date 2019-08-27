package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.masterdata.dto.DeRegisterDeviceRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceDataDto;
import io.mosip.kernel.masterdata.dto.DeviceInfoDto;
import io.mosip.kernel.masterdata.dto.DeviceRegResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceRegister;
import io.mosip.kernel.masterdata.entity.DeviceRegisterHistory;
import io.mosip.kernel.masterdata.exception.DeviceRegisterException;
import io.mosip.kernel.masterdata.repository.DeviceRegisterHistoryRepository;
import io.mosip.kernel.masterdata.repository.DeviceRegisterRepository;
import io.mosip.kernel.masterdata.service.DeviceRegisterService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Service class to register and de register Device.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Service
@Transactional
public class DeviceRegisterServiceImpl implements DeviceRegisterService {

	/**
	 * Reference to {@link DeviceRegisterRepository}.
	 */
	@Autowired
	private DeviceRegisterRepository deviceRegisterRepository;
	/**
	 * Reference to {@link DeviceRegisterHistoryRepository}.
	 */
	@Autowired
	private DeviceRegisterHistoryRepository deviceRegisterHistoryRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceRegisterService#registerDevice(io.
	 * mosip.kernel.masterdata.dto.DeviceRegisterDto)
	 */
	@Override
	public DeviceRegisterResponseDto registerDevice(DeviceRegisterDto request) {
		DeviceRegister deviceRegisterEntity = new DeviceRegister();
		DeviceRegisterHistory deviceRegisterHistory = new DeviceRegisterHistory();
		DeviceDataDto deviceDataDTO = request.getDeviceData();
		DeviceInfoDto deviceInfoDto = deviceDataDTO.getDeviceInfo();
		MapperUtils.map(deviceDataDTO, deviceRegisterEntity);
		MapperUtils.map(deviceInfoDto, deviceRegisterEntity);
		MapperUtils.map(deviceDataDTO, deviceRegisterHistory);
		MapperUtils.map(deviceInfoDto, deviceRegisterHistory);
		deviceRegisterEntity.setDpSignature(request.getDpSignature());
		deviceRegisterEntity.setFoundationTrustCertificate(
				CryptoUtil.decodeBase64(request.getDeviceData().getFoundationTrustCertificate()));
		LocalDateTime createdTime = DateUtils.getUTCCurrentDateTime();
		deviceRegisterEntity.setMosipProcess("mosip-process");
		deviceRegisterEntity.setCreatedDateTime(createdTime);
		deviceRegisterEntity.setCreatedBy(MetaDataUtils.getContextUser());
		deviceRegisterHistory.setDpSignature(request.getDpSignature());
		deviceRegisterHistory.setFoundationTrustCertificate(
				CryptoUtil.decodeBase64(request.getDeviceData().getFoundationTrustCertificate()));
		deviceRegisterHistory.setMosipProcess("mosip-process");
		deviceRegisterHistory.setCreatedDateTime(createdTime);
		deviceRegisterHistory.setCreatedBy(MetaDataUtils.getContextUser());
		deviceRegisterHistory.setEffectivetimes(LocalDateTime.now(ZoneId.of("UTC")));
		try {
			deviceRegisterRepository.create(deviceRegisterEntity);
			deviceRegisterHistoryRepository.create(deviceRegisterHistory);
		} catch (DataAccessLayerException e) {
			throw new DeviceRegisterException("KER-MSD-xx",
					"Error occur while registering device details " + ExceptionUtils.parseException(e));
		}
		DeviceRegisterResponseDto responseDto = new DeviceRegisterResponseDto();
		DeviceRegResponseDto regResponseDto = new DeviceRegResponseDto();
		regResponseDto.setDeviceCode(request.getDeviceData().getDeviceCode());
		regResponseDto.setStatus("success");
		responseDto.setResponse(regResponseDto);
		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceRegisterService#deRegisterDevice(io.
	 * mosip.kernel.masterdata.dto.DeRegisterDeviceRequestDto)
	 */
	@Override
	public DeviceRegisterResponseDto deRegisterDevice(DeRegisterDeviceRequestDto request) {
		DeviceRegister deviceRegisterEntity = null;
		DeviceRegisterHistory deviceRegisterHistory = new DeviceRegisterHistory();
		String deviceCode = request.getDevice().getDeviceCode();
		try {
			deviceRegisterEntity = deviceRegisterRepository.findDeviceRegisterByCodeAndStatusCode(deviceCode);
			if (deviceRegisterEntity != null) {
				deviceRegisterEntity.setStatusCode("deregister");
				MapperUtils.map(deviceRegisterEntity, deviceRegisterHistory);
				deviceRegisterHistory.setEffectivetimes(LocalDateTime.now(ZoneId.of("UTC")));
				deviceRegisterRepository.update(deviceRegisterEntity);
				deviceRegisterHistoryRepository.create(deviceRegisterHistory);
			} else {
				throw new DeviceRegisterException("KER-MSD-xx", "No register device found");
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new DeviceRegisterException("KER-MSD-xx",
					"Error occur while deregistering device details " + ExceptionUtils.parseException(e));
		}

		DeviceRegisterResponseDto responseDto = new DeviceRegisterResponseDto();
		DeviceRegResponseDto regResponseDto = new DeviceRegResponseDto();
		regResponseDto.setDeviceCode(request.getDevice().getDeviceCode());
		regResponseDto.setStatus("success");
		responseDto.setResponse(regResponseDto);
		return null;
	}

}
