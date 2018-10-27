package io.mosip.kernel.idgenerator.prid.repository;


import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.kernel.idgenerator.prid.entity.Prid;

import org.springframework.stereotype.Repository;

@Repository
public interface PridRepository extends BaseRepository<Prid, String> {

}
