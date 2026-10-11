CREATE TABLE IF NOT EXISTS inventory (
  item_id        VARCHAR(10)   NOT NULL,
  item_type      VARCHAR(10)   NOT NULL,
  isbn           VARCHAR(17)   NOT NULL,
  book_title     VARCHAR(150)  NOT NULL,
  quantity       INT           NOT NULL,
  reorder_level  INT           NOT NULL,
  unit_cost      DECIMAL(10,2) NOT NULL,
  last_updated   DATE          NOT NULL,
  warehouse      VARCHAR(40),
  shelf_code     VARCHAR(20),
  platform       VARCHAR(40),
  license_expiry DATE,
  PRIMARY KEY (item_id),
  CONSTRAINT chk_item_type CHECK (item_type IN ('PRINTED', 'EBOOK'))
);
