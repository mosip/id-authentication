package io.mosip.registration.processor.abis.queue.dto;

import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import lombok.Data;
/**
 * 
 * @author Girish Yarru
 *
 */
@Data
public class AbisQueueDetails {
	private String name;
	private String host;
	private String port;
	private String brokerUrl;
	private String inboundQueueName;
	private String outboundQueueName;
	private String pingInboundQueueName;
	private String pingOutboundQueueName;
	private String userName;
	private String password;
	private String typeOfQueue;
	private MosipQueue mosipQueue;

}
