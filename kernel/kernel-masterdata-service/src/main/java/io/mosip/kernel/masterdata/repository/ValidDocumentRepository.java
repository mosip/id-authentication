package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.ValidDocumentId;

@Repository
public interface ValidDocumentRepository extends BaseRepository<ValidDocument,ValidDocumentId>{

}
