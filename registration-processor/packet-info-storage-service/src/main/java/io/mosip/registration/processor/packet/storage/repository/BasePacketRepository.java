package io.mosip.registration.processor.packet.storage.repository;

import org.springframework.stereotype.Repository;
import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
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

}
