CREATE TABLE common_sales_price_unit
(
    part_no VARCHAR(50) NOT NULL,
    supplier VARCHAR(10) NOT NULL,
    part_name VARCHAR(100) NOT NULL,
    car_item VARCHAR(50) NOT NULL,
    product_code VARCHAR(10) NOT NULL,
    explain VARCHAR(10) NULL,
    car_props VARCHAR(50) NULL,
    group_props VARCHAR(50) NULL,
    unit VARCHAR(10) NOT NULL,
    price_unit VARCHAR(10) NOT NULL,
    price DECIMAL(20, 2) NOT NULL,
    to_date DATE NOT NULL,
    from_date DATE NOT NULL,
    create_date TIMESTAMP NOT NULL,
    PRIMARY KEY (part_no, price)
)