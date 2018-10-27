package io.mosip.registration.core.repository;


import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.core.entity.Groupid;

@Repository
public interface GroupidRepository extends BaseRepository<Groupid, String> {

}
