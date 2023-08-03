-- DROP SCHEMA spf;

CREATE SCHEMA spf AUTHORIZATION postgres;
-- spf.spf_form_manager definition

-- Drop table

-- DROP TABLE spf.spf_form_manager;

CREATE TABLE spf.spf_form_manager (
	form_id int8 NOT NULL,
	form_description varchar(255) NULL,
	form_label varchar(255) NOT NULL,
	form_mapped_to_all_users bool NULL,
	form_name varchar(255) NOT NULL,
	form_parent_table varchar(255) NULL,
	form_screning_data varchar(255) NULL,
	CONSTRAINT spf_form_manager_pkey PRIMARY KEY (form_id)
);

-- Permissions

ALTER TABLE spf.spf_form_manager OWNER TO postgres;
GRANT ALL ON TABLE spf.spf_form_manager TO postgres;


-- spf.spf_form_child_tables definition

-- Drop table

-- DROP TABLE spf.spf_form_child_tables;

CREATE TABLE spf.spf_form_child_tables (
	form_child_id int8 NOT NULL,
	form_id int8 NULL,
	child_table_name varchar(255) NOT NULL,
	CONSTRAINT spf_form_child_tables_pkey PRIMARY KEY (form_child_id),
	CONSTRAINT fk1fci9n0kgr33tyfgj3j77jj6m FOREIGN KEY (form_id) REFERENCES spf.spf_form_manager(form_id)
);

-- Permissions

ALTER TABLE spf.spf_form_child_tables OWNER TO postgres;
GRANT ALL ON TABLE spf.spf_form_child_tables TO postgres;


-- spf.spf_user_form_mapping definition

-- Drop table

-- DROP TABLE spf.spf_user_form_mapping;

CREATE TABLE spf.spf_user_form_mapping (
	user_form_mapping_id int8 NOT NULL,
	form_id int8 NULL,
	user_id int8 NULL,
	CONSTRAINT spf_user_form_mapping_pkey PRIMARY KEY (user_form_mapping_id),
	CONSTRAINT ukip0qikmrtapd929l13fyhi6mn UNIQUE (form_id, user_id)
);

-- Permissions

ALTER TABLE spf.spf_user_form_mapping OWNER TO postgres;
GRANT ALL ON TABLE spf.spf_user_form_mapping TO postgres;


-- spf.spf_user_form_mapping foreign keys

ALTER TABLE spf.spf_user_form_mapping ADD CONSTRAINT fk2uq87su3uyifeywb32hh1hyjn FOREIGN KEY (user_id) REFERENCES user_management.user_credentials(user_id);
ALTER TABLE spf.spf_user_form_mapping ADD CONSTRAINT fkpye24vwtj0v9blsrwrkci0l1x FOREIGN KEY (form_id) REFERENCES spf.spf_form_manager(form_id);




-- Permissions

GRANT ALL ON SCHEMA spf TO postgres;
