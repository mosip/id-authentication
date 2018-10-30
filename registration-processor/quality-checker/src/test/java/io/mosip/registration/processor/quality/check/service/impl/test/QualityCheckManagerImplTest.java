package io.mosip.registration.processor.quality.check.service.impl.test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.quality.check.service.impl.QualityCheckManagerImpl;

@RunWith(SpringRunner.class)
public class QualityCheckManagerImplTest {
	@InjectMocks
	QualityCheckManager<String, ApplicantInfoDto, QCUserDto>  qualityCheckManager= new QualityCheckManagerImpl();
	
	@Mock
	private ApplicantInfoDao applicantInfoDao;

	@Mock
	private AuditRequestBuilder auditRequestBuilder;

	@Mock
	private AuditHandler<AuditRequestDto> auditHandler;
}
