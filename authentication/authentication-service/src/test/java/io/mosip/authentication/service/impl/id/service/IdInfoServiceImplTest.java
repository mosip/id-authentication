package io.mosip.authentication.service.impl.id.service;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.service.impl.id.service.impl.IdRepoServiceImpl;

//@RunWith(SpringRunner.class)

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:sample-output-test.properties")
public class IdInfoServiceImplTest {

	IdRepoServiceImpl IdInfoService = new IdRepoServiceImpl();

	@Value("${sample.demo.entity}")
	String value;

	@Before
	public void before() {
		ReflectionTestUtils.setField(IdInfoService, "value", value);
	}

	@Test
	public void getMapvalue() throws IdAuthenticationDaoException {
		Map valuemap = IdInfoService.getIdInfo("12232323");
	}
}
