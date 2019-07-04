package io.mosip.registration.processor.core.queue.factory;

import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;

public class MosipQueueConnectionFactoryImpl implements MosipQueueConnectionFactory<MosipQueue> {

	@Override
	public MosipQueue createConnection(String typeOfQueue, String username, String password,
			String url) {
		if(typeOfQueue.equalsIgnoreCase("ACTIVEMQ")) {
			return new MosipActiveMq(typeOfQueue, username, password, url);
		}
		else {
			return null;
		}
	}
	

}
