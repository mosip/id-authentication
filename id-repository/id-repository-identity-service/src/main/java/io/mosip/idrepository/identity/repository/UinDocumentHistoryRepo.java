package io.mosip.idrepository.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.idrepository.identity.entity.UinDocumentHistory;

/**
 * The Interface UinDocumentHistoryRepo.
 *
 * @author Manoj SP
 */
public interface UinDocumentHistoryRepo extends JpaRepository<UinDocumentHistory, String> {
}
