DROP TABLE IF EXISTS order_second_outbound;

CREATE TABLE order_outbound_target_stock
(
    item_code   varchar(255),
    em_qty      INT,
    first_qty   INT,
    second_qty  INT,
    d3_qty      INT,
    upload_date DATE,
    created_at  timestamp not null,
    modified_at timestamp,
    PRIMARY KEY (item_code, upload_date)
)