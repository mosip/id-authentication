package io.mosip.kernel.lkeymanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyFetchResponseDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationResponseDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingResponseDto;

/**
 * Controller class that provides various methods for license key management
 * such as to generate license key for a specified TSP ID, mapping several
 * permissions to a generated license key, fetching the specified permissions
 * for a license key.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestController
public class LicenseKeyController {
	/**
	 * Autowired reference for {@link LicenseKeyManagerService}.
	 */
	@Autowired
	LicenseKeyManagerService<String, LicenseKeyGenerationDto, LicenseKeyMappingDto> licenseKeyManagerService;

	/**
	 * This method will generate license key against a certain TSP ID.
	 * 
	 * @param licenseKeyGenerationDto
	 *            the {@link LicenseKeyGenerationDto}.
	 * @return the response entity.
	 */
	@PostMapping(value = "/v1.0/license/generate")
	public ResponseEntity<LicenseKeyGenerationResponseDto> generateLicenseKey(
			@RequestBody LicenseKeyGenerationDto licenseKeyGenerationDto) {
		LicenseKeyGenerationResponseDto responseDto = new LicenseKeyGenerationResponseDto();
		responseDto.setLicenseKey(licenseKeyManagerService.generateLicenseKey(licenseKeyGenerationDto));
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * This method will map license key to several permissions. The permissions
	 * provided must be present in the master list.
	 * 
	 * @param licenseKeyMappingDto
	 *            the {@link LicenseKeyMappingDto}.
	 * @return the response entity.
	 */
	@PostMapping(value = "/v1.0/license/map")
	public ResponseEntity<LicenseKeyMappingResponseDto> mapLicenseKey(
			@RequestBody LicenseKeyMappingDto licenseKeyMappingDto) {
		LicenseKeyMappingResponseDto licenseKeyMappingResponseDto = new LicenseKeyMappingResponseDto();
		licenseKeyMappingResponseDto.setStatus(licenseKeyManagerService.mapLicenseKey(licenseKeyMappingDto));
		return new ResponseEntity<>(licenseKeyMappingResponseDto, HttpStatus.OK);
	}

	/**
	 * This method will fetch the mapped permissions for a license key.
	 * 
	 * @param licenseKey
	 *            the license key of which the permissions need to be fetched.
	 * @return the permissions fetched.
	 */
	@GetMapping(value = "/v1.0/license/fetch")
	public ResponseEntity<LicenseKeyFetchResponseDto> fetchLicenseKeyPermissions(@RequestParam("tspId") String tspId,
			@RequestParam("licenseKey") String licenseKey) {
		LicenseKeyFetchResponseDto licenseKeyFetchResponseDto = new LicenseKeyFetchResponseDto();
		licenseKeyFetchResponseDto
				.setMappedPermissions(licenseKeyManagerService.fetchLicenseKeyPermissions(tspId, licenseKey));
		return new ResponseEntity<>(licenseKeyFetchResponseDto, HttpStatus.OK);
	}
}
