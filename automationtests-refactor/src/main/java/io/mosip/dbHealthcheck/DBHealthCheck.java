package io.mosip.dbHealthcheck;

import io.mosip.dbaccess.*;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


@Test
public class DBHealthCheck {
	
	private static Logger logger = Logger.getLogger(DBHealthCheck.class);
	
	/*
	 * Check connectivity of Pre-Reg DB
	 * 
	 */
	public void kernel_CheckDBConnectivity()
	{
	
	
		boolean dbpreregConnectivity=PreRegDbread.prereg_dbconnectivityCheck();
		Assert.assertEquals(dbpreregConnectivity, true);
		logger.info("Pre-Reg DB is Connected Successfully");
		
	
	}
	
	/*
	 * Check connectivity of kernelMasterData DB
	 */
	public void kernelMasterData_CheckDBConnectivity()
	{
	
	
		boolean dbMasterDataConnectivity=KernelMasterDataR.kernelMasterData_dbconnectivityCheck();
		Assert.assertEquals(dbMasterDataConnectivity, true);
		logger.info("kernelMasterData DB is Connected Successfully");
		
	
	}
	
	
	/*
	 * Check connectivity of PreReg DB
	 */
	public void preReg_CheckDBConnectivity()
	{
	
	
		boolean dbpreregConnectivity=PreRegDbread.prereg_dbconnectivityCheck();
		Assert.assertEquals(dbpreregConnectivity, true);
		logger.info("preReg DB is Connected Successfully");
		
	
	}
	
	
	public static boolean prereg_preIDCheckDB(String preId)
	{
		boolean dbpreregPresence=PreRegDbread.prereg_dbDataPersistenceCheck(preId);
		try {
			if(dbpreregPresence)
			{
				Assert.assertTrue(dbpreregPresence);
			logger.info("Present in DB: " + dbpreregPresence );

			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

				logger.info("Pre-Reg Id is not Present in DB");
		}
		return dbpreregPresence;
	}
	

}
