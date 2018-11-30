package io.mosip.registration.processor.status.repositary;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.status.entity.BaseRegistrationEntity;

/**
 * The Interface RegistrationRepositary.
 *
 * @param <T>
 *            the generic type
 * @param <E>
 *            the element type
 */
@Repository
public interface RegistrationRepositary<T extends BaseRegistrationEntity, E> extends BaseRepository<T, E> {

	@Query("SELECT trn FROM TransactionEntity trn WHERE trn.registrationId=:regId and trn.statusCode=:statusCode")
	public List<T> getTransactionByRegIdAndStatusCode(@Param("regId") String regId,
			@Param("statusCode") String statusCode);

}
