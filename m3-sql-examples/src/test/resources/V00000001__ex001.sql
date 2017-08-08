CREATE TABLE `student` (
  id             int,
  code           VARCHAR(64),
  age            int,
  overall_rating long,
  created_at     datetime(3),
  readOnly       VARCHAR(100),
  PRIMARY KEY (`id`)
);

CREATE TABLE `auto_increment_pojo` (
  id             int         auto_increment,
  name           VARCHAR(64),
  PRIMARY KEY (`id`)
);