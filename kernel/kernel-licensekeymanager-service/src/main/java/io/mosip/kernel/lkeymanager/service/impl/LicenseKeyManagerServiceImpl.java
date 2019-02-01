package io.mosip.kernel.lkeymanager.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService;
import io.mosip.kernel.lkeymanager.constant.LicenseKeyManagerPropertyConstants;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto;
import io.mosip.kernel.lkeymanager.entity.LicenseKey;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyPermissions;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyPermissionsRepository;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyRepository;
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
	 * Autowired reference for {@link LicenseKeyRepository}
	 */
	@Autowired
	LicenseKeyRepository licenseKeyRepository;

	/**
	 * Autowired reference for {@link LicenseKeyPermissionsRepository}.
	 */
	@Autowired
	LicenseKeyPermissionsRepository licenseKeyPermissionsRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService#
	 * generateLicenseKey(java.lang.Object)
	 */
	@Override
	public String generateLicenseKey(LicenseKeyGenerationDto licenseKeyGenerationDto) {
		licenseKeyManagerUtil.hasNullOrEmptyParameters(licenseKeyGenerationDto.getTspId());
		LicenseKey licenseKeyEntity = new LicenseKey();
		licenseKeyEntity.setTspId(licenseKeyGenerationDto.getTspId());
		licenseKeyEntity.setLKey(licenseKeyManagerUtil.generateLicense());
		licenseKeyEntity.setCreatedAt(licenseKeyManagerUtil.getCurrentTimeInUTCTimeZone());
		licenseKeyEntity.setCreatedBy(LicenseKeyManagerPropertyConstants.DEFAULT_CREATED_BY.getValue());
		licenseKeyRepository.save(licenseKeyEntity);
		return licenseKeyEntity.getLKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService#
	 * mapLicenseKey(java.lang.Object)
	 */
	@Override
	public String mapLicenseKey(LicenseKeyMappingDto licenseKeyMappingDto) {
		licenseKeyManagerUtil.hasNullOrEmptyParameters(licenseKeyMappingDto.getPermissions(),
				licenseKeyMappingDto.getLKey(), licenseKeyMappingDto.getTspId());
		licenseKeyManagerUtil.areValidPermissions(licenseKeyMappingDto.getPermissions());
		LicenseKeyPermissions licenseKeyPermissionsEntity = new LicenseKeyPermissions();
		licenseKeyMappingDto.getPermissions().forEach(permission -> {
			licenseKeyPermissionsEntity.setLKey(licenseKeyMappingDto.getLKey());
			licenseKeyPermissionsEntity.setTspId(licenseKeyMappingDto.getTspId());
			licenseKeyPermissionsEntity.setPermission(permission);
			licenseKeyPermissionsEntity.setCreatedAt(licenseKeyManagerUtil.getCurrentTimeInUTCTimeZone());
			licenseKeyPermissionsRepository.save(licenseKeyPermissionsEntity);
		});
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
		licenseKeyManagerUtil.hasNullOrEmptyParameters(tspID, licenseKey);
		List<String> permissionsList = new ArrayList<>();
		LicenseKey licenseKeyDetails = licenseKeyRepository.findByTspIdAndLKey(tspID, licenseKey);
		if (licenseKeyDetails != null && licenseKeyManagerUtil.isLicenseExpired(licenseKeyDetails.getCreatedAt())) {
			List<LicenseKeyPermissions> licenseKeyPermissions = licenseKeyPermissionsRepository.findByTspId(tspID);
			licenseKeyPermissions.forEach(permission -> permissionsList.add(permission.getPermission()));
		}
		return permissionsList;
	}
}
