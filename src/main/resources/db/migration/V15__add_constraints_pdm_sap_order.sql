ALTER TABLE pdm_sap_order
ADD CONSTRAINT uk_pdm_sap_order_date_part_no UNIQUE (접수일자, 품목번호);