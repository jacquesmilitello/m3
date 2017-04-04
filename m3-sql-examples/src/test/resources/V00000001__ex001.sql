CREATE TABLE `student` (
  id             int,
  code           VARCHAR(64),
  age            int,
  overall_rating long,
  created_at     datetime(3),
  readOnly       VARCHAR(100),
  PRIMARY KEY (`id`)
);

CREATE TABLE `teacher` (
  id             int,
  code           VARCHAR(64),
  prefix_code    VARCHAR(32),
  PRIMARY KEY (`id`)
);


CREATE SEQUENCE seq_teacher;
