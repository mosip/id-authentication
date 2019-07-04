package io.mosip.registration.test.config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.WeakHashMap;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import io.mosip.registration.config.PropertiesConfig;

public class PropertiesConfigTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	PropertiesConfig propertiesConfig;

	@Mock
	JdbcTemplate jdbcTemplate;

	@Test
	public void getDBPropsTest() {
		Map<String, Object> dbProps = new WeakHashMap<>();
		when(jdbcTemplate.query(Mockito.anyString(), Mockito.any(ResultSetExtractor.class))).thenReturn(dbProps);
		assertEquals(propertiesConfig.getDBProps(), dbProps);
		new PropertiesConfig();
	}
	
}
