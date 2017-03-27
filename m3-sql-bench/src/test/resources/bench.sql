CREATE TABLE `person` (
  id      int,
  first_name    VARCHAR(64),
  middle_name   VARCHAR(64),
  last_name     VARCHAR(64),
  phone         VARCHAR(64),
  street        VARCHAR(64),
  zip           VARCHAR(64),
  city          VARCHAR(64),
  state         VARCHAR(64),
  country       CHAR(2),
  email         VARCHAR(128),
  birth_date    DATE,
  join_date    DATE,
  last_login_date    DATE,
  login_count      int,
  PRIMARY KEY (`id`)
);