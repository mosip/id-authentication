-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_master
-- Table Name 	: master.registered_device_master
-- Purpose    	: Registered Device Master : Contains list of registered devices and details, like fingerprint scanner, iris scanner, scanner etc used at registration centers, authentication services, eKYC...etc. Valid devices with active status only allowed at registering devices for respective functionalities. Device onboarding are handled through admin application/portal by the user who is having the device onboarding authority. Table is created temporarily and will be relooked in to this for re-design of the solution later
--           
-- Create By   	: Nasir Khan / Sadanandegowda
-- Created Date	: 30-Jul-2019
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- 
-- ------------------------------------------------------------------------------------------

-- object: master.registered_device_master | type: TABLE --
-- DROP TABLE IF EXISTS master.registered_device_master CASCADE;
CREATE TABLE master.registered_device_master(
	code character varying(36) NOT NULL,
	type character varying(64),
	subtype character varying(64),
	status_code character varying(36),
	device_id character varying(36) NOT NULL,
	device_sub_id character varying(1024),
	provider_id character varying(36) NOT NULL,
	provider_name character varying(128),
	mosip_process character varying(64) NOT NULL,
	firmware character varying(128),
	make character varying(36),
	model character varying(36),
	expiry_date timestamp,
	certification bytea,
	foundational_trust_provider_id character varying(36),
	foundational_trust_signature character varying(512),
	foundational_trust_certificate bytea,
	dpsignature character varying(512),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_regdevicem_code PRIMARY KEY (code),
	CONSTRAINT uk_devpro_id UNIQUE (device_id,provider_id)

);
-- ddl-end --
COMMENT ON TABLE master.registered_device_master IS 'Registered Device Master : Contains list of registered devices and details, like fingerprint scanner, iris scanner, scanner etc used at registration centers, authentication services, eKYC...etc. Valid devices with active status only allowed at registering devices for respective functionalities. Device onboarding are handled through admin application/portal by the user who is having the device onboarding authority. Table is created temporarily and will be relooked in to this for re-design of the solution later';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.code IS 'Device ID : Unique ID generated / assigned for device';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.type IS 'Type: Types of devices used for registration processes, authentication, eKYC..etc. for ex., printer, scanner, etc';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.subtype IS 'Sub Type: Sub types of devices used for registration processes, authentication, eKYC..etc. for ex., thumb finger print, palm print scanner, Iris scanner, etc';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.status_code IS ' Status Code : Status of the registered devices';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.device_id IS 'Device ID: Unique ID of the device by the provider, This ID can be serial number device';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.device_sub_id IS 'Device Sub ID: Sub ID of the devices, Each device will have an array of sub IDs';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.provider_id IS 'Provider ID: ID of the device provider';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.provider_name IS 'Provider Name: Name of the device provider';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.mosip_process IS 'MOSIP process where there devices will be used for. ex. Registrations, Authentication, eKYC...etc';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.firmware IS 'Firmware: Firmware used in devices';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.make IS 'Make: Make of the device';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.expiry_date IS 'Expiry Date: expiry date of the device';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.certification IS 'Certification: Device certification';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.foundational_trust_provider_id IS 'Foundational Trust Provider ID: Foundational trust provider ID';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.foundational_trust_signature IS 'Foundational Trust Signature: Signature of the foundational trust';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.foundational_trust_certificate IS 'Foundational Trust Certificate: Certificate of the foundational trust';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.dpsignature IS 'DP Signation: DP Signature';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.cr_by IS 'Created By : ID or name of the user who create / insert record';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN master.registered_device_master.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --

