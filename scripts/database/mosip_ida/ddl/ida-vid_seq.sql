
-- object: ida.vid_seq | type: TABLE --
-- DROP TABLE IF EXISTS ida.vid_seq CASCADE;
CREATE TABLE ida.vid_seq(
	seq_no character varying(32) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_vidseq PRIMARY KEY (seq_no)

);
-- ddl-end --
COMMENT ON TABLE ida.vid_seq IS 'Virtual ID Sequence: Stores sequence numbers that are used in the algorithm to generate vid. Stores a incremental sequence number that will be used as salt in the algorithm to generate a vid. This salt value is encrypted/hashed and used along with a seed number in the algorithm to generate a unique random number.';
-- ddl-end --
COMMENT ON COLUMN ida.vid_seq.seq_no IS 'Sequence Number: Sequence number is the number generated which is used in the algorithm to generate vid.';
-- ddl-end --
ALTER TABLE ida.vid_seq OWNER TO sysadmin;
-- ddl-end --

