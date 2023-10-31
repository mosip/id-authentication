-- -------------------------------------------------------------------------------------------------
-- Database Name	: mosip_ida
-- Release Version 	: 1.2.1
-- Purpose    		: Database Alter scripts for the release for ID Authentication DB.       
-- Create By   		: Mahammed Taheer
-- Created Date		: Aug-2023
-- 
-- Modified Date        Modified By         Comments / Remarks
-- -------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
\c mosip_ida sysadmin

CREATE TABLE ida.cred_subject_id_store(
    id character varying(36) NOT NULL,
    id_vid_hash character varying(128) NOT NULL,
    token_id character varying(128) NOT NULL,
    cred_subject_id character varying(2000) NOT NULL,
    csid_key_hash character varying(128) NOT NULL,
    oidc_client_id character varying(128),
    csid_status character varying(36),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    CONSTRAINT key_hash_unique UNIQUE (id_vid_hash, csid_key_hash)
);
COMMENT ON TABLE ida.cred_subject_id_store IS 'Credential Subject Id Store: To store and maintain the input credential subject ids to identify the individual.';
COMMENT ON COLUMN ida.cred_subject_id_store.id IS 'ID: Id is a unique identifier (UUID) used to map uniqueness to the credential subject id.';
COMMENT ON COLUMN ida.cred_subject_id_store.id_vid_hash IS 'IdVidHash: SHA 256 Hash value of the Id/VID.';
COMMENT ON COLUMN ida.cred_subject_id_store.token_id IS 'Token ID: Token ID generated in reference to UIN/VID';
COMMENT ON COLUMN ida.cred_subject_id_store.cred_subject_id IS 'Credential Subject ID : DID format holder id.';
COMMENT ON COLUMN ida.cred_subject_id_store.csid_key_hash IS 'Credential Subject ID Public Key Hash: Derived hash value of the public key.';
COMMENT ON COLUMN ida.cred_subject_id_store.oidc_client_id IS 'OIDC Client ID: An Id assigned to specific OIDC Client.';
COMMENT ON COLUMN ida.cred_subject_id_store.csid_status IS 'Credential Subject Id Status: To identify the current status of the credential subject id.';
COMMENT ON COLUMN ida.cred_subject_id_store.cr_by IS 'Created By : ID or name of the user who create / insert record';
COMMENT ON COLUMN ida.cred_subject_id_store.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
COMMENT ON COLUMN ida.cred_subject_id_store.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
COMMENT ON COLUMN ida.cred_subject_id_store.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
COMMENT ON COLUMN ida.cred_subject_id_store.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
COMMENT ON COLUMN ida.cred_subject_id_store.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';

CREATE INDEX ind_csid_key_hash ON ida.cred_subject_id_store (csid_key_hash);

INSERT INTO ida.key_policy_def (app_id, key_validity_duration, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes, pre_expire_days, access_allowed) 
VALUES('IDA_VCI_EXCHANGE', 1095, true, 'mosipadmin', now(), NULL, NULL, false, NULL, 60, 'NA');