package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.TemplateType;

@Repository
public interface TemplateTypeRepository extends BaseRepository<TemplateType, String> {

}
