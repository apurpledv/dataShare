-- setup
CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  email TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS files (
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  path TEXT NOT NULL,
  owner_id INTEGER NOT NULL,
  name TEXT NOT NULL,
  upload_date timestamp NOT NULL,
  expiration_date timestamp NOT NULL,
  type TEXT NOT NULL,
  size FLOAT NOT NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_fileOwner FOREIGN KEY(owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tags (
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  tag TEXT NOT NULL,
  file_id INTEGER NOT NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_tagOwner FOREIGN KEY(file_id) REFERENCES files(id) ON DELETE CASCADE
);

SET TIME ZONE 'UTC-1';

-- insert test data
INSERT INTO users (email, password) VALUES ('defaultUser', '$2a$10$7u61gALQy8G.Xw4Umy3Q6.01bcecyosJyleonTdAfs0CkW.aDHgQa');
INSERT INTO users (email, password) VALUES ('expertGamer', '$2a$10$1xqd20QnQxFlRs/5NbSqm.s0KtvH3P63Rw2aV5Xf/Bx5uRaHDS5mG');

INSERT INTO files (path, owner_id, name, upload_date, expiration_date, type, size)
  VALUES ('path/to/file.png', 1, 'file.png', NOW(), NOW() + INTERVAL '7' day, 'png', 172000);
INSERT INTO files (path, owner_id, name, upload_date, expiration_date, type, size)
  VALUES ('path/to/otherFile.jpg', 2, 'otherFile.jpg', NOW(), NOW() + INTERVAL '7' day, 'jpg', 1900);
  INSERT INTO files (path, owner_id, name, upload_date, expiration_date, type, size)
  VALUES ('path/to/otherFile2.jpg', 2, 'otherFile2.jpg', NOW(), NOW() + INTERVAL '1' minute, 'jpg', 1900);
  INSERT INTO files (path, owner_id, name, upload_date, expiration_date, type, size)
  VALUES ('path/to/otherFile3.jpg', 2, 'otherFile3.jpg', NOW(), NOW() + INTERVAL '5' minute, 'jpg', 1900);
  
INSERT INTO tags (tag, file_id) VALUES ('crackedapp', 1);
INSERT INTO tags (tag, file_id) VALUES ('download', 1);
INSERT INTO tags (tag, file_id) VALUES ('freewifi', 1);
INSERT INTO tags (tag, file_id) VALUES ('whydidyouredeemit', 1);

--USE IN CONTAINER:psql "postgresql://postgres:postgres@localhost:5432/datashare_dev"
