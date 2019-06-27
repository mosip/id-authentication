package io.mosip.util;

public class MosipQueueConnectionFactoryImpl implements MosipQueueConnectionFactory<MosipQueue>{

	@Override
	public MosipQueue createConnection(String typeOfQueue, String username, String password, String Url) {
		if(typeOfQueue.equalsIgnoreCase("ACTIVEMQ")) {
			return new MosipActiveMq(username, password, Url);
		}
		else {
			return null;
		}
	
	}

}
