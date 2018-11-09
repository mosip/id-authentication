CREATE schema IF NOT EXISTS ids;

create table  if not exists  ids.rid (
dongle_id character varying(64) not null primary key,
sequence_id NUMERIC(5) not null
);


