package io.mosip.kernel.lkeymanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationResponseDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto;

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
	 * This method will map license key to several permissions.
	 * 
	 * @param licenseKeyMappingDto
	 *            the {@link LicenseKeyMappingDto}.
	 * @return the response entity.
	 */
	@PostMapping(value = "/v1.0/license/map")
	public ResponseEntity<String> mapLicenseKey(@RequestBody LicenseKeyMappingDto licenseKeyMappingDto) {
		return new ResponseEntity<>(licenseKeyManagerService.mapLicenseKey(licenseKeyMappingDto), HttpStatus.OK);
	}

	/**
	 * This method will fetch the mapped permissions for a license key.
	 * 
	 * @param licenseKey
	 *            the license key of which the permissions need to be fetched.
	 * @return the permissions fetched.
	 */
	@GetMapping(value = "/v1.0/license/fetch")
	public ResponseEntity<List<String>> fetchLicenseKeyPermissions(@RequestParam("tspId") String tspId,
			@RequestParam("licenseKey") String licenseKey) {
		return new ResponseEntity<>(licenseKeyManagerService.fetchLicenseKeyPermissions(tspId, licenseKey),
				HttpStatus.OK);
	}
}
