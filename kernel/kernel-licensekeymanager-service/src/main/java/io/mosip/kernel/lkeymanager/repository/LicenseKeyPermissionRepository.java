package io.mosip.kernel.lkeymanager.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyPermission;
import io.mosip.kernel.lkeymanager.entity.id.LicenseKeyPermissionID;

/**
 * Repository class for {@link LicenseKeyPermission}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Repository
public interface LicenseKeyPermissionRepository extends BaseRepository<LicenseKeyPermission, LicenseKeyPermissionID> {
	/**
	 * Method to find license key permissions by license key.
	 * 
	 * @param licenseKey
	 *            the license key for which permission needs to be fetched.
	 * @return the license key entity.
	 */
	public LicenseKeyPermission findByLKey(String licenseKey);

	/**
	 * Method to update license key permissions.
	 * 
	 * @param updatedPermissionString
	 *            the updated permission list.
	 * @param licenseKey
	 *            the license key.
	 * @param updationTime
	 *            the time at which the list is updated.
	 * @param updatedBy
	 *            the list updated by.
	 * @return the permission entity response.
	 */
	@Modifying
	@Query("UPDATE LicenseKeyPermission p SET p.permission =?1, p.updatedDateTimes =?3, p.updatedBy=?4 WHERE p.lKey =?2")
	public int updatePermissionList(String updatedPermissionString, String licenseKey, LocalDateTime updationTime,
			String updatedBy);
}
