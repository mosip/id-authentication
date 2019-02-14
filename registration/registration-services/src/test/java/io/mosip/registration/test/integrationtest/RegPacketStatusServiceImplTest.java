package io.mosip.registration.test.integrationtest;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.impl.RegistrationDAOImpl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.impl.RegPacketStatusServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class RegPacketStatusServiceImplTest {
	@Autowired
	GlobalParamService globalParamService;

	@Autowired
	private RegPacketStatusServiceImpl regPacketStatusServiceImpl;
	@Autowired
	private RegistrationRepository regstrationRepository;
	
	@Before
	public void setup() {
		ApplicationContext context=ApplicationContext.getInstance();
		context.setApplicationLanguageBundle();
		context.setApplicationMessagesBundle();
		context.setLocalLanguageProperty();
		context.setLocalMessagesBundle();
		Map<String,Object> map=globalParamService.getGlobalParams();
		map.put(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS,"5");
		context.setApplicationMap(map);

	}
	
	@Test
	public void deleteRegistrationPacketsTest() {
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE, -8);
		List<Registration> list=regstrationRepository.findAll();
		Registration sampleReg=list.get(0);
		sampleReg.setId("101");
		sampleReg.setCrDtime(new Timestamp(cal.getTimeInMillis()));
		regstrationRepository.save(sampleReg);
		regPacketStatusServiceImpl.deleteRegistrationPackets();
	}
	
}
