package io.mosip.registration.code;


public enum FormType {

	FAMILY("family"),INDIVIDUAL("individual"),FRIENDS("friends");
	
	 private String formtype;
	 
	 FormType(String formtype) {
        this.formtype = formtype;
    }

	public String getFormtype() {
		return formtype;
	} 
 
}
