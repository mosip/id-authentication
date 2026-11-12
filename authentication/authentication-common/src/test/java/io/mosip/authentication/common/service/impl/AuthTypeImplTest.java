package io.mosip.authentication.common.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
public class AuthTypeImplTest {

	@Test
	public void test1() {
		AuthType authType = new AuthType() {

			@Override
			public AuthType getAuthTypeImpl() {
				return new AuthTypeImpl("address",
						AuthType.setOf(DemoMatchType.ADDR_LINE1, DemoMatchType.ADDR_LINE2, DemoMatchType.ADDR_LINE3,
								DemoMatchType.LOCATION1, DemoMatchType.LOCATION2, DemoMatchType.LOCATION3,
								DemoMatchType.PINCODE), "Address");
			}

			@Override
			public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
				return true;
			}

			@Override
			public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
				return false;
			}
		};

		assertEquals("Address", authType.getDisplayName());
		assertEquals("address", authType.getType());
		assertNotNull(authType.getAssociatedMatchTypes());
		assertTrue(authType.isAuthTypeEnabled(new AuthRequestDTO(), null));
		assertEquals("E", authType.getMatchingStrategy(new AuthRequestDTO(), null).get());
		assertFalse(authType.isAssociatedMatchType(DemoMatchType.ADDR));
		assertFalse(authType.isAuthTypeInfoAvailable(new AuthRequestDTO()));
	}

}
