
package io.mosip.kernel.masterdata.repository;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.IdType;

/**
 * Interface for idtype repository.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface IdTypeRepository extends BaseRepository<IdType, String> {
	/**
	 * Method that returns the list of id types for the specific language code.
	 * 
	 * @param languageCode
	 *            the language code.
	 * @return the list of id types.
	 */
	List<IdType> findByLangCodeAndIsDeletedFalse(String languageCode);
}
