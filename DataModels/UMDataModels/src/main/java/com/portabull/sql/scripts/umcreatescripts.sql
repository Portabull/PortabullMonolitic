-- DROP SCHEMA user_management;

CREATE SCHEMA user_management AUTHORIZATION postgres;
-- user_management.registration_in_otp definition

-- Drop table

-- DROP TABLE user_management.registration_in_otp;

CREATE TABLE user_management.registration_in_otp (
	logged_in_otp_id int8 NOT NULL,
	otp varchar(255) NOT NULL,
	otp_created_date timestamp NULL,
	otp_expired_date timestamp NULL,
	otp_length int8 NULL,
	"token" varchar(255) NULL,
	type_of_otp varchar(255) NULL,
	CONSTRAINT registration_in_otp_pkey PRIMARY KEY (logged_in_otp_id)
);

-- Permissions

ALTER TABLE user_management.registration_in_otp OWNER TO postgres;
GRANT ALL ON TABLE user_management.registration_in_otp TO postgres;


-- user_management.logged_in_otp definition

-- Drop table

-- DROP TABLE user_management.logged_in_otp;

CREATE TABLE user_management.logged_in_otp (
	logged_in_otp_id int8 NOT NULL,
	otp varchar(255) NOT NULL,
	otp_created_date timestamp NULL,
	otp_expired_date timestamp NULL,
	otp_length int8 NULL,
	type_of_otp varchar(255) NULL,
	user_id int8 NULL,
	CONSTRAINT logged_in_otp_pkey PRIMARY KEY (logged_in_otp_id)
);

-- Permissions

ALTER TABLE user_management.logged_in_otp OWNER TO postgres;
GRANT ALL ON TABLE user_management.logged_in_otp TO postgres;


-- user_management.user_credentials definition

-- Drop table

-- DROP TABLE user_management.user_credentials;

CREATE TABLE user_management.user_credentials (
	user_id int8 NOT NULL,
	is_two_step_verification_enabled bool NULL,
	logged_in_session_time_in_mins int8 NULL,
	no_of_last_logged_in_details_capture int8 NULL,
	"password" varchar(255) NULL,
	user_name varchar(255) NULL,
	wrong_password_count int4 NULL,
	account_unlock_reason varchar(255) NULL,
	is_account_locked bool NULL,
	logged_with_oauth bool NULL,
	user_already_registered bool NULL,
	is_admin bool NULL,
	profile_pic_dms_id int8 NULL,
	is_single_sign_in bool NULL,
	CONSTRAINT uk_atj1oy8pyni68uc7u27ud77op UNIQUE (user_name),
	CONSTRAINT user_credentials_pkey PRIMARY KEY (user_id)
);

-- Permissions

ALTER TABLE user_management.user_credentials OWNER TO postgres;
GRANT ALL ON TABLE user_management.user_credentials TO postgres;


-- user_management.user_logged_in_details definition

-- Drop table

-- DROP TABLE user_management.user_logged_in_details;

CREATE TABLE user_management.user_logged_in_details (
	logged_in_user_details_id int8 NOT NULL,
	lattitude varchar(255) NULL,
	logged_in_date timestamp NULL,
	longitude varchar(255) NULL,
	successfully_logged_in bool NULL,
	user_id int8 NULL,
	CONSTRAINT user_logged_in_details_pkey PRIMARY KEY (logged_in_user_details_id)
);

-- Permissions

ALTER TABLE user_management.user_logged_in_details OWNER TO postgres;
GRANT ALL ON TABLE user_management.user_logged_in_details TO postgres;


-- user_management.user_profile definition

-- Drop table

-- DROP TABLE user_management.user_profile;

CREATE TABLE user_management.user_profile (
	address varchar(255) NULL,
	alternative_mobile_number varchar(255) NULL,
	city varchar(255) NULL,
	landmark varchar(255) NULL,
	mobile_number varchar(255) NULL,
	pincode varchar(255) NULL,
	reporting_to int8 NULL,
	state varchar(255) NULL,
	street_address varchar(255) NULL,
	user_name varchar(255) NULL,
	user_id int8 NOT NULL,
	CONSTRAINT user_profile_pkey PRIMARY KEY (user_id)
);

-- Permissions

ALTER TABLE user_management.user_profile OWNER TO postgres;
GRANT ALL ON TABLE user_management.user_profile TO postgres;


-- user_management.logged_in_otp foreign keys

ALTER TABLE user_management.logged_in_otp ADD CONSTRAINT fkgev8ykneofu04j3khurioxqpn FOREIGN KEY (user_id) REFERENCES user_management.user_credentials(user_id);


-- user_management.user_credentials foreign keys

ALTER TABLE user_management.user_credentials ADD CONSTRAINT user_credentials_profile_pic_dms_id_fkey FOREIGN KEY (profile_pic_dms_id) REFERENCES dms.document_data(id);


-- user_management.user_logged_in_details foreign keys

ALTER TABLE user_management.user_logged_in_details ADD CONSTRAINT user_logged_in_details_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_management.user_credentials(user_id);


-- user_management.user_profile foreign keys

ALTER TABLE user_management.user_profile ADD CONSTRAINT fkob20954dtpxsj36lajm7uqunr FOREIGN KEY (user_id) REFERENCES user_management.user_credentials(user_id);
ALTER TABLE user_management.user_profile ADD CONSTRAINT fksek9ggfx6ojpg33v8otyi2h0e FOREIGN KEY (reporting_to) REFERENCES user_management.user_profile(user_id);


-- user_management.user_info source

CREATE OR REPLACE VIEW user_management.user_info
AS SELECT up.user_id AS userid,
    up.user_name AS username,
    up.address,
    up.mobile_number AS mobilenumber,
    up.alternative_mobile_number AS alternativemobilenumber,
    up.city,
    up.state
   FROM user_management.user_credentials uc
     JOIN user_management.user_profile up ON uc.user_id = up.user_id;

-- Permissions

ALTER TABLE user_management.user_info OWNER TO postgres;
GRANT ALL ON TABLE user_management.user_info TO postgres;


-- user_management.view_name source

CREATE OR REPLACE VIEW user_management.view_name
AS SELECT up.address,
    up.alternative_mobile_number,
    up.city,
    up.landmark,
    up.mobile_number,
    up.pincode,
    up.reporting_to,
    up.state,
    up.street_address,
    up.user_name,
    up.user_id
   FROM user_management.user_profile up;

-- Permissions

ALTER TABLE user_management.view_name OWNER TO postgres;
GRANT ALL ON TABLE user_management.view_name TO postgres;




-- Permissions

GRANT ALL ON SCHEMA user_management TO postgres;


alter table user_management.user_credentials add column mfa_login_type int4 default 0;





alter table user_management.user_credentials add column login_user_name varchar(255);
alter table user_management.user_credentials drop constraint UK_g5u43txxey26ouly11jq9e48d;
alter table user_management.user_credentials add constraint UK_g5u43txxey26ouly11jq9e48d unique (login_user_name);
ALTER TABLE  user_management.user_profile DROP COLUMN user_name;


create sequence seq_user_cred_id start 1 increment 1;
create sequence seq_logged_in_otp_id start 1 increment 1;
create sequence seq_logged_in_dtls_id start 1 increment 1;
create sequence seq_registration_otp_id start 1 increment 1;






create table user_management.notification_user_jwt_token (notification_user_id int8 not null, approval int4 default 0, expiry_date int8, jwt_token varchar(255), random_token_id varchar(255), primary key (notification_user_id))
Hibernate: alter table user_management.notification_user_jwt_token drop constraint UK_ber35634mdlmd82j7iryer8nh
Hibernate: alter table user_management.notification_user_jwt_token add constraint UK_ber35634mdlmd82j7iryer8nh unique (jwt_token)
Hibernate: alter table user_management.notification_user_jwt_token drop constraint UK_mfbkui0vs6xx6vekrxstihkfr
Hibernate: alter table user_management.notification_user_jwt_token add constraint UK_mfbkui0vs6xx6vekrxstihkfr unique (random_token_id)