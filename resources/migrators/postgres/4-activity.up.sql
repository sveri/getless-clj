CREATE TABLE activity (
id bigserial NOT NULL PRIMARY KEY,
written_at timestamp without time zone NOT NULL,
edited_at timestamp without time zone NOT NULL,
users_id bigint NOT NULL,
content TEXT
);

ALTER TABLE activity OWNER TO getless;

CREATE INDEX activity_users_index ON weight USING btree (users_id);

ALTER TABLE ONLY activity
    ADD CONSTRAINT fk_users_id FOREIGN KEY (users_id) REFERENCES users(id);
