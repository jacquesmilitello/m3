CREATE TABLE `m3_event_store` (
  `id`                bigint,
  `aggregate_type`    VARCHAR(64),
  `aggregate_id`      VARCHAR(64),
  `trace_id`          CHAR(16),
  `created_at`        TIMESTAMP,
  `event`             VARCHAR(4000),
  PRIMARY KEY (`id`)
);

CREATE SEQUENCE seq__m3_event_store;

CREATE INDEX `idx__m3_event_store__bk` ON `m3_event_store`(`aggregate_type`, `aggregate_id`);

