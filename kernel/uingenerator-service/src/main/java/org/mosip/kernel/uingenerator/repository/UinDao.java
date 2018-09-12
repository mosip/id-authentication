package org.mosip.kernel.uingenerator.repository;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UinDao extends BaseRepository<UinBean, String> {
	@Query
	public int countFreeUin(boolean used);

	@Query
	public UinBean findUnusedUin(boolean used);

}
