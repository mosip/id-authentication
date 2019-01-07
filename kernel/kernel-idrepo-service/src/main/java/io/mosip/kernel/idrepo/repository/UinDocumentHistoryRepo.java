package io.mosip.kernel.idrepo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.kernel.idrepo.entity.UinDocumentHistory;

/**
 * The Interface UinDocumentHistoryRepo.
 *
 * @author Manoj SP
 */
public interface UinDocumentHistoryRepo extends JpaRepository<UinDocumentHistory, String> {
}
