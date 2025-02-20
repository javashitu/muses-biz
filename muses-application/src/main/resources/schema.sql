DROP TABLE IF EXISTS purchases;

CREATE TABLE purchases (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  card_type VARCHAR(10) NOT NULL,
  card_number VARCHAR(16) NOT NULL,
  total_amount INT DEFAULT 0,
  txn_curr VARCHAR(3) NOT NULL
);

DROP TABLE IF EXISTS card_details;

CREATE TABLE Card_Details (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  expiry_date VARCHAR(250) NOT NULL,
  card_number VARCHAR(16) NOT NULL,
  card_holder_name VARCHAR(32) NOT NULL
);

DROP TABLE IF EXISTS user_txn;

CREATE TABLE user_txn (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  user_id VARCHAR(10) NOT NULL,
  card_holder_name VARCHAR(32) NOT NULL,
  card_number VARCHAR(16) NOT NULL,
  txn_amount DECIMAL(10,2),
  txn_curr VARCHAR(3),
  txn_date DATE NOT NULL
);

DROP TABLE IF EXISTS promotions;

CREATE TABLE promotions (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  user_id VARCHAR(10) NOT NULL,
  name VARCHAR(32) NOT NULL,
  link VARCHAR(16) NOT NULL
);