package io.mosip.registration.processor.camel.bridge;

import static org.junit.Assert.assertEquals;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.mosip.registration.processor.camel.bridge.processor.ValidationProcessor;
import io.mosip.registration.processor.camel.bridge.statuscode.MessageEnum;
import io.mosip.registration.processor.camel.bridge.util.BridgeUtil;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.vertx.camel.CamelBridgeOptions;
import io.vertx.camel.InboundMapping;
import io.vertx.camel.OutboundMapping;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MosipBridgeMappingTest {

	MosipBridgeMapping mosipBridgeMapping = new MosipBridgeMapping();
	private RouteBuilder RouteBuilder;
	private CamelContext camelContext = new DefaultCamelContext();

	private CamelBridgeOptions options;
	private static Processor validateStructure = new ValidationProcessor();

	@Before
	public void setup() throws Exception {

		RouteBuilder = new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				// Decryption to Structure Validation routing
				from(BridgeUtil.getEndpoint(MessageBusAddress.BATCH_BUS)).choice()
						.when(header(MessageEnum.IS_VALID.getParameter()).isEqualTo(true))
						.to(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_IN));

				// Structure Validation to Demographic Validation routing
				from(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_OUT)).process(validateStructure).choice()
						.when(header(MessageEnum.INTERNAL_ERROR.getParameter()).isEqualTo(true))
						.to(BridgeUtil.getEndpoint(MessageBusAddress.RETRY_BUS))
						.when(header(MessageEnum.IS_VALID.getParameter()).isEqualTo(true))
						.to(BridgeUtil.getEndpoint(MessageBusAddress.QUALITY_CHECK_BUS))
						.when(header(MessageEnum.IS_VALID.getParameter()).isEqualTo(false))
						.to(BridgeUtil.getEndpoint(MessageBusAddress.ERROR));

			}

		};
		camelContext.addRoutes(RouteBuilder);
		camelContext.start();

		options = new CamelBridgeOptions(camelContext);

		options.addInboundMapping(InboundMapping.fromCamel(BridgeUtil.getEndpoint(MessageBusAddress.ERROR))
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

				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.BATCH_BUS.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_IN)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.STRUCTURE_BUS_OUT.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_OUT)))
				.addOutboundMapping(OutboundMapping.fromVertx(MessageBusAddress.RETRY_BUS.getAddress())
						.toCamel(BridgeUtil.getEndpoint(MessageBusAddress.RETRY_BUS)));
	}

	@Test
	public void BridgeOutboundMappingTest() {

		assertEquals(options.getOutboundMappings().get(0).getAddress(),
				mosipBridgeMapping.getMapping(camelContext).getOutboundMappings().get(0).getAddress());
		assertEquals(options.getOutboundMappings().get(1).getAddress(),
				mosipBridgeMapping.getMapping(camelContext).getOutboundMappings().get(1).getAddress());
		assertEquals(options.getOutboundMappings().get(2).getAddress(),
				mosipBridgeMapping.getMapping(camelContext).getOutboundMappings().get(2).getAddress());

	}

	@Test
	public void BridgeInboundMappingTest() {
		assertEquals(options.getInboundMappings().get(0).getAddress(),
				mosipBridgeMapping.getMapping(camelContext).getInboundMappings().get(0).getAddress());
		assertEquals(options.getInboundMappings().get(1).getAddress(),
				mosipBridgeMapping.getMapping(camelContext).getInboundMappings().get(1).getAddress());
		assertEquals(options.getInboundMappings().get(2).getAddress(),
				mosipBridgeMapping.getMapping(camelContext).getInboundMappings().get(2).getAddress());
		assertEquals(options.getInboundMappings().get(3).getAddress(),
				mosipBridgeMapping.getMapping(camelContext).getInboundMappings().get(3).getAddress());
		assertEquals(options.getInboundMappings().get(4).getAddress(),
				mosipBridgeMapping.getMapping(camelContext).getInboundMappings().get(4).getAddress());

	}
}
