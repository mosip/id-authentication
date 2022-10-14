CREATE TABLE ida.policy_data (
	policy_id character varying(36) NOT NULL,
	policy_data bytea NOT NULL,
	policy_name character varying(128) NOT NULL,
	policy_status character varying(36) NOT NULL,
	policy_description character varying(256),
	policy_commence_on timestamp NOT NULL,
	policy_expires_on timestamp,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT policy_data_pk PRIMARY KEY (policy_id)

);

CREATE INDEX ind_pl_pid ON ida.policy_data (policy_id);
