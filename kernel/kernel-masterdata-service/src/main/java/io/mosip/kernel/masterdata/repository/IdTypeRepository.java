
package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

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
	@Query("FROM IdType WHERE lang_code = ?1 and (isDeleted is null or isDeleted =false)")
	List<IdType> findByLangCode(String languageCode);
}
