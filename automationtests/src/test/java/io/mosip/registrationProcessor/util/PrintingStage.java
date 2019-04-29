package io.mosip.registrationProcessor.util;

import org.apache.log4j.Logger;

import io.mosip.dbaccess.RegProcStageDb;

public class PrintingStage {
	private static Logger logger = Logger.getLogger(PrintingStage.class);

	
	public boolean validatePrintingStage(String rid){
		RegProcStageDb dbData= new RegProcStageDb();
		String uin = dbData.regproc_getUIN(rid);
		logger.info("uin : "+uin);
		return false;
		
	}
	
	public static void main(String args[]){
		PrintingStage ps = new PrintingStage();
		ps.validatePrintingStage("10002100320000720190422115140");
	}

}
