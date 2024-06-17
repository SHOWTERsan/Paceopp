ALTER TABLE orders ADD COLUMN user_id INT;

ALTER TABLE orders ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id);
