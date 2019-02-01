package io.mosip.kernel.core.licensekeymanager.spi;

import java.util.List;

public interface LicenseKeyManagerService<T, D, S> {
	public T generateLicenseKey(D licenseKeyGenerationDto);

	public T mapLicenseKey(S licenseKeyMappingDto);

	public List<T> fetchLicenseKeyPermissions(T tspID, T licenseKey);

}
