package org.mosip.kernel.uingenerator.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mosip.kernel.uingenerator.dto.UinResponseDto;
import org.mosip.kernel.uingenerator.entity.UinEntity;
import org.mosip.kernel.uingenerator.exception.UinGenerationJobException;
import org.mosip.kernel.uingenerator.exception.UinNotFoundException;
import org.mosip.kernel.uingenerator.repository.UinRepository;
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
	private UinRepository uinRepository;

	@InjectMocks
	private UinGeneratorServiceImpl uinGeneratorServiceImpl;

	@Test
	public void auditServiceTest() {

		UinEntity uinBean = new UinEntity();
		uinBean.setUin("1029384756");
		uinBean.setUsed(false);
		UinResponseDto uinResponseDto = new UinResponseDto();
		uinResponseDto.setUin(uinBean.getUin());

		when(uinRepository.findUnusedUin()).thenReturn(uinBean);
		when(uinRepository.save(uinBean)).thenReturn(uinBean);

		assertThat(uinGeneratorServiceImpl.getUin(), is(uinResponseDto));
	}

	@Test(expected = UinNotFoundException.class)
	public void auditServiceExceptionTest() {

		UinEntity uinBean = null;

		when(uinRepository.findUnusedUin()).thenReturn(uinBean);
		uinGeneratorServiceImpl.getUin();

	}

	@Test(expected = UinGenerationJobException.class)
	public void auditServiceBatchExceptionTest() {

		when(uinRepository.findUnusedUin()).thenThrow(new UinGenerationJobException("code", "message"));
		uinGeneratorServiceImpl.getUin();

	}

}
