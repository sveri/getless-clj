CREATE TABLE meal (
id bigserial NOT NULL PRIMARY KEY,
users_id bigint NOT NULL,
name character varying(150),
products TEXT
);

ALTER TABLE meal OWNER TO getless;

DROP INDEX IF EXISTS meal_users_index;
CREATE INDEX meal_users_index ON weight USING btree (users_id);

ALTER TABLE ONLY meal
    ADD CONSTRAINT fk_users_id FOREIGN KEY (users_id) REFERENCES users(id);
