CREATE TABLE weight (
id bigserial NOT NULL PRIMARY KEY,
weight smallint,
weighted_at timestamp without time zone NOT NULL,
users_id bigint NOT NULL
);

CREATE INDEX users_index ON weight USING btree (users_id);


ALTER TABLE ONLY weight
    ADD CONSTRAINT fk_90wtphkdtfleo9ddap43c6ukx FOREIGN KEY (users_id) REFERENCES users(id);


--;;
INSERT INTO weight (weight, weighted_at, users_id) VALUES
(89, '2016-09-03 00:00:00-00', 1)
