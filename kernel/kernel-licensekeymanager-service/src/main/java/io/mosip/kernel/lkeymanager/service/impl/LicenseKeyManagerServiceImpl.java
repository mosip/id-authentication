package io.mosip.kernel.lkeymanager.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService;
import io.mosip.kernel.lkeymanager.constant.LicenseKeyManagerExceptionConstants;
import io.mosip.kernel.lkeymanager.constant.LicenseKeyManagerPropertyConstants;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyList;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyPermission;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyTspMap;
import io.mosip.kernel.lkeymanager.exception.LicenseKeyServiceException;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyListRepository;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyPermissionRepository;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyTspMapRepository;
import io.mosip.kernel.lkeymanager.util.LicenseKeyManagerUtil;

/**
 * Implementation class for {@link LicenseKeyManagerService}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Service
public class LicenseKeyManagerServiceImpl
		implements LicenseKeyManagerService<String, LicenseKeyGenerationDto, LicenseKeyMappingDto> {
	/**
	 * Autowired reference for {@link LicenseKeyManagerUtil}.
	 */
	@Autowired
	LicenseKeyManagerUtil licenseKeyManagerUtil;
	/**
	 * Autowired reference for {@link LicenseKeyListRepository}
	 */
	@Autowired
	LicenseKeyListRepository licenseKeyListRepository;

	/**
	 * Autowired reference for {@link LicenseKeyPermissionRepository}.
	 */
	@Autowired
	LicenseKeyPermissionRepository licenseKeyPermissionsRepository;

	/**
	 * Autowired reference for {@link LicenseKeyTspMapRepository}.
	 */
	@Autowired
	LicenseKeyTspMapRepository licenseKeyTspMapRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService#
	 * generateLicenseKey(java.lang.Object)
	 */
	@Override
	public String generateLicenseKey(LicenseKeyGenerationDto licenseKeyGenerationDto) {

		licenseKeyManagerUtil.validateTSP(licenseKeyGenerationDto.getTspId());

		String generatedLicense = licenseKeyManagerUtil.generateLicense();

		LicenseKeyList licenseKeyListEntity = new LicenseKeyList();
		LicenseKeyTspMap licenseKeyTspMapEntity = new LicenseKeyTspMap();

		licenseKeyListEntity.setLicenseKey(generatedLicense);
		licenseKeyListEntity.setActive(true);
		licenseKeyListEntity.setCreatedAt(licenseKeyManagerUtil.getCurrentTimeInUTCTimeZone());
		licenseKeyListEntity.setExpiryDateTimes(licenseKeyGenerationDto.getLicenseExpiryTime());
		licenseKeyListEntity.setCreatedBy(LicenseKeyManagerPropertyConstants.DEFAULT_CREATED_BY.getValue());
		licenseKeyListEntity.setCreatedDateTimes(licenseKeyManagerUtil.getCurrentTimeInUTCTimeZone());

		licenseKeyTspMapEntity.setTspId(licenseKeyGenerationDto.getTspId());
		licenseKeyTspMapEntity.setLKey(generatedLicense);
		licenseKeyTspMapEntity.setActive(true);
		licenseKeyTspMapEntity.setCreatedDateTimes(licenseKeyManagerUtil.getCurrentTimeInUTCTimeZone());
		licenseKeyTspMapEntity.setCreatedBy(LicenseKeyManagerPropertyConstants.DEFAULT_CREATED_BY.getValue());

		licenseKeyListRepository.save(licenseKeyListEntity);
		licenseKeyTspMapRepository.save(licenseKeyTspMapEntity);

		return generatedLicense;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService#
	 * mapLicenseKey(java.lang.Object)
	 */
	@Transactional
	@Override
	public String mapLicenseKey(LicenseKeyMappingDto licenseKeyMappingDto) {
		licenseKeyManagerUtil.validateRequestParameters(licenseKeyMappingDto.getTspId(),
				licenseKeyMappingDto.getLicenseKey(), licenseKeyMappingDto.getPermissions());
		if (licenseKeyTspMapRepository.findByLKeyAndTspId(licenseKeyMappingDto.getLicenseKey(),
				licenseKeyMappingDto.getTspId()) == null) {
			List<ServiceError> errorList = new ArrayList<>();
			errorList.add(new ServiceError(LicenseKeyManagerExceptionConstants.LICENSEKEY_NOT_FOUND.getErrorCode(),
					LicenseKeyManagerExceptionConstants.LICENSEKEY_NOT_FOUND.getErrorMessage()));
			throw new LicenseKeyServiceException(errorList);
		}

		licenseKeyManagerUtil.areValidPermissions(licenseKeyMappingDto.getPermissions());

		LicenseKeyPermission licenseKeyPermissionEntity = new LicenseKeyPermission();
		licenseKeyPermissionEntity.setLKey(licenseKeyMappingDto.getLicenseKey());
		licenseKeyPermissionEntity.setActive(true);
		licenseKeyPermissionEntity.setCreatedDateTimes(licenseKeyManagerUtil.getCurrentTimeInUTCTimeZone());
		licenseKeyPermissionEntity.setCreatedBy(LicenseKeyManagerPropertyConstants.DEFAULT_CREATED_BY.getValue());

		LicenseKeyPermission licenseKeyPermission = licenseKeyPermissionsRepository
				.findByLKey(licenseKeyMappingDto.getLicenseKey());

		if (licenseKeyPermission == null) {
			licenseKeyPermissionEntity.setPermission(
					licenseKeyManagerUtil.concatPermissionsIntoASingleRow(licenseKeyMappingDto.getPermissions()));
			licenseKeyPermissionsRepository.save(licenseKeyPermissionEntity);
		} else {
			licenseKeyMappingDto.getPermissions().add(licenseKeyPermission.getPermission());
			licenseKeyPermissionEntity.setPermission(
					licenseKeyManagerUtil.concatPermissionsIntoASingleRow(licenseKeyMappingDto.getPermissions()));
			licenseKeyPermissionsRepository.updatePermissionList(
					licenseKeyManagerUtil.concatPermissionsIntoASingleRow(licenseKeyMappingDto.getPermissions()),
					licenseKeyMappingDto.getLicenseKey(), licenseKeyManagerUtil.getCurrentTimeInUTCTimeZone(),
					LicenseKeyManagerPropertyConstants.DEFAULT_CREATED_BY.getValue());
		}

		return LicenseKeyManagerPropertyConstants.MAPPED_STATUS.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService#
	 * fetchLicenseKeyPermissions(java.lang.Object, java.lang.Object)
	 */
	@Override
	public List<String> fetchLicenseKeyPermissions(String tspID, String licenseKey) {
		licenseKeyManagerUtil.validateTSPAndLicenseKey(tspID, licenseKey);
		if (licenseKeyTspMapRepository.findByLKeyAndTspId(licenseKey, tspID) == null) {
			List<ServiceError> errorList = new ArrayList<>();
			errorList.add(new ServiceError(LicenseKeyManagerExceptionConstants.LICENSEKEY_NOT_FOUND.getErrorCode(),
					LicenseKeyManagerExceptionConstants.LICENSEKEY_NOT_FOUND.getErrorMessage()));
			throw new LicenseKeyServiceException(errorList);
		}
		if (licenseKeyManagerUtil.getCurrentTimeInUTCTimeZone()
				.isAfter(licenseKeyListRepository.findByLicenseKey(licenseKey).getExpiryDateTimes())) {
			List<ServiceError> errorList = new ArrayList<>();
			errorList.add(new ServiceError(LicenseKeyManagerExceptionConstants.LICENSEKEY_EXPIRED.getErrorCode(),
					LicenseKeyManagerExceptionConstants.LICENSEKEY_EXPIRED.getErrorMessage()));
			throw new LicenseKeyServiceException(errorList);

		}

		LicenseKeyPermission licenseKeyPermissions = licenseKeyPermissionsRepository.findByLKey(licenseKey);
		return Arrays.asList(licenseKeyPermissions.getPermission().split(","));
	}
}
