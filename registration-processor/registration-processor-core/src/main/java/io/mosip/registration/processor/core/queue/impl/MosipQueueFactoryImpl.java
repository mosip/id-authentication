package io.mosip.registration.processor.core.queue.impl;

import io.mosip.registration.processor.core.spi.queue.MosipQueueFactory;

public class MosipQueueFactoryImpl implements MosipQueueFactory<MosipQueue> {

	@Override
	public MosipQueue getQueue(String typeOfQueue, String u) {
		if(typeOfQueue == "activeMq") {
			return null;
		}
		else {
			return 
		}
	}
	

}
