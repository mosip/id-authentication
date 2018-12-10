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
	 * @param camelContext
	 *            the camel context
	 * @return the mapping
	 */
	public CamelBridgeOptions getMapping(CamelContext camelContext) {

		CamelBridgeOptions options = new CamelBridgeOptions(camelContext);
		options

				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.ERROR))
						.toVertx(MessageBusAddress.ERROR.getAddress()))
				.addInboundMapping(
						InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.DEMOGRAPHIC_BUS_IN))
								.toVertx(MessageBusAddress.DEMOGRAPHIC_BUS_IN.getAddress()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_IN))
						.toVertx(MessageBusAddress.STRUCTURE_BUS_IN.getAddress()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.QUALITY_CHECK_BUS))
						.toVertx(MessageBusAddress.QUALITY_CHECK_BUS.getAddress()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.RETRY_BUS))
						.toVertx(MessageBusAddress.RETRY_BUS.getAddress()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.OSI_BUS_IN))
						.toVertx(MessageBusAddress.OSI_BUS_IN.getAddress()))
				.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.UIN_GENERATION_BUS_IN))
						.toVertx(MessageBusAddress.UIN_GENERATION_BUS_IN.getAddress()))

				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.BATCH_BUS.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_IN)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.STRUCTURE_BUS_OUT.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.OSI_BUS_IN)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.RETRY_BUS.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.RETRY_BUS)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.OSI_BUS_OUT.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.QUALITY_CHECK_BUS)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.MANUAL_VERIFICATION_BUS.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.UIN_GENERATION_BUS_IN)));

		return options;
	}

}