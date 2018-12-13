package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;

/**
 * 
 * Repository function to fetching and save device specification details
 * 
 * @author Uday Kumar
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
public interface DeviceSpecificationRepository extends BaseRepository<DeviceSpecification, String> {
	/**
	 * This method trigger query to fetch the Device Specification detail for the
	 * given language code.
	 *
	 * @param langCode
	 *            languageCode provided by user
	 * 
	 * @return List Device specific Details fetched from database
	 */
	@Query("FROM DeviceSpecification d where d.langCode = ?1 and (d.isDeleted is null or d.isDeleted = false)")
	List<DeviceSpecification> findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode);

	/**
	 * This method trigger query to fetch the Device Specification detail for the
	 * given language code and device Type Code.
	 *
	 * @param langCode
	 *            LanguageCode provided by user
	 * @param deviceTypeCode
	 *            Device Type Code provided by user
	 * @return List Device specific Details fetched from database
	 */
	@Query("FROM DeviceSpecification d where d.langCode = ?1 and d.deviceTypeCode = ?2 and (d.isDeleted is null or d.isDeleted = false)")
	List<DeviceSpecification> findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode,
			String deviceTypeCode);
}
