CREATE TABLE beats
(
    id bigserial PRIMARY KEY,
    name character varying(100) NOT NULL,
    bpm integer NOT NULL,
    image_id integer
);
CREATE TABLE audio_files
(
    id bigserial PRIMARY KEY,
    beat_id integer NOT NULL,
    file_format character varying(50),
    data bytea
);
CREATE TABLE images
(
    id bigserial PRIMARY KEY,
    name character varying(255),
    data bytea
);

ALTER TABLE ONLY public.beats
    ADD CONSTRAINT fk_image_id FOREIGN KEY (image_id) REFERENCES public.images(id) ON DELETE SET NULL;

ALTER TABLE ONLY public.audio_files
    ADD CONSTRAINT audio_files_beat_id_fkey FOREIGN KEY (beat_id) REFERENCES public.beats(id);