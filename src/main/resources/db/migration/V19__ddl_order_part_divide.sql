CREATE TABLE order_part_divide
(
    item        varchar(50) not null,
    part_no     varchar(50) not null,
    site        varchar(10)  not null,
    etc         varchar(100) not null,
    fixed       varchar(10),
    st_date     date        not null,
    created_at  timestamp   not null,
    modified_at timestamp,
    primary key (part_no, st_date)
)