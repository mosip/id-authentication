package io.mosip.kernel.pridgenerator.repository;


import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.pridgenerator.entity.Prid;

@Repository
public interface PridRepository extends BaseRepository<Prid, String> {

}
