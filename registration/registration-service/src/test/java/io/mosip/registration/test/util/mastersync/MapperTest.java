package io.mosip.registration.test.util.mastersync;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import io.mosip.registration.dto.mastersync.LanguageDto;
import io.mosip.registration.dto.mastersync.TitleDto;
import io.mosip.registration.entity.mastersync.MasterLanguage;
import io.mosip.registration.entity.mastersync.MasterRegistrationCenter;
import io.mosip.registration.entity.mastersync.MasterTitle;
import io.mosip.registration.util.mastersync.EmptyCheckUtils;
import io.mosip.registration.util.mastersync.MapperUtils;

import static io.mosip.registration.util.mastersync.MapperUtils.map;
import static io.mosip.registration.util.mastersync.MetaDataUtils.setCreateMetaData;
import static io.mosip.registration.util.mastersync.MetaDataUtils.setUpdateMetaData;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class MapperTest {
	
	private static List<TitleDto> rcdDtos = null;
	private static TitleDto rcdDto = null;

	@Before
	public void setup() {
		rcdDtos = new ArrayList<>();
		rcdDto = new TitleDto();
		rcdDto.setTitleName("Admin");
		rcdDto.setLangCode("ENG");
		rcdDto.setTitleCode("T1001");
		rcdDto.setIsActive(true);
		rcdDtos.add(rcdDto);

	}

	@Test(expected = NullPointerException.class)
	public void testMapSourceNull() {
		map(null, new MasterLanguage());
	}

	@Test(expected = NullPointerException.class)
	public void testMapMetaDataSourceNull() {
		setCreateMetaData(null, MasterLanguage.class);
	}

	@Test(expected = NullPointerException.class)
	public void testMapMetaDataDestinationNull() {
		setCreateMetaData(new LanguageDto(), null);
	}

	@Test
	public void testMapWithDestinationObject() {
		MasterTitle rcd = null;

		rcd = setCreateMetaData(rcdDto, MasterTitle.class);
		assertTrue(rcd != null);

		TitleDto newRcdDto = map(rcd, TitleDto.class);

		assertTrue(newRcdDto != null);

	}

	@Test
	public void testSetUpdateMetaData() {
		LanguageDto dto = new LanguageDto();
		MasterLanguage entity = new MasterLanguage();

		dto.setCode("ENG");
		dto.setFamily("English");

		entity.setCode("eng");
		entity.setFamily("english");
		entity.setName("english");
		entity.setNativeName("english");
		entity.setIsActive(true);
		entity.setCreatedDateTime(LocalDateTime.now());
		entity.setCreatedBy("admin");
		entity.setUpdatedBy("admin");

		setUpdateMetaData(dto, entity, false);

		assertTrue(entity.getCode().equals(dto.getCode()));
		assertTrue(entity.getFamily().equals(dto.getFamily()));
		assertTrue(entity.getName().equals("english"));
		assertTrue(entity.getNativeName().equals("english"));
		//assertTrue(entity.getUpdatedBy() != null);
		assertTrue(entity.getUpdatedDateTime() != null);
	}

	@Test
	public void testSetCreateMetaDataList() {
		List<MasterRegistrationCenter> rcds = setCreateMetaData(rcdDtos, MasterRegistrationCenter.class);
		assertTrue(!EmptyCheckUtils.isNullEmpty(rcds));
	}
	
}
