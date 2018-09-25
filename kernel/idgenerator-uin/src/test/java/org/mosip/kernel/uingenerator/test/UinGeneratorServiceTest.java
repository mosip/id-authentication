package org.mosip.kernel.uingenerator.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mosip.kernel.uingenerator.dto.UinResponseDto;
import org.mosip.kernel.uingenerator.exception.UinGenerationJobException;
import org.mosip.kernel.uingenerator.exception.UinNotFoundException;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.mosip.kernel.uingenerator.repository.UinDao;
import org.mosip.kernel.uingenerator.service.impl.UinGeneratorServiceImpl;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
public class UinGeneratorServiceTest {
	@Mock
	private UinDao uinDao;

	@InjectMocks
	private UinGeneratorServiceImpl uinGeneratorServiceImpl;

	@Test
	public void auditServiceTest() {

		UinBean uinBean = new UinBean();
		uinBean.setUin("1029384756");
		uinBean.setUsed(false);
		UinResponseDto uinResponseDto = new UinResponseDto();
		uinResponseDto.setUin(uinBean.getUin());

		when(uinDao.findUnusedUin()).thenReturn(uinBean);
		when(uinDao.save(uinBean)).thenReturn(uinBean);

		assertThat(uinGeneratorServiceImpl.getUin(), is(uinResponseDto));
	}

	@Test(expected = UinNotFoundException.class)
	public void auditServiceExceptionTest() {

		UinBean uinBean = null;

		when(uinDao.findUnusedUin()).thenReturn(uinBean);
		uinGeneratorServiceImpl.getUin();

	}

	@Test(expected = UinGenerationJobException.class)
	public void auditServiceBatchExceptionTest() {

		when(uinDao.findUnusedUin()).thenThrow(new UinGenerationJobException("code", "message"));
		uinGeneratorServiceImpl.getUin();

	}

}
