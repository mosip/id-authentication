package io.mosip.preregistration.documents.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.documents.entity.DocumentEntity;

/**
 * Document Repository
 * 
 * @author M1043008
 *
 */
@Repository("documentRepositoery")
@Transactional
public interface DocumentRepository extends BaseRepository<DocumentEntity, String> {
	
	//public static final String record ="SELECT document FROM document.document WHERE document.preregid= :preId and document.doc_cat_code= :catCode";

//	public void deleteAllByPreregId(String preregId);

	public boolean existsByPreregId(String preregId);

	List<DocumentEntity> findBypreregId(String preId);
	
	DocumentEntity findBydocumentId(Integer documentId);
	
	@Query("SELECT d FROM DocumentEntity d WHERE d.preregId= :preId AND d.doc_cat_code= :catCode")
	DocumentEntity findSingleDocument(@Param("preId")String preId,@Param("catCode") String catCode);
	
//	Boolean deleteAllBydocumentId(Integer documentId);
	public Long deleteAllBydocumentId(Integer documentId);
	
	public List<DocumentEntity> deleteAllBypreregId(String preregId);

}
