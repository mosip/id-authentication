package io.mosip.registration.processor.packet.storage.repository;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import io.mosip.registration.processor.packet.storage.entity.BasePacketEntity;

/**
 * The Interface BasePacketRepository.
 *
 * @author Girish Yarru
 * @param <E>
 *            the element type
 * @param <T>
 *            the generic type
 */
@Repository
public interface BasePacketRepository<E extends BasePacketEntity<?>, T> extends BaseRepository<E, T> {

	@Query("SELECT qcUser FROM QcuserRegistrationIdEntity qcUser WHERE qcUser.id.usrId=:qcuserId")
	public List<E> findByUserId(@Param("qcuserId") String qcuserId);

	@Query("SELECT ape,ide FROM ApplicantPhotographEntity ape, IndividualDemographicDedupeEntity ide"
			+ " WHERE ide.id.refId=:refId")
	public List<Object[]> getApplicantInfo(@Param("refId") String regId);
}
