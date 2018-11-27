package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
/**
 * 
 * @author Srinivasan
 *
 */
@Repository
public interface ReasonCategoryRepository extends BaseRepository<ReasonCategory, String> {

	
	/**
	 * 
	 * @return
	 */
	List<ReasonCategory> findReasonCategoryByIsDeletedFalse();
	/**
	 * 
	 * @param code
	 * @param languageCode
	 * @return
	 */
	List<ReasonCategory> findReasonCategoryByCodeAndLangCodeAndIsDeletedFalse(String code, String languageCode);

	
}
