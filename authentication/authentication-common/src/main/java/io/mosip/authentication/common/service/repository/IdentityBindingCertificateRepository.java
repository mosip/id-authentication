package io.mosip.authentication.common.service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.IdentityBindingCertificateStore;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

import java.util.List;

/**
 * Repository class for Identity binding certificate store
 * 
 * @author Mahammed Taheer
 *
 */
@Repository
public interface IdentityBindingCertificateRepository extends BaseRepository<IdentityBindingCertificateStore, String> {

	@Query("SELECT count(i.id) FROM IdentityBindingCertificateStore i where i.publicKeyHash = :publicKeyHash and i.token in " +
				" (SELECT cs.token FROM IdentityBindingCertificateStore cs where cs.idVidHash = :idVidHash)")
	public int countPublicKeysByIdHash(@Param("idVidHash") String idVidHash, @Param("publicKeyHash") String publicKeyHash);


	@Query("SELECT i.certThumbprint, i.authFactor, i.certificateData, i.authFactor FROM IdentityBindingCertificateStore i where i.idVidHash = :idVidHash and i.partnerName = :partnerId and " +
			" ( i.isDeleted is null or i.isDeleted = false )")
	List<Object[]> findAllByIdVidHashAndPartnerId(@Param("idVidHash") String idVidHash, @Param("partnerId") String partnerId);
}
