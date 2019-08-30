package io.mosip.idrepository.saltgenerator.step.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.authentication.saltgenerator.entity.SaltEntity;
import io.mosip.authentication.saltgenerator.repository.SaltRepository;
import io.mosip.authentication.saltgenerator.step.SaltWriter;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;

@RunWith(MockitoJUnitRunner.class)
public class SaltWriterTest {

	@InjectMocks
	SaltWriter writer;

	@Mock
	SaltRepository repo;

	@Test
	public void testWriter() throws Exception {
		SaltEntity entity = new SaltEntity();
		entity.setId(0l);
		when(repo.countByIdIn(Mockito.any())).thenReturn(0l);
		writer.write(Collections.singletonList(entity));
	}

	@Test
	public void testWriterRecordExists() throws Exception {
		try {
			SaltEntity entity = new SaltEntity();
			entity.setId(0l);
			when(repo.countByIdIn(Mockito.any())).thenReturn(1l);
			writer.write(Collections.singletonList(entity));
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.RECORD_EXISTS.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.RECORD_EXISTS.getErrorMessage(), e.getErrorText());
		}
	}

}
