-- Seed Data
INSERT INTO users (username, password, nickname, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'admin', 'admin'),
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'testuser', 'user');

INSERT INTO products (title, category, sub_category, min_price, max_price, stock, tags) VALUES
('study camp 1', 'camp', 'tech', 299.00, 499.00, 50, '["kids","6-12"]'),
('outdoor camp', 'camp', 'outdoor', 399.00, 599.00, 30, '["kids","8-14"]'),
('science camp', 'camp', 'science', 199.00, 399.00, 40, '["solo","5-10"]'),
('culture camp', 'camp', 'culture', 259.00, 459.00, 35, '["kids","7-15"]'),
('mountain hotel', 'hotel', 'mountain', 388.00, 688.00, 20, '["mountain","big bed"]'),
('lake hotel', 'hotel', 'lake', 428.00, 728.00, 15, '["lake","family"]'),
('old town inn', 'hotel', 'town', 288.00, 488.00, 25, '["town","standard"]'),
('hot spring hotel', 'hotel', 'spring', 588.00, 888.00, 10, '["spring","luxury"]');

INSERT INTO product_specs (product_id, name, price, original_price, stock) VALUES
(1, 'standard', 499.00, 599.00, 30),(1, 'early bird', 299.00, NULL, 20),
(2, 'standard', 599.00, NULL, 20),(2, 'early bird', 399.00, 499.00, 10),
(3, 'standard', 399.00, NULL, 25),(3, 'group', 199.00, 299.00, 15),
(4, 'standard', 459.00, NULL, 20),(4, 'early bird', 259.00, 359.00, 15),
(5, 'big bed', 688.00, NULL, 10),(5, 'economy', 388.00, 488.00, 10),
(6, 'family room', 728.00, NULL, 8),(6, 'standard', 428.00, 528.00, 7),
(7, 'standard', 488.00, NULL, 15),(7, 'economy', 288.00, 388.00, 10),
(8, 'luxury', 888.00, NULL, 5),(8, 'standard', 588.00, 688.00, 5);

INSERT INTO coupons (title, type, discount_value, min_amount, total_count, valid_days, start_time, end_time) VALUES
('new user coupon', 'cash', 50.00, 200.00, 500, 7, '2026-08-01 00:00:00', '2026-12-31 23:59:59'),
('discount 30', 'cash', 30.00, 300.00, 300, 14, '2026-08-01 00:00:00', '2026-12-31 23:59:59'),
('camp coupon', 'cash', 100.00, 500.00, 200, 7, '2026-08-01 00:00:00', '2026-12-31 23:59:59'),
('hotel discount', 'discount', 0.85, 0, 100, 30, '2026-08-01 00:00:00', '2026-12-31 23:59:59'),
('points bonus', 'gift', 100.00, 0, 200, 30, '2026-08-01 00:00:00', '2026-12-31 23:59:59');

INSERT INTO redemption_codes (code, type, code_value) VALUES
('WELCOME2024', 'coupon', '1'),
('SUMMER2024', 'points', '200'),
('CAMP2024', 'coupon', '3'),
('FREEROOM', 'free_room', '5');
