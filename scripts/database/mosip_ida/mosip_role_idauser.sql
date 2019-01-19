-- object: idauser | type: ROLE --
-- DROP ROLE IF EXISTS idauser;
CREATE ROLE idauser WITH 
	INHERIT
	LOGIN
	PASSWORD 'Mosip@dev123';
-- ddl-end --

