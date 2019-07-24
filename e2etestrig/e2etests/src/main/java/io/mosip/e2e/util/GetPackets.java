package io.mosip.e2e.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GetPackets {
public Map<String,File> getPacket() {
	String pattern = "dd-MMM-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	Map<String,File> mapOfPackets=new HashMap<String,File>();
	String date = simpleDateFormat.format(new Date());
	
	String matchDate=date.substring(date.indexOf("-")+1,date.length());
	String configPath=BaseUtil.getGlobalResourcePath()+"/src/main/resources/packets/UniqueCBEFF_Packets";
	File file =new File(configPath);
	File[] listOfFiles=file.listFiles();
	for(File f:listOfFiles) {
		
		File [] packets=f.listFiles();
		for(File packet:packets) {
			if(packet.getName().contains(matchDate)) {
				for(File uploadPackets:packet.listFiles()) {
					mapOfPackets.put(f.getName(), uploadPackets);
				}
			}
		}
	}
	return mapOfPackets;
}

}
