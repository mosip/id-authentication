-- object: prereguser | type: ROLE --
-- DROP ROLE IF EXISTS prereguser;
CREATE ROLE prereguser WITH 
	INHERIT
	LOGIN
	PASSWORD 'Mosip@dev123';
-- ddl-end --

