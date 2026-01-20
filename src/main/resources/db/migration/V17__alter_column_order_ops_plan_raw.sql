ALTER TABLE order_ops_plan_raw
ALTER COLUMN transmission TYPE varchar(50) USING transmission::varchar(50),
ALTER COLUMN engine_capa TYPE varchar(50) USING engine_capa::varchar(50);