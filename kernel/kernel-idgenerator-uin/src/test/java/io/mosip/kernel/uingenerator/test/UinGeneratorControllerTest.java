package io.mosip.kernel.uingenerator.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.idgenerator.uin.controller.UinGeneratorController;
import io.mosip.kernel.idgenerator.uin.dto.UinResponseDto;
import io.mosip.kernel.idgenerator.uin.service.impl.UinGeneratorServiceImpl;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
public class UinGeneratorControllerTest {
	@Mock
	private UinGeneratorServiceImpl service;
	@InjectMocks
	private UinGeneratorController controller;

	@Test
	public void getUinTest() throws Exception {

		UinResponseDto uinResponseDto = new UinResponseDto();
		uinResponseDto.setUin("1029384756");
		when(service.getUin()).thenReturn(uinResponseDto);

		assertThat(controller.getUin(), is(new ResponseEntity<>(service.getUin(), HttpStatus.OK)));

	}

}
