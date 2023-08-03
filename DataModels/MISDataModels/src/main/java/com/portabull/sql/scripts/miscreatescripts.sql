-- DROP SCHEMA mis;

CREATE SCHEMA mis AUTHORIZATION postgres;
-- mis.mis_mail_audit definition

-- Drop table

-- DROP TABLE mis.mis_mail_audit;

CREATE TABLE mis.mis_mail_audit (
	mail_id int8 NOT NULL,
	mis_id int8 NULL,
	mis_mail_data varchar(255) NULL,
	CONSTRAINT mis_mail_audit_pkey PRIMARY KEY (mail_id)
);

-- Permissions

ALTER TABLE mis.mis_mail_audit OWNER TO postgres;
GRANT ALL ON TABLE mis.mis_mail_audit TO postgres;


-- mis.mis_report definition

-- Drop table

-- DROP TABLE mis.mis_report;

CREATE TABLE mis.mis_report (
	mis_id int8 NOT NULL,
	document_tittle varchar(255) NULL,
	download_count int8 NULL,
	group_by varchar(255) NULL,
	header_color_code varchar(255) NULL,
	is_logo_required bool NULL,
	timestamp_required bool NULL,
	logo_dms_id int8 NULL,
	mail_count int8 NULL,
	mis_name varchar(255) NOT NULL,
	order_by varchar(255) NULL,
	temp_logo_image_path_for_pdf varchar(255) NULL,
	timestamp_format varchar(255) NULL,
	user_id int8 NOT NULL,
	view_name varchar(255) NOT NULL,
	CONSTRAINT mis_report_pkey PRIMARY KEY (mis_id)
);

-- Permissions

ALTER TABLE mis.mis_report OWNER TO postgres;
GRANT ALL ON TABLE mis.mis_report TO postgres;


-- mis.mis_select_clause definition

-- Drop table

-- DROP TABLE mis.mis_select_clause;

CREATE TABLE mis.mis_select_clause (
	select_id int8 NOT NULL,
	alias_name varchar(255) NULL,
	mis_id int8 NULL,
	select_name varchar(255) NOT NULL,
	CONSTRAINT mis_select_clause_pkey PRIMARY KEY (select_id)
);

-- Permissions

ALTER TABLE mis.mis_select_clause OWNER TO postgres;
GRANT ALL ON TABLE mis.mis_select_clause TO postgres;


-- mis.mis_where_clause definition

-- Drop table

-- DROP TABLE mis.mis_where_clause;

CREATE TABLE mis.mis_where_clause (
	where_id int8 NOT NULL,
	default_value varchar(255) NULL,
	mis_id int8 NULL,
	user_id int8 NOT NULL,
	where_clause varchar(255) NOT NULL,
	CONSTRAINT mis_where_clause_pkey PRIMARY KEY (where_id)
);

-- Permissions

ALTER TABLE mis.mis_where_clause OWNER TO postgres;
GRANT ALL ON TABLE mis.mis_where_clause TO postgres;


-- mis.mis_mail_audit foreign keys

ALTER TABLE mis.mis_mail_audit ADD CONSTRAINT fkccx9eex86athsd9wqsgbjbqt8 FOREIGN KEY (mis_id) REFERENCES mis.mis_report(mis_id);


-- mis.mis_report foreign keys

ALTER TABLE mis.mis_report ADD CONSTRAINT mis_report_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_management.user_credentials(user_id);


-- mis.mis_select_clause foreign keys

ALTER TABLE mis.mis_select_clause ADD CONSTRAINT fk52ahbfhqf8hbvgpdn2dvnrpva FOREIGN KEY (mis_id) REFERENCES mis.mis_report(mis_id);


-- mis.mis_where_clause foreign keys

ALTER TABLE mis.mis_where_clause ADD CONSTRAINT fki2hesfoov7mjrrkqiiyaavx1i FOREIGN KEY (mis_id) REFERENCES mis.mis_report(mis_id);
ALTER TABLE mis.mis_where_clause ADD CONSTRAINT mis_where_clause_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_management.user_credentials(user_id);




-- Permissions

GRANT ALL ON SCHEMA mis TO postgres;
