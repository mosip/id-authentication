package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
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
	 * @return ReasonCategory - reasoncategory obj
	 */
	List<ReasonCategory> findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull();
	/**
	 * 
	 * @param code
	 * @param languageCode
	 * @return reasonCategoryObj
	 */
	@Query("FROM ReasonCategory r where r.code=?1 and r.langCode=?2 and (r.isDeleted is null or r.isDeleted=false)")
	List<ReasonCategory> findReasonCategoryByCodeAndLangCode(String code, String languageCode);

	
}
