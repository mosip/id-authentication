package io.mosip.registrationProcessor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketFlowStatus {
	List<String> status_code = new ArrayList<String>();


	public void packetFlowStatusSetUp() {
		for (int i = 0; i < 9; i++) {
			status_code.add("ERROR"); 
		}
	}
	public List<String> setCharacter(List<Integer> stageBits) {
		int i=0;
		for (Integer integer : stageBits) {
			if(integer==1) {
				status_code.set(i, "SUCCESS");
				i++;
			}
		}
		if((stageBits.get(stageBits.size()-1))==1) {
			status_code.set(stageBits.size()-1, "PROCESSED");
		}
		return status_code;
	}
	public List<Integer> getList(String stageBits){
		packetFlowStatusSetUp();
		List<Integer> listOfIntegers = new ArrayList<>();
		Arrays.asList(stageBits.split("")).stream().forEach(e->listOfIntegers.add(Integer.parseInt(e)));
		return listOfIntegers;
	}


}
