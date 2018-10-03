package io.mosip.registration.processor.packet.decryptor.job;

import io.vertx.core.eventbus.EventBus;
/**
 * Mosip Event Bus class
 * @author Jyoti Prakash Nayak
 *
 */
public class MosipEventBus {

	private EventBus eventBus;

	/**
	 * constructor
	 * @param eventBus
	 */
	public MosipEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	/**
	 * getter for eventBus
	 * @return void
	 */
	public EventBus getEventBus() {
		return eventBus;
	}
}