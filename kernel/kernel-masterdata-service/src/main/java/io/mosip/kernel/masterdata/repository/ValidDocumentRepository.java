package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;

@Repository
public interface ValidDocumentRepository extends BaseRepository<ValidDocument,ValidDocumentID>{

	 List<ValidDocument> findByDocCategoryCode(String code);
}
