package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceService;

/**
 * MDS repository class.
 *
 * @author Srinivasan
 * @author Megha Tanga
 */
@Repository
public interface MOSIPDeviceServiceRepository extends BaseRepository<MOSIPDeviceService, String> {

	/**
	 * Find by id and is active is true.
	 *
	 * @param id
	 *            the id
	 * @return the device service
	 */
	List<MOSIPDeviceService> findBySwVersionAndIsActiveIsTrue(String id);

	/**
	 * Find by provider id and service version.
	 *
	 * @param id
	 *            the id
	 * @param providerId
	 *            the provider id
	 * @return the device service
	 */
	MOSIPDeviceService findByDeviceProviderIdAndSwVersion(String id, String deviceProviderId);
	
	/**
	 * Find by provider id and service version.
	 *
	 * @param id
	 *            the id
	 * @param providerId
	 *            the provider id
	 * @return the device service
	 */
	MOSIPDeviceService findByDeviceProviderIdAndSwVersionAndMakeAndModel(String id, String deviceProviderId,String make,String model);

	/**
	 * Find by device code.
	 *
	 * @param deviceCode
	 *            the device code
	 * @return {@link MOSIPDeviceService} the list
	 */
	@Query(value = "select * from master.mosip_device_service a,master.registered_device_master b where a.dtype_code=b.dtype_code and a.dstype_code=b.dstype_code and a.make=b.make and a.model=b.model and a.dprovider_id=b.provider_id and b.code=?1", nativeQuery = true)
	List<MOSIPDeviceService> findByDeviceCode(String deviceCode);
	
	/**
	 * Find by device detail.
	 *
	 * @param version the version
	 * @param deviceTypeCode the device type code
	 * @param devicesTypeCode the devices type code
	 * @param make the make
	 * @param model the model
	 * @param dp the dp
	 * @return the MOSIP device service
	 */
	@Query(value = "select * from mosip_device_service where sw_version=?1 and dtype_code=?2 and dstype_code=?3 and make=?4 and model=?5 and dprovider_id=?6", nativeQuery = true)
	MOSIPDeviceService findByDeviceDetail(String version, String deviceTypeCode, String devicesTypeCode, String make,
			String model, String dp);

}
