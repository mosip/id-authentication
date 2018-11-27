package io.mosip.registration.processor.qc.users;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.processor.qc.users.dao.QCUserInfoDao;
import io.mosip.registration.processor.qc.users.entity.UserDetailEntity;
import io.mosip.registration.processor.qc.users.entity.UserDetailPKEntity;
import io.mosip.registration.processor.qc.users.repository.QcuserRegRepositary;

@RunWith(MockitoJUnitRunner.class)
public class QCUserInfoDaoTest {
	@InjectMocks
	private QCUserInfoDao qcUserInfoDao;
	
	@Mock
	private QcuserRegRepositary<UserDetailEntity, UserDetailPKEntity> qcuserRepositary;
	
	private UserDetailPKEntity pk1;
	private UserDetailPKEntity pk2;
	private List<String> qcNamelist;
	private List<UserDetailPKEntity> qclist;
	
	@Before
	public void setUp() {
		pk1=new UserDetailPKEntity();
		pk2=new UserDetailPKEntity();
		qclist= new ArrayList<UserDetailPKEntity>();
		pk1.setId("qc001");
		pk2.setId("qc002");
		qclist.add(pk1);
		qclist.add(pk2);
		qcNamelist=Arrays.asList("qc001","qc002");
	}
	@Test
	public void getAllQcuserIdsTest(){
		Mockito.when(qcuserRepositary.findAllUserIds()).thenReturn(qclist);
		
		assertTrue(qcNamelist.containsAll(qcUserInfoDao.getAllQcuserIds()));
	}
}
