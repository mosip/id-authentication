package io.mosip.kernel.masterdata.repository;

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
	 * This method trigger query to fetch the Machine detail for the given machine id and language
	 * code.
	 * 
	 * @param Id
	 *            Machine Id provided by user
	 * @param langCode
	 *            languageCode provided by user
	 * @return MachineDetail fetched from database
	 */
	MachineDetail findAllByIdAndLangCode(String id, String langCode);

}
