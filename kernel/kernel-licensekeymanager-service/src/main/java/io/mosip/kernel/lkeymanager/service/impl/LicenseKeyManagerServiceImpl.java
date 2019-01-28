package io.mosip.kernel.lkeymanager.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.lkeymanager.controller.dto.LicenseKeyGenerationDto;
import io.mosip.kernel.lkeymanager.controller.dto.LicenseKeyMappingDto;
import io.mosip.kernel.lkeymanager.entity.LicenseKey;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyRepository;
import io.mosip.kernel.lkeymanager.service.LicenseKeyManagerService;
import io.mosip.kernel.lkeymanager.util.LicenseKeyManagerUtil;

@Service
public class LicenseKeyManagerServiceImpl implements LicenseKeyManagerService {
	@Autowired
	LicenseKeyManagerUtil licenseKeyManagerUtil;

	@Autowired
	LicenseKeyRepository licenseKeyRepository;

	/**
	 * The length of license key to be generated.
	 */
	@Value("${mosip.kernel.licensekey.length}")
	private String licenseKeyLength;

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

	@Override
	public String mapLicenseKey(LicenseKeyMappingDto licenseKeyMappingDto) {
		return null;
	}

	@Override
	public List<String> fetchLicenseKeyPermissions(String licenseKey) {
		return null;
	}
}
