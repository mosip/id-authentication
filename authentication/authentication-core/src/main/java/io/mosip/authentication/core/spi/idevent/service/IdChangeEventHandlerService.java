package io.mosip.authentication.core.spi.idevent.service;

import java.util.List;

import io.mosip.idrepository.core.dto.EventDTO;

public interface IdChangeEventHandlerService {
	
	boolean handleIdEvent(List<EventDTO> events);
	
}
