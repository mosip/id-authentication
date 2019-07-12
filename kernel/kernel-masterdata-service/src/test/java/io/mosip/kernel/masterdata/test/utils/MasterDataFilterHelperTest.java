package io.mosip.kernel.masterdata.test.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MasterDataFilterHelperTest {

	@Autowired
	private MasterDataFilterHelper masterDataFilterHelper;
	
	@Test
	public void filterValuesTest() {
		List<?>list=masterDataFilterHelper.filterValues(Machine.class, "name", "all", "eng");
		assertThat(list.isEmpty(),is(true));
	}
}
