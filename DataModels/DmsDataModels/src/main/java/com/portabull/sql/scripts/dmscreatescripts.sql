-- DROP SCHEMA dms;

CREATE SCHEMA dms AUTHORIZATION postgres;
-- dms.default_document_security definition

-- Drop table

-- DROP TABLE dms.default_document_security;

CREATE TABLE dms.default_document_security (
	default_security_id int8 NOT NULL,
	default_key varchar(255) NOT NULL,
	CONSTRAINT default_document_security_pkey PRIMARY KEY (default_security_id)
);

-- Permissions

ALTER TABLE dms.default_document_security OWNER TO postgres;
GRANT ALL ON TABLE dms.default_document_security TO postgres;


-- dms.dynamic_document_security definition

-- Drop table

-- DROP TABLE dms.dynamic_document_security;

CREATE TABLE dms.dynamic_document_security (
	dynamic_security_id int8 NOT NULL,
	dynamic_key varchar(255) NOT NULL,
	CONSTRAINT dynamic_document_security_pkey PRIMARY KEY (dynamic_security_id)
);

-- Permissions

ALTER TABLE dms.dynamic_document_security OWNER TO postgres;
GRANT ALL ON TABLE dms.dynamic_document_security TO postgres;


-- dms.document_data definition

-- Drop table

-- DROP TABLE dms.document_data;

CREATE TABLE dms.document_data (
	id int8 NOT NULL,
	description varchar(255) NULL,
	e_location varchar(255) NOT NULL,
	"name" varchar(255) NULL,
	"size" int8 NULL,
	uploaded_date timestamp NULL,
	user_id int8 NOT NULL,
	user_name varchar(255) NOT NULL,
	default_security_id int8 NULL,
	dynamic_security_id int8 NULL,
	CONSTRAINT document_data_pkey PRIMARY KEY (id),
	CONSTRAINT uk_g5xyfou76l2y4gc493exs3r0x UNIQUE (e_location)
);

-- Permissions

ALTER TABLE dms.document_data OWNER TO postgres;
GRANT ALL ON TABLE dms.document_data TO postgres;


-- dms.document_data foreign keys

ALTER TABLE dms.document_data ADD CONSTRAINT document_data_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_management.user_credentials(user_id);
ALTER TABLE dms.document_data ADD CONSTRAINT fkcmr13uilfoq42661qjdkcyxie FOREIGN KEY (default_security_id) REFERENCES dms.default_document_security(default_security_id);
ALTER TABLE dms.document_data ADD CONSTRAINT fkeavolrl585vu8pkd5tn85hynl FOREIGN KEY (dynamic_security_id) REFERENCES dms.dynamic_document_security(dynamic_security_id);




-- Permissions

GRANT ALL ON SCHEMA dms TO postgres;




create table dms.user_directory (dir_id int8 not null, dir_created_date timestamp, dir_level int4, directory_name varchar(255), root_dir boolean, parent_dir_id int8, primary key (dir_id))
create table dms.user_doc_dir_mapping (document_id int8 not null, dir_id int8 not null, primary key (dir_id, document_id))
alter table dms.user_directory add constraint FKpmfoboq6c36d484s1i0ykfso5 foreign key (parent_dir_id) references dms.user_directory
alter table dms.user_doc_dir_mapping add constraint FK5nw273t2te3ttn1hqttfhbeos foreign key (dir_id) references dms.user_directory
alter table dms.user_doc_dir_mapping add constraint FKkhh17ilbgm3v6ehot7uow2ad5 foreign key (document_id) references dms.document_data




CREATE INDEX indexDmsDocuId
ON dms.document_data (id);


CREATE INDEX indexDmsDynamicSecurityId
ON dms.dynamic_document_security (dynamic_security_id );

CREATE INDEX indexDmsDefaultSecurityId
ON dms.default_document_security  (default_security_id  );

CREATE INDEX indexuserDirectoryDirId
ON dms.user_directory  (dir_id);


CREATE INDEX indexuserDirectoryParentId
ON dms.user_directory  (parent_dir_id);

CREATE INDEX indexuserDocMappingDocId
ON dms.user_doc_dir_mapping  (document_id);


CREATE INDEX indexuserDocMappingDirId
ON dms.user_doc_dir_mapping  (dir_id);

 alter table dms.user_directory add column user_id int8 not null;

 alter table dms.user_directory add column is_deleted boolean;

  create table dms.document_notes (note_id int8 not null, created_date timestamp, note_hidden boolean default false, notes TEXT, tittle varchar(255), updated_date timestamp, user_id int8, primary key (note_id));
  create sequence seq_document_notes start 1 increment 1


