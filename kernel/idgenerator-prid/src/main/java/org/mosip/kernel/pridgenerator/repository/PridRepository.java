package org.mosip.kernel.pridgenerator.repository;


import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.kernel.pridgenerator.entity.Prid;
import org.springframework.stereotype.Repository;

@Repository
public interface PridRepository extends BaseRepository<Prid, String> {

}
