package io.mosip.kernel.jsonvalidator.constant;

	/**
	 * This enum provides all the constants for property source to be used.
	 * 
	 * @author Swati Raj
	 *
	 */
	public enum JsonValidatorPropertySourceConstant {
		CONFIG_SERVER("CONFIG_SERVER"), 
		LOCAL("LOCAL");

		/**
		 * The property Source.
		 */
		private final String propertySource;

		/**
		 * Setter for propertySource.
		 * 
		 * @param propertySource
		 *            The property source to be set
		 */
		private JsonValidatorPropertySourceConstant(String propertySource) {
			this.propertySource = propertySource;
		}

		/**
		 * Getter for propertySource.
		 * 
		 * @return The propertySource.
		 */
		public String getPropertySource() {
			return propertySource;
		}
	}
