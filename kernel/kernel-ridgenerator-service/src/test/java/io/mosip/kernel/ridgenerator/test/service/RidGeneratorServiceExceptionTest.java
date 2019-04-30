package io.mosip.kernel.ridgenerator.test.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.ridgenerator.dto.RidGeneratorResponseDto;
import io.mosip.kernel.ridgenerator.entity.Rid;
import io.mosip.kernel.ridgenerator.exception.InputLengthException;
import io.mosip.kernel.ridgenerator.repository.RidRepository;
import io.mosip.kernel.ridgenerator.service.RidGeneratorService;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RidGeneratorServiceExceptionTest {

	@Autowired
	private RidGeneratorService<RidGeneratorResponseDto> service;

	@MockBean
	private RidRepository repository;

	Rid entity = null;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(service, "centerIdLength", -1);
		entity = new Rid();
		entity.setCenterId("23123");
		entity.setMachineId("67687");
		entity.setCurrentSequenceNo(99999);
		entity.setCreatedBy("SYSTEM");
		entity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
	}

	@Test(expected = InputLengthException.class)
	public void generateRidInvalidCenterIdLengthExceptionTest() {
		service.generateRid("23123", "67687");
	}
}
