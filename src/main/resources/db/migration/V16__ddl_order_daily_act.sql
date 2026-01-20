CREATE TABLE order_daily_act
(
    plan_date   VARCHAR(15) NOT NULL ,
    part_no     VARCHAR(30) NOT NULL ,
    actual_qty  INT,
    created_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (plan_date, part_no)
);

CREATE INDEX idx_order_daily_act_part_no ON order_daily_act (part_no);


