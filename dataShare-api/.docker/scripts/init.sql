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
  upload_date timestamp NOT NULL DEFAULT NOW(),
  expiration_date timestamp NOT NULL,
  size FLOAT NOT NULL,
  status TEXT NOT NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_fileOwner FOREIGN KEY(owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS tags (
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  tag TEXT NOT NULL,
  file_id INTEGER NOT NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_tagOwner FOREIGN KEY(file_id) REFERENCES files(id)
);

-- insert test data
INSERT INTO users (email, password) VALUES ('defaultUser', 'defaultUser');
INSERT INTO users (email, password) VALUES ('expertGamer', 'expertGamer');

INSERT INTO files (path, owner_id, name, expiration_date, size, status)
  VALUES ('path/to/file.png', 1, 'defaultFile', NOW(), 172000, 'valid');
INSERT INTO files (path, owner_id, name, expiration_date, size, status)
  VALUES ('path/to/otherFile.jpg', 2, 'otherFile', NOW(), 1900, 'valid');
  
INSERT INTO tags (tag, file_id) VALUES ('crackedapp', 1);
INSERT INTO tags (tag, file_id) VALUES ('download', 1);
INSERT INTO tags (tag, file_id) VALUES ('freewifi', 1);
INSERT INTO tags (tag, file_id) VALUES ('whydidyouredeemit', 1);

--USE IN CONTAINER: psql "postgresql://postgres:postgres@localhost:5432/datashare_dev"
