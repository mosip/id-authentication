package io.mosip.dbdto;

public class DemoDedupeDto {
	
		/** The reg id. */
		private String regId;
		
		/** The uin. */
		private String uin;
		
		/** The lang code. */
		private String langCode;

		/** The name. */
		private String name;
		
		/** The dob. */
		private String dob;
		
		
		public String getDob() {
			return dob;
		}

		public void setDob(String dob) {
			this.dob = dob;
		}

		/** The gender code. */
		private String genderCode;

		public String getRegId() {
			return regId;
		}

		public void setRegId(String regId) {
			this.regId = regId;
		}

		public String getUin() {
			return uin;
		}

		public void setUin(String uin) {
			this.uin = uin;
		}

		public String getLangCode() {
			return langCode;
		}

		public void setLangCode(String langCode) {
			this.langCode = langCode;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getGenderCode() {
			return genderCode;
		}

		public void setGenderCode(String genderCode) {
			this.genderCode = genderCode;
		}

		@Override
		public String toString() {
			return "DemoDedupeDto [regId=" + regId + ", uin=" + uin + ", langCode=" + langCode + ", name=" + name
					+ ", dob=" + dob + ", genderCode=" + genderCode + "]";
		}
		
		

}
