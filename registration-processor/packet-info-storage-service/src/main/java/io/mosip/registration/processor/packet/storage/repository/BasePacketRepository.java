package io.mosip.registration.processor.packet.storage.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.processor.packet.storage.entity.BasePacketEntity;
/**
 * 
 * @author Girish Yarru
 *
 * @param <E>
 * @param <T>
 */
@Repository
public interface BasePacketRepository<E extends BasePacketEntity<?>, T> extends BaseRepository<E, T> {

}
