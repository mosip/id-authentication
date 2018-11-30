package io.mosip.registration.processor.manual.adjudication.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

import io.mosip.registration.processor.manual.adjudication.entity.ManualAdjudicationEntity;

@Repository
public interface ManualAdjudiacationRepository<T extends ManualAdjudicationEntity, E> extends BaseRepository<T, E> {

}
