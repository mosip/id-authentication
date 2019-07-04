package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;

@RunWith(SpringRunner.class)
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
								DemoMatchType.PINCODE),
						AuthTypeDTO::isDemo, "Address");
			}

			@Override
			public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
				return true;
			}
		};

		assertEquals("Address", authType.getDisplayName());
		assertEquals("address", authType.getType());
		assertNotNull(authType.getAssociatedMatchTypes());
		assertNotNull(authType.getAuthTypePredicate());
		assertTrue(authType.isAuthTypeEnabled(new AuthRequestDTO(), null));
		assertEquals("E", authType.getMatchingStrategy(new AuthRequestDTO(), null).get());
		assertFalse(authType.isAssociatedMatchType(DemoMatchType.ADDR));
		assertFalse(authType.isAuthTypeInfoAvailable(new AuthRequestDTO()));
	}

}
