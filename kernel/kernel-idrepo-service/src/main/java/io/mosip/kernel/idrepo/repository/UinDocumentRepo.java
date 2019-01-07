package io.mosip.kernel.idrepo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.kernel.idrepo.entity.UinDocument;

/**
 * The Interface UinDocumentRepo.
 *
 * @author Manoj SP
 */
public interface UinDocumentRepo extends JpaRepository<UinDocument, String> {
}
