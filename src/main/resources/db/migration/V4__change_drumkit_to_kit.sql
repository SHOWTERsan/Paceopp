ALTER TABLE drumkit
    RENAME COLUMN image_url TO image_id;

ALTER TABLE drumkit
    ALTER COLUMN image_id TYPE integer USING image_id::integer;

ALTER TABLE drumkit
    ADD COLUMN data bytea;

ALTER TABLE drumkit
    ADD CONSTRAINT fk_drumkit_image_id FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE SET NULL;