package io.mosip.registration.processor.packet.manager.dto;

// TODO: Auto-generated Javadoc
/**
 * The directory names for FileManager operation.
 * 
 * @author M1039303
 *
 */
public enum DirectoryPathDto {

	/** The virus scan. */
	VIRUS_SCAN {
		@Override
		public String toString() {
			return "registration.processor.VIRUS_SCAN";
		}
	},

	/** The virus scan enc. */
	VIRUS_SCAN_ENC {
		@Override
		public String toString() {
			return "registration.processor.VIRUS_SCAN_ENC";
		}
	},

	/** The virus scan dec. */
	VIRUS_SCAN_DEC {
		@Override
		public String toString() {
			return "registration.processor.VIRUS_SCAN_DEC";
		}
	},
	/** The virus scan retry. */
	VIRUS_SCAN_RETRY {
		@Override
		public String toString() {
			return "registration.processor.VIRUS_SCAN_RETRY";
		}
	},

	/** The archive location. */
	ARCHIVE_LOCATION {
		@Override
		public String toString() {
			return "registration.processor.ARCHIVE_LOCATION";
		}
	},

	/** The virus scan unpack. */
	VIRUS_SCAN_UNPACK {
		@Override
		public String toString() {
			return "registration.processor.VIRUS_SCAN_UNPACK";
		}
	},

	/** The packet generated decrypted. */
	PACKET_GENERATED_DECRYPTED {
		@Override
		public String toString() {
			return "registration.processor.packet.storageLocation.decrypted";
		}
	},

	/** The packet generated encrypted. */
	PACKET_GENERATED_ENCRYPTED {
		@Override
		public String toString() {
			return "registration.processor.packet.storageLocation.encrypted";
		}
	},

	/** The landing zone. */
	LANDING_ZONE {
		@Override
		public String toString() {
			return "registration.processor.LANDLING_ZONE";
		}
	},

}
