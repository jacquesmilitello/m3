CREATE TABLE `teacher` (
  id             int,
  code           VARCHAR(64),
  prefix_code    VARCHAR(32),
  create_ts      timestamp,
  update_ts      timestamp,
  PRIMARY KEY (`id`)
);


CREATE SEQUENCE seq_teacher;
