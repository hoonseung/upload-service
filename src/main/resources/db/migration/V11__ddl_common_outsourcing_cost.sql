CREATE TABLE common_std_outsourcing_cost
(
    part_no                varchar(50),
    price_set              varchar(50),
    product_code           varchar(50),
    mat_cost               numeric(18, 4),
    labor_cost             numeric(18, 4),
    changing_cost          numeric(18, 4),
    fixed_cost             numeric(18, 4),
    out_sourcing_cost      numeric(18, 4),
    total_cost             numeric(18, 4),
    mat_cost_down          numeric(18, 4),
    labor_cost_down        numeric(18, 4),
    changing_cost_down     numeric(18, 4),
    fixed_cost_down        numeric(18, 4),
    out_sourcing_cost_down numeric(18, 4),
    cost_modify_date       date,
    groups                 varchar(50),
    groups2                varchar(50),
    car_props_type         varchar(50),
    car_props              varchar(50),
    freezing               varchar(50),
    created_at          date
);

CREATE TABLE common_outsourcing_cost
(
    price_list            varchar(50),
    name                  varchar(50),
    price_unit            varchar(50),
    product_code          varchar(50),
    part_no               varchar(50),
    groups                varchar(50),
    groups2               varchar(50),
    unit                  varchar(50),
    start_date            date,
    end_date              date,
    etc                   varchar(50),
    explanation           varchar(100),
    out_sourcing_cost     numeric(18,4),
    modify_date           date,
    factory               varchar(50),
    car_props_type        varchar(50),
    std_unit              varchar(50),
    created_at          date
);

