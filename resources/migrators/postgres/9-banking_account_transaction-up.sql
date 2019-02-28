CREATE TABLE banking_account_transaction (
id bigserial NOT NULL PRIMARY KEY,
banking_account_id bigint NOT NULL,
booking_date DATE NOT NULL,
value_date DATE NOT NULL,
booking_text TEXT,
contractor_beneficiary TEXT,
purpose TEXT,
banking_account_number TEXT,
blz TEXT,
amount NUMERIC(12, 2),
amount_currency TEXT,
creditor_id TEXT,
mandate_reference TEXT,
customer_reference TEXT
);


ALTER TABLE banking_account_transaction OWNER TO getless;


CREATE INDEX ON banking_account_transaction USING btree (banking_account_id);


ALTER TABLE ONLY banking_account_transaction
    ADD FOREIGN KEY (banking_account_id) REFERENCES banking_account(id);
