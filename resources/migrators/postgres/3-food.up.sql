CREATE TABLE food (
id bigserial NOT NULL PRIMARY KEY,
eaten_at timestamp without time zone NOT NULL,
users_id bigint NOT NULL,
product_id bigint NOT NULL
);

ALTER TABLE food OWNER TO getless;

CREATE INDEX food_users_index ON weight USING btree (users_id);

ALTER TABLE ONLY food
    ADD CONSTRAINT fk_90wtphkdtfleo9ddap43c6ukx FOREIGN KEY (users_id) REFERENCES users(id);


ALTER TABLE food ADD COLUMN amount smallint NOT NULL DEFAULT 1;

CREATE TYPE unit_enum AS ENUM ('gramm', 'liter');

ALTER TABLE food ADD COLUMN unit unit_enum NOT NULL DEFAULT 'gramm';