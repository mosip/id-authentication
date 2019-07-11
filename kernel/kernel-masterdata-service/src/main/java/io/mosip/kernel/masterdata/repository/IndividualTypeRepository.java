package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.IndividualType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * Interface to do CRUD operation on Individual type
 * 
 * @author Bal Vikash Sharma
 *
 */
public interface IndividualTypeRepository extends BaseRepository<IndividualType, CodeAndLanguageCodeID> {

	@Query(value = "FROM IndividualType t where t.isActive = true and (t.isDeleted is null or t.isDeleted = false)")
	public List<IndividualType> findAll();

}
