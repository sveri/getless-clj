CREATE TABLE banking_account (
id bigserial NOT NULL PRIMARY KEY,
institute_name TEXT,
account_name TEXT,
iban text UNIQUE,
users_id bigint NOT NULL,
type TEXT
);

-- TODO make type an enum


ALTER TABLE banking_account OWNER TO getless;


CREATE INDEX ON banking_account USING btree (users_id);


ALTER TABLE ONLY banking_account
    ADD FOREIGN KEY (users_id) REFERENCES users(id);
