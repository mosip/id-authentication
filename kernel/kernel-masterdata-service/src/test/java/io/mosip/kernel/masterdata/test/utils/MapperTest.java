package io.mosip.kernel.masterdata.test.utils;

import static io.mosip.kernel.masterdata.utils.MapperUtils.map;
import static io.mosip.kernel.masterdata.utils.MetaDataUtils.setCreateMetaData;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.utils.EmptyCheckUtils;

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

	@Test(expected = NullPointerException.class)
	public void testMapMetaDataSourceNull() {
		setCreateMetaData(null, Language.class);
	}

	@Test(expected = NullPointerException.class)
	public void testMapMetaDataDestinationNull() {
		setCreateMetaData(new LanguageDto(), null);
	}

	@Test
	public void testMapWithDestinationObject() {
		RegistrationCenterDevice rcd = null;

		rcd = setCreateMetaData(rcdDto, RegistrationCenterDevice.class);
		assertTrue(rcd != null);

		RegistrationCenterDeviceDto newRcdDto = map(rcd, RegistrationCenterDeviceDto.class);

		assertTrue(newRcdDto != null);

	}

	@Test
	public void testSetCreateMetaDataList() {
		List<RegistrationCenterDevice> rcds = setCreateMetaData(rcdDtos, RegistrationCenterDevice.class);
		assertTrue(!EmptyCheckUtils.isNullEmpty(rcds));
	}

}
