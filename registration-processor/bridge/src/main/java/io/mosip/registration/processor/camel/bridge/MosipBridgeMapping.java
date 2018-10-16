package io.mosip.registration.processor.camel.bridge;

import org.apache.camel.CamelContext;

import io.mosip.registration.processor.camel.bridge.util.BridgeUtil;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
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
	
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.ERROR.getAddress()))
						.toVertx(MessageBusAddress.ERROR.getAddress()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.DEMOGRAPHIC_BUS_IN.getAddress()))
						.toVertx(MessageBusAddress.DEMOGRAPHIC_BUS_IN.getAddress()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_IN.getAddress()))
						.toVertx(MessageBusAddress.STRUCTURE_BUS_IN.getAddress()))
				/*.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.BIOMETRIC_BUS_IN.getAddress()))
						.toVertx(MessageBusAddress.BIOMETRIC_BUS_IN.getAddress()))*/
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.RETRY_BUS.getAddress()))
						.toVertx(MessageBusAddress.RETRY_BUS.getAddress()))

				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.BATCH_BUS.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_IN.getAddress())))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.STRUCTURE_BUS_OUT.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_OUT.getAddress())))
				/*.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.DEMOGRAPHIC_BUS_OUT.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.DEMOGRAPHIC_BUS_OUT.getAddress())))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.BIOMETRIC_BUS_OUT.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.BIOMETRIC_BUS_OUT.getAddress())))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.FAILURE_BUS.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.FAILURE_BUS.getAddress())))*/
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.RETRY_BUS.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.RETRY_BUS.getAddress())));
		return options;
	}

}
