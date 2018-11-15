package io.mosip.kernel.masterdata.repository;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.MachineDetail;

/**
 * Repository function to fetching machine details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public interface MachineDetailRepository extends BaseRepository<MachineDetail, String> {
	/**
	 * This method trigger query to fetch the all Machine details
	 * code.
	 * 
	 * @return MachineDetail fetched from database
	 */
	List<MachineDetail> findAllByIsActiveTrueAndIsDeletedFalse();
	/**
	 * This method trigger query to fetch the Machine detail for the given machine id and language
	 * code.
	 * 
	 * @param Id
	 *            Machine Id provided by user
	 * @param langCode
	 *            languageCode provided by user
	 * @return MachineDetail fetched from database
	 */
	MachineDetail findAllByIdAndLangCodeAndIsActiveTrueAndIsDeletedFalse(String id, String langCode);

}
