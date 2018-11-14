package io.mosip.preregistration.core.repository;


import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.core.entity.Groupid;

@Repository
public interface GroupidRepository extends BaseRepository<Groupid, String> {

}
