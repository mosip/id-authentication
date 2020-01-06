package io.mosip.kernel.syncdata.test.utils;

import static io.mosip.kernel.syncdata.utils.MapperUtils.map;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.syncdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.syncdata.entity.Language;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class MapperTest {
	private static List<RegistrationCenterDeviceDto> rcdDtos = null;
	private static RegistrationCenterDeviceDto rcdDto = null;

	@Before
	public void setup() {
		rcdDtos = new ArrayList<>();
		rcdDto = new RegistrationCenterDeviceDto();
		rcdDto.setRegCenterId("12");
		rcdDto.setDeviceId("4567");
		rcdDto.setIsActive(true);
		rcdDtos.add(rcdDto);

	}

	@Test(expected = NullPointerException.class)
	public void testMapSourceNull() {
		map(null, new Language());
	}

	@Test(expected = NullPointerException.class)
	public void testMapDestinationNull() {
		map(new Language(), null);
	}

}
