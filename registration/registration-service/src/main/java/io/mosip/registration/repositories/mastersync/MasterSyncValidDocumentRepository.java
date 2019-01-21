package io.mosip.registration.repositories.mastersync;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterValidDocument;
import io.mosip.registration.entity.mastersync.id.ValidDocumentID;

public interface MasterSyncValidDocumentRepository extends BaseRepository<MasterValidDocument,ValidDocumentID>{

}
