-- object: prereg.prid_seq | type: TABLE --
-- DROP TABLE IF EXISTS prereg.prid_seq CASCADE;
CREATE TABLE prereg.prid_seq(
	seq_no character varying(32) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_prseq PRIMARY KEY (seq_no)

);
-- ddl-end --
COMMENT ON TABLE prereg.prid_seq IS 'Pre-Registration ID Sequence: Stores sequence numbers that are used in the algorithm to generate PRID. Stores a incremental sequence number that will be used as salt in the algorithm to generate a PRID. This salt value is encrypted/hashed and used along with a seed number in the algorithm to generate a unique random number.';
-- ddl-end --
COMMENT ON COLUMN prereg.prid_seq.seq_no IS 'Sequence Number: Sequence number is the number generated which is used in the algorithm to generate PRID.';
-- ddl-end --
ALTER TABLE prereg.prid_seq OWNER TO sysadmin;
-- ddl-end --
