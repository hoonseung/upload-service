ALTER TABLE order_ops_plan_last_mon_agg
ADD COLUMN confirm VARCHAR(10);

ALTER TABLE order_ops_plan_past_mon_agg
ADD COLUMN confirm VARCHAR(10);