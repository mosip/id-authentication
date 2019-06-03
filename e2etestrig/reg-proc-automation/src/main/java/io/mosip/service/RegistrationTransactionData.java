package io.mosip.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistrationTransactionData {
	List<String> status_code = new ArrayList<String>();


public RegistrationTransactionData() {
	for (int i = 0; i < 8; i++) {
		status_code.add("FAILED");
	}
}
public List<String> setCharacter(List<Integer> stageBits) {
	for (Integer integer : stageBits) {
		if(integer==1) {
			status_code.add(stageBits.indexOf(integer), "SUCCESS");
		}
	}
	return status_code;
}
public List<Integer> getList(String stageBits){
	List<Integer> listOfIntegers = new ArrayList<>();
	Arrays.asList(stageBits.split("")).stream().forEach(e->listOfIntegers.add(Integer.parseInt(e)));
	return listOfIntegers;
}


}
