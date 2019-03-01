CREATE TABLE banking_account_balance (
id bigserial NOT NULL PRIMARY KEY,
banking_account_id bigint NOT NULL,
balance NUMERIC(12, 2) NOT NULL,
balance_date DATE NOT NULL
);



ALTER TABLE banking_account_balance OWNER TO getless;


CREATE INDEX ON banking_account_balance USING btree (banking_account_id);


ALTER TABLE ONLY banking_account_balance
    ADD FOREIGN KEY (banking_account_id) REFERENCES banking_account(id);


ALTER TABLE banking_account_balance ADD UNIQUE (banking_account_id, balance_date);
