-- Rename the table from drumkit to kit
ALTER TABLE drumkit RENAME TO kit;

-- Delete the data column
ALTER TABLE kit DROP COLUMN data;

-- Add the archive_id column
ALTER TABLE kit ADD COLUMN archive_id integer;

CREATE TABLE kit_archive
(
    id   serial PRIMARY KEY,
    data bytea
);


-- Add a foreign key constraint on the archive_id column
ALTER TABLE kit
    ADD CONSTRAINT fk_kit_archive_id FOREIGN KEY (archive_id) REFERENCES kit_archive(id) ON DELETE SET NULL;

-- Create the kit_archive table

CREATE TABLE orders (
    id serial PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    service_id INT NOT NULL,
    beat_id INT NOT NULL,
    order_date TIMESTAMP NOT NULL,
    FOREIGN KEY (beat_id) REFERENCES beats(id)
);
