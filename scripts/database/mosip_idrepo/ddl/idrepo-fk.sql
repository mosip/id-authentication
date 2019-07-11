-- object: fk_uind_uin | type: CONSTRAINT --
-- ALTER TABLE idrepo.uin_document DROP CONSTRAINT IF EXISTS fk_uind_uin CASCADE;
ALTER TABLE idrepo.uin_document ADD CONSTRAINT fk_uind_uin FOREIGN KEY (uin_ref_id)
REFERENCES idrepo.uin (uin_ref_id) ;
-- ddl-end --


-- object: fk_uinb_uin | type: CONSTRAINT --
-- ALTER TABLE idrepo.uin_biometric DROP CONSTRAINT IF EXISTS fk_uinb_uin CASCADE;
ALTER TABLE idrepo.uin_biometric ADD CONSTRAINT fk_uinb_uin FOREIGN KEY (uin_ref_id)
REFERENCES idrepo.uin (uin_ref_id) ;
-- ddl-end --
