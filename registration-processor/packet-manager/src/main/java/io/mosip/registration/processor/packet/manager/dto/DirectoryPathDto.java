package io.mosip.registration.processor.packet.manager.dto;

/**
 * The directory names for FileManager operation.
 * 
 * @author M1039303
 *
 */	
public enum DirectoryPathDto {
    /** The landing zone. */
	LANDING_ZONE{
		@Override
		public String toString() {
		return "registration.processor.LANDING_ZONE";
	}},
    /** The virus scan. */
	VIRUS_SCAN{
		@Override
	public String toString() {
		return "registration.processor.VIRUS_SCAN";
	}},
    /** The virus scan retry. */
	VIRUS_SCAN_RETRY{
		@Override
		public String toString() {
			return "registration.processor.VIRUS_SCAN_RETRY";
			}},
    /** The ftp zone. */
	FTP_ZONE{
		@Override
		public String toString() {
			return "registration.processor.FTP_ZONE";
		}},
    /** The archive location. */
	ARCHIVE_LOCATION{
		@Override
		public String toString() {
			return "registration.processor.ARCHIVE_LOCATION";
		}}

}
