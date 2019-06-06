package io.mosip.registration.test.util.mastersync;

import static io.mosip.registration.util.mastersync.MapperUtils.map;
import static io.mosip.registration.util.mastersync.MetaDataUtils.setCreateMetaData;
import static io.mosip.registration.util.mastersync.MetaDataUtils.setUpdateMetaData;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dto.mastersync.LanguageDto;
import io.mosip.registration.dto.mastersync.TitleDto;
import io.mosip.registration.entity.Language;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.Title;
import io.mosip.registration.util.mastersync.EmptyCheckUtils;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SessionContext.class })
public class MapperTest {
	
	private static List<TitleDto> rcdDtos = null;
	private static TitleDto rcdDto = null;

	@Before
	public void setup() throws Exception{
		rcdDtos = new ArrayList<>();
		rcdDto = new TitleDto();
		rcdDto.setTitleName("Admin");
		rcdDto.setLangCode("ENG");
		rcdDto.setCode("T1001");
		rcdDto.setIsActive(true);
		rcdDtos.add(rcdDto);
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getUserId()).thenReturn("mosip");

	}

	@Test(expected = NullPointerException.class)
	public void testMapSourceNull() {
		map(null, new Language());
	}


	@Test(expected = NullPointerException.class)
	public void testMapMetaDataDestinationNull() {
		setCreateMetaData(new LanguageDto(), null);
	}

	@Test
	public void testMapWithDestinationObject() {
		Title rcd = null;

		rcd = setCreateMetaData(rcdDto, Title.class);
		assertTrue(rcd != null);

		TitleDto newRcdDto = map(rcd, TitleDto.class);

		assertTrue(newRcdDto != null);

	}

	@Test
	public void testSetUpdateMetaData() {
		LanguageDto dto = new LanguageDto();
		Language entity = new Language();

		dto.setCode("ENG");
		dto.setFamily("English");

		entity.setCode("eng");
		entity.setFamily("english");
		entity.setName("english");
		entity.setNativeName("english");
		entity.setIsActive(true);
		entity.setCrDtime(Timestamp.valueOf(LocalDateTime.now()));
		entity.setCrBy("admin");
		entity.setUpdBy("admin");

		setUpdateMetaData(dto, entity, false);

		assertTrue(entity.getCode().equals(dto.getCode()));
		assertTrue(entity.getFamily().equals(dto.getFamily()));
		assertTrue(entity.getName().equals("english"));
		assertTrue(entity.getNativeName().equals("english"));
		//assertTrue(entity.getUpdatedBy() != null);
		assertTrue(entity.getUpdDtimes() != null);
	}

	@Test
	public void testSetCreateMetaDataList() {
		List<RegistrationCenter> rcds = setCreateMetaData(rcdDtos, Title.class);
		assertTrue(!EmptyCheckUtils.isNullEmpty(rcds));
	}
	
}
