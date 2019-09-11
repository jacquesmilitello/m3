CREATE TABLE "sales_record" (
  id             bigint,
  region         VARCHAR(64),
  country        VARCHAR(32),
  item_type      VARCHAR(32),
  sales_channel  VARCHAR(32),
  order_priority VARCHAR(1),
  order_date     date,
  order_id       bigint,
  ship_date     date,
  unit_sold      double,
  unit_price     double,
  unit_cost      double,
  total_revenue  double,
  total_cost     double,
  total_profit   double,
  PRIMARY KEY ("id")
);

