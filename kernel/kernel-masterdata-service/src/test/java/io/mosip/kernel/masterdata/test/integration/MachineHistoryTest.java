package io.mosip.kernel.masterdata.test.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.MachineHistoryRepository;
import io.mosip.kernel.masterdata.service.MachineHistoryService;
import io.mosip.kernel.masterdata.service.impl.MachineHistoryServiceImpl;
import io.mosip.kernel.masterdata.utils.MapperUtils;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(PowerMockRunner.class)
@PrepareForTest(MachineHistoryServiceImpl.class)
public class MachineHistoryTest {
	
	

	
	@MockBean
	MachineHistoryRepository machineHistoryRepository;
	
	
	
	@Test(expected = RequestException.class)
	public void addApplicationDataFetchException() {
		PowerMockito.mockStatic(MapperUtils.class);
				when(MapperUtils.parseToLocalDateTime(Mockito.any())).thenThrow(RequestException.class);
		MachineHistoryService machineHistoryService;
		machineHistoryService.getMachineHistroyIdLangEffDTime("1000", "ENG", "2018-12-11T11:18:21.033Z");
	}

}
