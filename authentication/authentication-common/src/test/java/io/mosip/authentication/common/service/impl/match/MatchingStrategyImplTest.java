package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
public class MatchingStrategyImplTest {

	@Test
	public void test1() {
		MatchingStrategy ms = new MatchingStrategy() {

			@Override
			public MatchingStrategy getMatchingStrategy() {
				return new MatchingStrategyImpl(MatchingStrategyType.PARTIAL, null);
			}
		};

		assertNull(ms.getMatchFunction());
		assertEquals(MatchingStrategyType.PARTIAL, ms.getType());
	}

	@Test
	public void test2() {
		MatchingStrategy ms = new MatchingStrategy() {
		};

		assertNull(ms.getMatchingStrategy());
	}

}
