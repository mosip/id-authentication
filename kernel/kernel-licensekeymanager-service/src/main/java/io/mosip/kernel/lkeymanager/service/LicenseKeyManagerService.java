package io.mosip.kernel.lkeymanager.service;

import java.util.List;

import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto;

public interface LicenseKeyManagerService {
	public String generateLicenseKey(LicenseKeyGenerationDto licenseKeyGenerationDto);

	public String mapLicenseKey(LicenseKeyMappingDto licenseKeyMappingDto);

	public List<String> fetchLicenseKeyPermissions(String tspID, String licenseKey);

}
