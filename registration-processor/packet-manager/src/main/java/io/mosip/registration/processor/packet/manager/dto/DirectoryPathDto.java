package io.mosip.registration.processor.packet.manager.dto;

/**
 * The directory names for FileManager operation.
 * 
 * @author M1039303
 *
 */
public enum DirectoryPathDto {

	LANDING_ZONE{
		@Override
		public String toString() {
		return "registration.processor.LANDING_ZONE";
	}},
	VIRUS_SCAN{
		@Override
	public String toString() {
		return "registration.processor.VIRUS_SCAN";
	}},
	VIRUS_SCAN_RETRY{
		@Override
		public String toString() {
			return "registration.processor.VIRUS_SCAN_RETRY";
			}},
	FTP_ZONE{
		@Override
		public String toString() {
			return "registration.processor.FTP_ZONE";
		}}, 
	ARCHIVE_LOCATION{
		@Override
		public String toString() {
			return "registration.processor.ARCHIVE_LOCATION";
		}}

}
