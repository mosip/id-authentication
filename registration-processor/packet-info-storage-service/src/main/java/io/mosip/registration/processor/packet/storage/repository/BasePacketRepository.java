package io.mosip.registration.processor.packet.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.packet.storage.entity.BasePacketEntity;
/**
 * The Interface BasePacketRepository.
 *
 * @author Girish Yarru
 * @param <E> the element type
 * @param <T> the generic type
 */
@Repository
public interface BasePacketRepository<E extends BasePacketEntity<?>, T> extends BaseRepository<E, T> {

	@Query("SELECT qcUser FROM QcuserRegistrationIdEntity qcUser WHERE qcUser.id.usrId=:qcuserId")
	public List<E> findByUserId(@Param("qcuserId") String qcuserId);

	@Query("SELECT ape,ade FROM ApplicantPhotographEntity ape, ApplicantDemographicEntity ade"
            + " WHERE ade.id.regId=:regId")
    public List<Object[]> getApplicantInfo(@Param("regId") String regId);
}
