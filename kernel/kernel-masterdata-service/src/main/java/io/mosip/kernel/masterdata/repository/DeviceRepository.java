
package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Device;

/**
 * Repository function to fetching device details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public interface DeviceRepository extends BaseRepository<Device, String> {

	List<Device> findByLangCode(String langCode);

	@Query(value = "select d.id, d.name, d.mac_address, d.serial_num, d.ip_address, d.dspec_id, d.lang_code, d.is_active, s.dtyp_code from master.device_master  d, master.device_spec s where  d.dspec_id = s.id  and  d.lang_code = ?1 and s.dtyp_code = ?2", nativeQuery = true)
	List<Object[]> findByLangCodeAndDtypeCode(String langCode, String deviceTypeCode);

}
