package io.mosip.registration.processor.core.spi.queue;

/**
 * @author Pranav Kumar
 *
 * @param <Q> The type of Queue
 */
public interface MosipQueueConnectionFactory<Q> {

	/**
	 * Method to return Connection to the Queue
	 * 
	 * @param typeOfQueue The type of Queue
	 * @param username    Username
	 * @param password    password
	 * @param Url         Url of installation path
	 * @return
	 */
	public Q createConnection(String typeOfQueue, String username, String password, String Url);

}
