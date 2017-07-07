CREATE TABLE goals (
id bigserial NOT NULL PRIMARY KEY,
users_id bigint NOT NULL,
goal TEXT,
s_m_l VARCHAR(1),
done BOOLEAN DEFAULT FALSE,
created_at timestamp without time zone default (now() at time zone 'utc')
);

ALTER TABLE goals OWNER TO getless;

DROP INDEX IF EXISTS goals_users_index;
CREATE INDEX goals_users_index ON goals USING btree (users_id);

ALTER TABLE ONLY goals
    ADD CONSTRAINT fk_users_id FOREIGN KEY (users_id) REFERENCES users(id);