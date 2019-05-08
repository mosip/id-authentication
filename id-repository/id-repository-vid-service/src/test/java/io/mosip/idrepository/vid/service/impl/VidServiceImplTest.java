package io.mosip.idrepository.vid.service.impl;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.vid.entity.Vid;
import io.mosip.idrepository.vid.repository.VidRepo;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * @author Prem Kumar
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
public class VidServiceImplTest {

	@InjectMocks
	private VidServiceImpl impl;

	@Mock
	private VidRepo vidRepo;

	@Autowired
	Environment environment;

	@Before
	public void before() {
		ReflectionTestUtils.setField(impl, "env", environment);
		ReflectionTestUtils.setField(impl, "vidRepo", vidRepo);
	}

	@Test
	public void testRetrieveUinByVid() throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVATED", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.retrieveVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
			impl.retrieveUinByVid("12345678");
	}
	
	@Test
	public void testRetrieveUinByVid_Expired() {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime();
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVATED", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.retrieveVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
			try {
				impl.retrieveUinByVid("12345678");
			} catch (IdRepoAppException e) {
				assertEquals("IDR-VID-002 --> Expired VID", e.getMessage());
			}
	}
	
	@Test
	public void testRetrieveUinByVid_Blocked() {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "Blocked", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.retrieveVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
			try {
				impl.retrieveUinByVid("12345678");
			} catch (IdRepoAppException e) {
				assertEquals("IDR-VID-002 --> Blocked VID", e.getMessage());
			}
	}
	
	@Test
	public void testRetrieveUinByVid_Invalid_NoRecordsFound() {
		Mockito.when(vidRepo.retrieveVid(Mockito.anyString())).thenReturn(null);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
			try {
				impl.retrieveUinByVid("12345678");
			} catch (IdRepoAppException e) {
				assertEquals("IDR-VID-006 --> No Record(s) found", e.getMessage());
			}
	}
}
