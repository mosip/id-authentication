package io.mosip.kernel.lkeymanager.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.lkeymanager.constant.LicenseKeyManagerExceptionConstants;
import io.mosip.kernel.lkeymanager.constant.LicenseKeyManagerPropertyConstants;
import io.mosip.kernel.lkeymanager.exception.InvalidArgumentsException;
import io.mosip.kernel.lkeymanager.exception.LicenseKeyServiceException;

/**
 * This class provides several utility methods to be used in license key manager
 * service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Component
public class LicenseKeyManagerUtil {
	/**
	 * The list of specified permissions by ADMIN.
	 */
	@Value("#{'${mosip.kernel.licensekey.permissions}'.split(',')}")
	private List<String> validPermissions;

	/**
	 * The time after which a license key expires.
	 */
	@Value("${mosip.kernel.licensekey.expiry-period-in-days}")
	private String licenseKeyExpiryPeriod;

	/**
	 * The length of license key as specified by ADMIN.
	 */
	@Value("${mosip.kernel.licensekey.length}")
	private int licenseKeyLength;

	/**
	 * This method adds all the permissions into a single row separated by comma.
	 * 
	 * @param permissionsList
	 *            the list of permissions.
	 * @return the resultant string.
	 */
	public String concatPermissionsIntoASingleRow(List<String> permissionsList) {
		StringBuilder permissionString = new StringBuilder();
		int permissionsListCount = 0;
		for (String permission : permissionsList) {
			if (++permissionsListCount <= permissionsList.size() - 1) {
				permissionString.append(permission + ",");
			} else {
				permissionString.append(permission);
			}
		}
		return permissionString.toString();
	}

	/**
	 * This method validates whether the input permissions are from the master list
	 * or not.
	 * 
	 * @param inputPermissions
	 *            the list of input permissions.
	 * @return true if all the input permissions are valid.
	 */
	public boolean areValidPermissions(List<String> inputPermissions) {
		List<ServiceError> errorList = new ArrayList<>();
		if (!(inputPermissions.stream()
				.allMatch(permission -> validPermissions.stream().anyMatch(permission::contains)))) {
			errorList.add(new ServiceError(LicenseKeyManagerExceptionConstants.NOT_ACCEPTABLE_PERMISSION.getErrorCode(),
					LicenseKeyManagerExceptionConstants.NOT_ACCEPTABLE_PERMISSION.getErrorMessage()));
			throw new LicenseKeyServiceException(errorList);
		}
		return true;
	}

	/**
	 * Method that returns the current date-time in UTC time zone.
	 * 
	 * @return the local date time as specified.
	 */
	public LocalDateTime getCurrentTimeInUTCTimeZone() {
		return LocalDateTime.now(ZoneId.of(LicenseKeyManagerPropertyConstants.TIME_ZONE.getValue()));
	}

	/**
	 * Method that generates a random license key of specified length.
	 * 
	 * @return the generated license key.
	 */
	public String generateLicense() {
		List<ServiceError> errorList = new ArrayList<>();
		String licenseKey = RandomStringUtils.randomAlphanumeric(licenseKeyLength);
		if (licenseKey.length() != licenseKeyLength) {
			errorList.add(
					new ServiceError(LicenseKeyManagerExceptionConstants.INVALID_GENERATED_LICENSEKEY.getErrorCode(),
							LicenseKeyManagerExceptionConstants.INVALID_GENERATED_LICENSEKEY.getErrorMessage()));
			throw new LicenseKeyServiceException(errorList);
		}
		return licenseKey;
	}

	/**
	 * This method checks for null or empty parameters passed as input.
	 * 
	 * @param parameters
	 *            the input parameters.
	 */
	public void hasNullOrEmptyParameters(String... parameters) {
		List<ServiceError> validationErrorsList = new ArrayList<>();
		for (String parameter : parameters) {
			if (parameter == null || parameter.trim().length() == 0) {
				validationErrorsList.add(
						new ServiceError(LicenseKeyManagerExceptionConstants.ILLEGAL_INPUT_ARGUMENTS.getErrorCode(),
								LicenseKeyManagerExceptionConstants.ILLEGAL_INPUT_ARGUMENTS.getErrorMessage()));
			}
		}
		if (!validationErrorsList.isEmpty()) {
			throw new InvalidArgumentsException(validationErrorsList);
		}
	}

	/**
	 * This method checks for null or empty parameters passed as input.
	 * 
	 * @param parameterList
	 *            the list of input parameters.
	 * @param parameters
	 *            the input parameters.
	 */
	public void hasNullOrEmptyParameters(List<String> parameterList, String... parameters) {
		List<ServiceError> errorList = new ArrayList<>();
		for (String parameter : parameterList) {
			if (parameter == null || parameter.trim().length() == 0) {
				errorList.add(
						new ServiceError(LicenseKeyManagerExceptionConstants.ILLEGAL_INPUT_ARGUMENTS.getErrorCode(),
								LicenseKeyManagerExceptionConstants.ILLEGAL_INPUT_ARGUMENTS.getErrorMessage()));
				throw new LicenseKeyServiceException(errorList);
			}
		}
		for (String parameter : parameters) {
			if (parameter == null || parameter.trim().length() == 0) {
				errorList.add(
						new ServiceError(LicenseKeyManagerExceptionConstants.ILLEGAL_INPUT_ARGUMENTS.getErrorCode(),
								LicenseKeyManagerExceptionConstants.ILLEGAL_INPUT_ARGUMENTS.getErrorMessage()));
				throw new LicenseKeyServiceException(errorList);
			}
		}
	}
}
