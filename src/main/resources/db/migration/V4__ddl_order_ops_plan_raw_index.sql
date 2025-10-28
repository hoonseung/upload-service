CREATE INDEX idx_order_ops_plan_car_responder_st_date
    ON order_ops_plan_raw (responder, car_props, st_date);


ALTER TABLE order_ops_plan_raw
ALTER COLUMN engine_capa TYPE varchar(30);
