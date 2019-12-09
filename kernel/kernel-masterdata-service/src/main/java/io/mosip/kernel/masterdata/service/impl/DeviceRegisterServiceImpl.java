package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import org.bouncycastle.asn1.cms.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.masterdata.constant.DeviceRegisterErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.DeRegisterDeviceRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceDataDto;
import io.mosip.kernel.masterdata.dto.DeviceInfoDto;
import io.mosip.kernel.masterdata.dto.DeviceRegResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceRegister;
import io.mosip.kernel.masterdata.entity.DeviceRegisterHistory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.DeviceRegisterException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
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

	/** The registered. */
	private static String REGISTERED = "Registered";

	/** The revoked. */
	private static String REVOKED = "Revoked";

	/** The retired. */
	private static String RETIRED = "Retired";

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
		deviceRegisterEntity.setPurpose("mosip-process");
		deviceRegisterEntity.setCreatedDateTime(createdTime);
		deviceRegisterEntity.setCreatedBy(MetaDataUtils.getContextUser());
		deviceRegisterHistory.setDpSignature(request.getDpSignature());
		deviceRegisterHistory.setFoundationTrustCertificate(
				CryptoUtil.decodeBase64(request.getDeviceData().getFoundationTrustCertificate()));
		deviceRegisterHistory.setPurpose("mosip-process");
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
	public DeviceRegisterResponseDto deRegisterDevice(String deviceCode) {
		DeviceRegister deviceRegisterEntity = null;
		DeviceRegisterHistory deviceRegisterHistory = new DeviceRegisterHistory();
		try {
			deviceRegisterEntity = deviceRegisterRepository.findDeviceRegisterByCodeAndStatusCode(deviceCode);
			if (deviceRegisterEntity != null) {
				deviceRegisterEntity.setStatusCode("Retired");
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
		regResponseDto.setDeviceCode(deviceCode);
		regResponseDto.setStatus("success");
		responseDto.setResponse(regResponseDto);
		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceRegisterService#updateStatus(java.
	 * lang.String, java.lang.String)
	 */
	@Transactional
	@Override
	public ResponseDto updateStatus(String deviceCode, String statusCode) {
		DeviceRegister deviceRegister = null;
		try {
			deviceRegister = deviceRegisterRepository.findById(DeviceRegister.class, deviceCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceRegisterErrorCode.DEVICE_REGISTER_FETCH_EXCEPTION.getErrorCode(),
					DeviceRegisterErrorCode.DEVICE_REGISTER_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

		if (deviceRegister == null) {
			throw new DataNotFoundException(DeviceRegisterErrorCode.DATA_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceRegisterErrorCode.DATA_NOT_FOUND_EXCEPTION.getErrorMessage());
		}

		if (statusCode.equals(deviceRegister.getStatusCode())) {
			throw new RequestException(DeviceRegisterErrorCode.STATUS_CODE_ALREADY_EXISTS.getErrorCode(),
					DeviceRegisterErrorCode.STATUS_CODE_ALREADY_EXISTS.getErrorMessage());
		}
		if (!Arrays.asList(REGISTERED, REVOKED, RETIRED).contains(statusCode)) {
			throw new RequestException(DeviceRegisterErrorCode.INVALID_STATUS_CODE.getErrorCode(),
					DeviceRegisterErrorCode.INVALID_STATUS_CODE.getErrorMessage());
		}
		deviceRegister.setStatusCode(statusCode);
		updateRegisterDetails(deviceRegister);
		
		createHistoryDetails(deviceRegister);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setMessage(MasterDataConstant.DEVICE_REGISTER_UPDATE_MESSAGE);
		responseDto.setStatus(MasterDataConstant.SUCCESS);
		return responseDto;
	}

	/**
	 * Creates the history details.
	 *
	 * @param deviceRegister
	 *            the device register
	 */
	private void createHistoryDetails(DeviceRegister deviceRegister) {
		DeviceRegisterHistory deviceRegisterHistory = new DeviceRegisterHistory();
		MapperUtils.map(deviceRegister, deviceRegisterHistory);
		deviceRegisterHistory.setCreatedDateTime(deviceRegister.getCreatedDateTime());
		deviceRegisterHistory.setCreatedBy(MetaDataUtils.getContextUser());
		deviceRegisterHistory.setEffectivetimes(LocalDateTime.now(ZoneId.of("UTC")));
		deviceRegisterHistory.setUpdatedBy(MetaDataUtils.getContextUser());
		deviceRegisterHistory.setUpdatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		try {
			deviceRegisterHistoryRepository.create(deviceRegisterHistory);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					DeviceRegisterErrorCode.DEVICE_REGISTER_UPDATE_EXCEPTION.getErrorCode(),
					DeviceRegisterErrorCode.DEVICE_REGISTER_UPDATE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

	}

	/**
	 * Update register details.
	 *
	 * @param deviceRegister
	 *            the device register
	 */
	private void updateRegisterDetails(DeviceRegister deviceRegister) {
		try {
			deviceRegisterRepository.update(deviceRegister);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					DeviceRegisterErrorCode.DEVICE_REGISTER_UPDATE_EXCEPTION.getErrorCode(),
					DeviceRegisterErrorCode.DEVICE_REGISTER_UPDATE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

	}

}
