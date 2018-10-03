package io.mosip.registration.processor.core.bridge;
/**
 * @author Mukul Puspam
 */
import org.apache.camel.CamelContext;

import io.mosip.registration.processor.core.bridge.address.MessageBusAddress;
import io.mosip.registration.processor.core.bridge.util.BridgeUtil;
import io.vertx.camel.CamelBridgeOptions;
import io.vertx.camel.InboundMapping;
import io.vertx.camel.OutboundMapping;

/**
 * The Class MosipBridgeMapping.
 */
public class MosipBridgeMapping {

	/**
	 * Gets the mapping.
	 *
	 * @param camelContext the camel context
	 * @return the mapping
	 */
	public CamelBridgeOptions getMapping(CamelContext camelContext) {

		CamelBridgeOptions options = new CamelBridgeOptions(camelContext);
		options
	
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.ERROR))
						.toVertx(MessageBusAddress.ERROR.toString()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.DEMOGRAPHIC_BUS_IN))
						.toVertx(MessageBusAddress.DEMOGRAPHIC_BUS_IN.toString()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_IN))
						.toVertx(MessageBusAddress.STRUCTURE_BUS_IN.toString()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.BIOMETRIC_BUS_IN))
						.toVertx(MessageBusAddress.BIOMETRIC_BUS_IN.toString()))

				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.BATCH_BUS.toString())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.BATCH_BUS)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.STRUCTURE_BUS_OUT.toString())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_OUT)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.DEMOGRAPHIC_BUS_OUT.toString())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.DEMOGRAPHIC_BUS_OUT)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.BIOMETRIC_BUS_OUT.toString())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.BIOMETRIC_BUS_OUT)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.FAILURE_BUS.toString())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.FAILURE_BUS)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.RETRY_BUS.toString())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.RETRY_BUS)));
		return options;
	}

}
