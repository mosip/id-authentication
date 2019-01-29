package io.mosip.kernel.lkeymanager.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto;
import io.mosip.kernel.lkeymanager.entity.LicenseKey;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyPermissions;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyPermissionsRepository;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyRepository;
import io.mosip.kernel.lkeymanager.service.LicenseKeyManagerService;
import io.mosip.kernel.lkeymanager.util.LicenseKeyManagerUtil;

/**
 * Implementation class for {@link LicenseKeyManagerService}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Service
public class LicenseKeyManagerServiceImpl implements LicenseKeyManagerService {
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

	/**
	 * The length of license key to be generated.
	 */
	@Value("${mosip.kernel.licensekey.length}")
	private String licenseKeyLength;

	/**
	 * The time after which a license key expires.
	 */
	@Value("${mosip.kernel.licensekey.expiry-period-in-days}")
	private String licenseKeyExpiryPeriod;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.lkeymanager.service.LicenseKeyManagerService#
	 * generateLicenseKey()
	 */
	@Override
	public String generateLicenseKey(LicenseKeyGenerationDto licenseKeyGenerationDto) {
		LicenseKey licenseKeyEntity = new LicenseKey();
		licenseKeyEntity.setTspId(licenseKeyGenerationDto.getTspId());
		licenseKeyEntity.setLKey(licenseKeyManagerUtil.generateLicense());
		licenseKeyEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
		licenseKeyEntity.setCreatedBy("defaultadmin@mosip.io");
		licenseKeyRepository.save(licenseKeyEntity);
		return licenseKeyEntity.getLKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.lkeymanager.service.LicenseKeyManagerService#mapLicenseKey(io
	 * .mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto)
	 */
	@Override
	public String mapLicenseKey(LicenseKeyMappingDto licenseKeyMappingDto) {
		LicenseKeyPermissions licenseKeyPermissionsEntity = new LicenseKeyPermissions();
		licenseKeyMappingDto.getPermissions().forEach(permission -> {
			licenseKeyPermissionsEntity.setLKey(licenseKeyMappingDto.getLKey());
			licenseKeyPermissionsEntity.setTspId(licenseKeyMappingDto.getTspId());
			licenseKeyPermissionsEntity.setPermission(permission);
			licenseKeyPermissionsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
			licenseKeyPermissionsRepository.save(licenseKeyPermissionsEntity);
		});
		return "Mapped License with the permissions";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.lkeymanager.service.LicenseKeyManagerService#
	 * fetchLicenseKeyPermissions(java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> fetchLicenseKeyPermissions(String tspId, String licenseKey) {
		List<String> permissionsList = new ArrayList<>();
		LicenseKey licenseKeyDetails = licenseKeyRepository.findByTspIdAndLKey(tspId, licenseKey);
		if (licenseKeyDetails != null && (licenseKeyDetails.getCreatedAt().until(LocalDateTime.now(ZoneId.of("UTC")),
				ChronoUnit.DAYS)) < Integer.parseInt(licenseKeyExpiryPeriod)) {
			List<LicenseKeyPermissions> licenseKeyPermissions = licenseKeyPermissionsRepository.findByTspId(tspId);
			licenseKeyPermissions.forEach(permission -> permissionsList.add(permission.getPermission()));
		}
		return permissionsList;
	}
}
