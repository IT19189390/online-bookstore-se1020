INSERT IGNORE INTO inventory
  (item_id, item_type, isbn, book_title, quantity, reorder_level, unit_cost, last_updated, warehouse, shelf_code, platform, license_expiry)
VALUES
  ('INV001', 'PRINTED', '978-0-13-468599-1', 'Effective Java', 24, 10, 4850.00, '2026-09-20', 'Warehouse A', 'A-03', NULL, NULL),
  ('INV002', 'PRINTED', '978-0-596-00712-6', 'Head First Design Patterns', 6, 8, 5200.00, '2026-09-18', 'Warehouse A', 'A-07', NULL, NULL),
  ('INV003', 'PRINTED', '978-1-61729-757-1', 'Spring in Action', 0, 5, 6100.00, '2026-09-10', 'Warehouse B', 'B-02', NULL, NULL),
  ('INV004', 'PRINTED', '978-0-13-235088-4', 'Clean Code', 35, 12, 4400.00, '2026-09-22', 'Warehouse B', 'B-11', NULL, NULL),
  ('INV005', 'EBOOK', '978-0-13-235088-4', 'Clean Code', 15, 5, 3200.00, '2026-09-15', NULL, NULL, 'Kindle Store', '2027-06-30'),
  ('INV006', 'EBOOK', '978-0-13-595705-9', 'The Pragmatic Programmer', 3, 5, 3900.00, '2026-09-12', NULL, NULL, 'Google Play Books', '2026-10-31'),
  ('INV007', 'EBOOK', '978-0-13-475759-9', 'Refactoring', 10, 4, 4100.00, '2026-08-02', NULL, NULL, 'Kobo', '2026-08-31');
