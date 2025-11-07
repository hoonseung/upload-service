DROP INDEX IF EXISTS idx_order_ops_plan_car_responder_st_date;
CREATE INDEX idx_order_ops_plan_car_responder_st_date_car_props
    ON order_ops_plan_raw(responder, st_date, car_props);

CREATE INDEX idx_order_ops_plan_st_date
    ON order_ops_plan_raw(st_date);


