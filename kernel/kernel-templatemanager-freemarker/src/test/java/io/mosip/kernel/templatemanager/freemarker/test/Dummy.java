package io.mosip.kernel.templatemanager.freemarker.test;

public class Dummy {
	public String getName() {
		int i=1;
		if (i==1) {
			throw new IllegalArgumentException();
		}
		return "Abhishek";
	}
}
