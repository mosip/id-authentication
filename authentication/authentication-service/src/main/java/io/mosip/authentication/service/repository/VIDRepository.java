package io.mosip.authentication.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.VIDEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * The Interface VIDRepository is used to fetch VIDEntity.
 * 
 * @author Rakesh Roshan
 */
@Repository
public interface VIDRepository extends BaseRepository<VIDEntity, String> {

	@Query("Select vid from VIDEntity vid where vid.id =:vidNumber")
	Optional<VIDEntity> findUinByVid(@Param("vidNumber") String vidNumber);

	@Query(value = "SELECT * FROM ida.vid where uin=:uin ORDER BY generated_dtimes DESC", nativeQuery = true)
	List<VIDEntity> findByUIN(@Param("uin") String uin, Pageable pagaeable);

	@Query(value = "SELECT id FROM ida.vid where uin=:uin ORDER BY generated_dtimes DESC", nativeQuery = true)
	List<String> findVIDByUIN(@Param("uin") String uin, Pageable pagaeable);

}
