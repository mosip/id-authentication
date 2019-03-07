package io.mosip.kernel.idgenerator.prid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.prid.entity.Prid;

@Repository
public interface PridRepository extends BaseRepository<Prid, String>{

	@Query("from Prid")
	List<Prid> findRandomValues();
	
	@Modifying
	@Query("UPDATE Prid p SET p.sequenceCounter=?1 WHERE p.randomValue=?2")
	int updateCounterValue(String sequenceCounter,String randomValue);
}
