package io.mosip.kernel.idrepo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.kernel.idrepo.entity.UinDetail;

/**
 * The Interface UinDetailRepo.
 *
 * @author Manoj SP
 */
public interface UinDetailRepo extends JpaRepository<UinDetail, String> {

}
