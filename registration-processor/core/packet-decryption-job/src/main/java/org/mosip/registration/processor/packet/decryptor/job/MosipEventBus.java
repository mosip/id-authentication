package org.mosip.registration.processor.packet.decryptor.job;

import io.vertx.core.eventbus.EventBus;
public class MosipEventBus {

	private EventBus eventBus;

	public MosipEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	public EventBus getEventBus() {
		return eventBus;
	}
}