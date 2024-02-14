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











create table portabull_generic.ad_activation_history (activation_id int8 not null, activate boolean, activate_desc varchar(255), activate_reason varchar(255), created_by int8, created_date timestamp, updated_by int8, updated_date timestamp, primary key (activation_id));
create table portabull_generic.ad_company_information (client_id int8 not null, company_address1 varchar(255), company_address2 varchar(255), company_biography TEXT, company_city varchar(255), company_email varchar(255), company_logo TEXT, company_mobile varchar(255), company_name varchar(255), company_state varchar(255), company_zip varchar(255), primary key (client_id));
create table portabull_generic.ad_company_master (company_master_id int8 not null, ad_desc varchar(255), images TEXT, meta_data TEXT, primary key (company_master_id));
create table portabull_generic.ad_company_tool_subscriptions (toolId int8 not null, activation_last_date timestamp, activation_time_in_days int8, active boolean, created_by int8, created_date timestamp, deactivate_reason boolean, updated_by int8, updated_date timestamp, activationId int8 not null, clientId int8 not null, masterId int8 not null, paymentId int8 not null, primary key (toolId));
create table portabull_generic.ad_payment_details (payment_id int8 not null, payment_created_by int8, payment_created_date timestamp, payment_desc varchar(255), payment_name varchar(255), payment_updated_by int8, payment_updated_date timestamp, plan_payment_amount_due float8, total_plan_payment_amount float8, total_subscription_amount float8, primary key (payment_id));
create table portabull_generic.ad_payment_history (paymentId int8 not null, payment_amount float8, payment_history_created_by int8, payment_history_created_date timestamp, payment_history_desc float8, payment_history_updated_by int8, payment_history_updated_date timestamp, primary key (paymentId));
create table portabull_generic.ad_portabull_tools (tool_id int8 not null, tool_details varchar(255), tool_dimensions TEXT, tool_html_pages varchar(255), tool_name varchar(255), primary key (tool_id));
alter table portabull_generic.ad_company_information drop constraint UK_s8obcbqma6pr134cqqn2ne9jh;
alter table portabull_generic.ad_company_information add constraint UK_s8obcbqma6pr134cqqn2ne9jh unique (company_name);
alter table portabull_generic.ad_company_tool_subscriptions drop constraint UK_5m27cf88c85054tm1rnhuura4;
alter table portabull_generic.ad_company_tool_subscriptions add constraint UK_5m27cf88c85054tm1rnhuura4 unique (activationId);
alter table portabull_generic.ad_company_tool_subscriptions drop constraint UK_22kqor4cd5eskf591hk5t352v;
alter table portabull_generic.ad_company_tool_subscriptions add constraint UK_22kqor4cd5eskf591hk5t352v unique (clientId);
alter table portabull_generic.ad_company_tool_subscriptions drop constraint UK_s9ip33n8nh429bif7nbggcmnc;
alter table portabull_generic.ad_company_tool_subscriptions add constraint UK_s9ip33n8nh429bif7nbggcmnc unique (masterId);
alter table portabull_generic.ad_company_tool_subscriptions drop constraint UK_nbrort8om4vrkft6074lugp8b;
alter table portabull_generic.ad_company_tool_subscriptions add constraint UK_nbrort8om4vrkft6074lugp8b unique (paymentId);
alter table portabull_generic.ad_portabull_tools drop constraint UK_tkess494fuustigjvq7os4wk9;
alter table portabull_generic.ad_portabull_tools add constraint UK_tkess494fuustigjvq7os4wk9 unique (tool_name);
alter table portabull_generic.ad_company_tool_subscriptions add constraint FKhtyd42rk6yafkedk9vmmy9nm8 foreign key (activationId) references portabull_generic.ad_activation_history;
alter table portabull_generic.ad_company_tool_subscriptions add constraint FKmspivta5069mvubjb53b919yd foreign key (clientId) references portabull_generic.ad_company_information;
alter table portabull_generic.ad_company_tool_subscriptions add constraint FK9ryb4awjb43dcdvm5p8blgjo5 foreign key (masterId) references portabull_generic.ad_company_master;
alter table portabull_generic.ad_company_tool_subscriptions add constraint FKq5pwhh5up25gv4e6hj25a06cx foreign key (paymentId) references portabull_generic.ad_payment_details;
alter table portabull_generic.ad_company_tool_subscriptions add constraint FKdjrojj1ct43y672a3y4k6htg5 foreign key (toolId) references portabull_generic.ad_portabull_tools;
alter table portabull_generic.ad_payment_history add constraint FK9bq6f12cysvbfj6aa6pcjifr9 foreign key (paymentId) references portabull_generic.ad_payment_details;





create table portabull_generic.scheduler_actions (scheduler_action_id int8 not null, action TEXT, action_type varchar(255), scheduler_id int8, primary key (scheduler_action_id))
create table portabull_generic.scheduler_task (scheduler_id int8 not null, days varchar(255), is_active boolean, last_triggered_date timestamp, scheduler_name varchar(255), specific_date timestamp, time_gap int4, trigger_type varchar(255), user_id int8, primary key (scheduler_id))
create sequence seq_scheduler_action_id start 1 increment 1
create sequence seq_scheduler_id start 1 increment 1
alter table portabull_generic.scheduler_actions add constraint FKs2gkv5y7ewyyw1ppg2qmckuar foreign key (scheduler_id) references portabull_generic.scheduler_task;











insert into portabull_generic.static_java_imports values(1,'java','import java.util.*;import java.io.*;import java.math.*; import java.text.*; import java.util.concurrent.*;import javax.swing.*;import java.awt.*; import java.nio.*;');
insert into portabull_generic.static_java_imports values(2,'springframework','import org.springframework.http.*;import org.springframework.web.*;');
insert into portabull_generic.static_java_imports values(3,'java&springframework','import javax.mail.*;import org.springframework.mail.javamail.*;import java.io.*;');
insert into portabull_generic.static_java_imports values(4,'apacheRestClient','import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;import org.apache.http.conn.ssl.TrustSelfSignedStrategy;import org.apache.http.ssl.SSLContexts;import org.apache.http.impl.client.HttpClients;import org.springframework.web.client.DefaultResponseErrorHandler;import org.springframework.web.client.RestTemplate;');