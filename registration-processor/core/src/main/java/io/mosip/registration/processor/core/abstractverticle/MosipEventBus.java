package io.mosip.registration.processor.core.abstractverticle;

import io.mosip.registration.processor.core.spi.eventbus.MosipEventbusFactory;
import io.vertx.core.Vertx;

public class MosipEventBus implements MosipEventbusFactory<Vertx> {
	
	private Vertx vertx = null;
	
	public MosipEventBus(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public Vertx getEventbus() {
		return this.vertx;
	}

}
