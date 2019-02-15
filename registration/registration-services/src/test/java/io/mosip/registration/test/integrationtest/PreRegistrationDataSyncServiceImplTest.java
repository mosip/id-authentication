package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.repositories.PreRegistrationDataSyncRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class PreRegistrationDataSyncServiceImplTest {
	@Autowired
	GlobalParamService globalParamService;
	@Autowired
	PreRegistrationDataSyncRepository preRegistrationDataSyncRepository;
	@Before
	public void setup() {
		ApplicationContext context=ApplicationContext.getInstance();
		context.setApplicationLanguageBundle();
	
		
		context.setApplicationMessagesBundle();
		context.setLocalLanguageProperty();
		context.setLocalMessagesBundle();
		Map<String,Object> map=globalParamService.getGlobalParams();
		map.put(RegistrationConstants.PRE_REG_DELETION_CONFIGURED_DAYS,"5");
		context.setApplicationMap(map);

	}
	
	@Autowired
	private PreRegistrationDataSyncService preRegistrationDataSyncService;
	
	/**
	 * Test Case when preRegPacket is deleted successfully
	 * @throws IOException 
	 */
	@Test
	public void fetchAndDeleteRecordsTest() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("Test String");
		String packetPath=System.getProperty("user.dir")+"\\samplePacket.zip";
		File samplePacket=new File(packetPath);
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(samplePacket));
		ZipEntry e = new ZipEntry("mytext.txt");
		out.putNextEntry(e);

		byte[] data = sb.toString().getBytes();
		out.write(data, 0, data.length);
		out.closeEntry();

		out.close();
		List<PreRegistrationList> list=preRegistrationDataSyncRepository.findAll();
		PreRegistrationList preRegEntity=list.get(0);
		preRegEntity.setId("101");
		preRegEntity.setIsDeleted(false);
		preRegEntity.setPacketPath(packetPath);
		System.out.println(preRegEntity.getIsDeleted());	
		Calendar c=Calendar.getInstance();
		c.add(Calendar.DATE, -8);
		preRegEntity.setAppointmentDate(Date.from(c.toInstant()));
		preRegistrationDataSyncRepository.save(preRegEntity);
		assertEquals(preRegistrationDataSyncService.fetchAndDeleteRecords().getSuccessResponseDTO().getMessage(),"Pre-Registration Records deleted");
	}
	
	/**
	 * Inavlid Registration_Center_Id Test Case
	 * 
	 */
	@Test 
	public void getPreRegistrationIds_InvalidRegistrationCenterId() {
		System.setProperty("http.proxyHost", "172.22.218.218");
		System.setProperty("http.proxyPort", "8085");
		assertEquals(preRegistrationDataSyncService.getPreRegistrationIds("User").getErrorResponseDTOs().get(0).getMessage(),"Unable to get Pre registartion id's");
		
	}
	
	
	
}
