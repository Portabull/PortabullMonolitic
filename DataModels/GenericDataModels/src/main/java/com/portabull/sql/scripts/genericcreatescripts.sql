-- DROP SCHEMA portabull_generic;

CREATE SCHEMA portabull_generic AUTHORIZATION postgres;
-- portabull_generic.audit_details definition

-- Drop table

-- DROP TABLE portabull_generic.audit_details;

CREATE TABLE portabull_generic.audit_details (
	id int8 NOT NULL,
	"data" varchar(5000) NULL,
	entity_name varchar(255) NULL,
	operation_type varchar(255) NULL,
	CONSTRAINT audit_details_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE portabull_generic.audit_details OWNER TO postgres;
GRANT ALL ON TABLE portabull_generic.audit_details TO postgres;


-- portabull_generic.customer_enquiry definition

-- Drop table

-- DROP TABLE portabull_generic.customer_enquiry;

CREATE TABLE portabull_generic.customer_enquiry (
	customer_id int8 NOT NULL,
	customer_email varchar(255) NOT NULL,
	customer_name varchar(255) NOT NULL,
	message varchar(255) NOT NULL,
	customer_mobile_number varchar(255) NULL,
	subject varchar(255) NOT NULL,
	enquiry_date int8 NULL,
	CONSTRAINT customer_enquiry_pkey PRIMARY KEY (customer_id)
);

-- Permissions

ALTER TABLE portabull_generic.customer_enquiry OWNER TO postgres;
GRANT ALL ON TABLE portabull_generic.customer_enquiry TO postgres;


-- portabull_generic.html_templates definition

-- Drop table

-- DROP TABLE portabull_generic.html_templates;

CREATE TABLE portabull_generic.html_templates (
	template_id int8 NOT NULL,
	"template" varchar(255) NULL,
	CONSTRAINT html_templates_pkey PRIMARY KEY (template_id)
);

-- Permissions

ALTER TABLE portabull_generic.html_templates OWNER TO postgres;
GRANT ALL ON TABLE portabull_generic.html_templates TO postgres;


-- portabull_generic.master_image_uri definition

-- Drop table

-- DROP TABLE portabull_generic.master_image_uri;

CREATE TABLE portabull_generic.master_image_uri (
	master_uri_id int8 NOT NULL,
	description varchar(255) NULL,
	image_name varchar(255) NULL,
	image_url varchar(255) NULL,
	purpose varchar(255) NULL,
	CONSTRAINT master_image_uri_pkey PRIMARY KEY (master_uri_id)
);

-- Permissions

ALTER TABLE portabull_generic.master_image_uri OWNER TO postgres;
GRANT ALL ON TABLE portabull_generic.master_image_uri TO postgres;


-- portabull_generic.one_time_password definition

-- Drop table

-- DROP TABLE portabull_generic.one_time_password;

CREATE TABLE portabull_generic.one_time_password (
	otp_id int8 NOT NULL,
	otp varchar(255) NOT NULL,
	otp_created_date timestamp NULL,
	otp_expired_date timestamp NULL,
	otp_length int8 NULL,
	type_of_otp varchar(255) NULL,
	CONSTRAINT one_time_password_pkey PRIMARY KEY (otp_id)
);

-- Permissions

ALTER TABLE portabull_generic.one_time_password OWNER TO postgres;
GRANT ALL ON TABLE portabull_generic.one_time_password TO postgres;


-- portabull_generic.image_uri definition

-- Drop table

-- DROP TABLE portabull_generic.image_uri;

CREATE TABLE portabull_generic.image_uri (
	masteruriid int8 NOT NULL,
	replace_url_name varchar(255) NULL,
	template_id int8 NULL,
	CONSTRAINT image_uri_pkey PRIMARY KEY (masteruriid),
	CONSTRAINT fkg4oximx7vuo17ie5nbfu6a0b6 FOREIGN KEY (masteruriid) REFERENCES portabull_generic.master_image_uri(master_uri_id),
	CONSTRAINT fkj47bt2uu72ysg27je24vu06j7 FOREIGN KEY (template_id) REFERENCES portabull_generic.html_templates(template_id)
);

-- Permissions

ALTER TABLE portabull_generic.image_uri OWNER TO postgres;
GRANT ALL ON TABLE portabull_generic.image_uri TO postgres;


-- portabull_generic.internal_emails definition

-- Drop table

-- DROP TABLE portabull_generic.internal_emails;

CREATE TABLE portabull_generic.internal_emails (
	message_id int8 NOT NULL,
	message_subject varchar(255) NULL,
	receiver_address varchar(255) NOT NULL,
	receiver_id int8 NOT NULL,
	sender_id int8 NOT NULL,
	sender_address varchar(255) NOT NULL,
	message_body text NULL,
	created_date varchar(255) NULL,
	mail_seen bool NULL,
	CONSTRAINT internal_emails_pkey PRIMARY KEY (message_id)
);

-- Permissions

ALTER TABLE portabull_generic.internal_emails OWNER TO postgres;
GRANT ALL ON TABLE portabull_generic.internal_emails TO postgres;


-- portabull_generic.user_document_storage definition

-- Drop table

-- DROP TABLE portabull_generic.user_document_storage;

CREATE TABLE portabull_generic.user_document_storage (
	storage_id int8 NOT NULL,
	storage_size float8 NULL,
	user_id int8 NULL,
	user_storage_size float8 NULL,
	CONSTRAINT uk_c4hpdqqsseb3un9xqnukq7huf UNIQUE (user_id),
	CONSTRAINT user_document_storage_pkey PRIMARY KEY (storage_id)
);

-- Permissions

ALTER TABLE portabull_generic.user_document_storage OWNER TO postgres;
GRANT ALL ON TABLE portabull_generic.user_document_storage TO postgres;


-- portabull_generic.internal_emails foreign keys

ALTER TABLE portabull_generic.internal_emails ADD CONSTRAINT internal_emails_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES user_management.user_credentials(user_id);
ALTER TABLE portabull_generic.internal_emails ADD CONSTRAINT internal_emails_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES user_management.user_credentials(user_id);


-- portabull_generic.user_document_storage foreign keys

ALTER TABLE portabull_generic.user_document_storage ADD CONSTRAINT user_document_storage_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_management.user_credentials(user_id);




-- Permissions

GRANT ALL ON SCHEMA portabull_generic TO postgres;


create sequence seq_doc_storage_id start 160001013 increment 1;
create sequence seq_message_id start 160001013 increment 1;


create table portabull_generic.user_note_settings (user_id int8 not null, enable_share_notes boolean default true, hide_delete_button boolean default false, show_hidden_notes boolean default false, primary key (user_id))



create table portabull_generic.shared_notes_settings (share_id int8 not null, note_id int8, notes_delete boolean default false, notes_downloadable boolean default true, notes_editable boolean default false, notes_sharable boolean default false, shared_by int8, shared_to int8, primary key (share_id))


create table portabull_generic.user_acc_cal_details (user_acc_cal_id int8 not null, created_date timestamp, description varchar(255), profit_loss float8, tittle varchar(255), total_amount float8, updated_date timestamp, primary key (user_acc_cal_id));