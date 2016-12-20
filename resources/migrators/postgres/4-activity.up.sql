CREATE TABLE activity (
id bigserial NOT NULL PRIMARY KEY,
for_date date NOT NULL,
users_id bigint NOT NULL,
content TEXT
);

ALTER TABLE activity OWNER TO getless;

DROP INDEX IF EXISTS activity_users_index;
CREATE INDEX activity_users_index ON weight USING btree (users_id);

ALTER TABLE ONLY activity
    ADD CONSTRAINT fk_users_id FOREIGN KEY (users_id) REFERENCES users(id);
