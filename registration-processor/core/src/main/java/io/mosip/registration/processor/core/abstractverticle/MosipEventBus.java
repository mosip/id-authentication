package io.mosip.registration.processor.core.abstractverticle;

import io.mosip.registration.processor.core.spi.eventbus.MosipEventbusFactory;
import io.vertx.core.Vertx;

/**
 * This class wraps vertx instance to MosipEventBus
 * 
 * @author Pranav Kumar
 * @since 0.0.1
 */
public class MosipEventBus implements MosipEventbusFactory<Vertx> {

	private Vertx vertx = null;

	/**
	 * @param vertx
	 *            The vertx instance
	 */
	public MosipEventBus(Vertx vertx) {
		this.vertx = vertx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.eventbus.MosipEventbusFactory#
	 * getEventbus()
	 */
	@Override
	public Vertx getEventbus() {
		return this.vertx;
	}

}
