-- ============================================
-- Seed Data（幂等版：可重复执行，不会产生重复数据）
-- 1) 先清理历史重复行  2) 表为空时才插入种子数据
-- ============================================

-- 老库结构升级（幂等）：订单表补充 completed_at 列（H2 若已存在则跳过）
ALTER TABLE orders ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP DEFAULT NULL;

-- 清理历史重复数据（每组保留最早一条；已领取的优惠券不删）
DELETE FROM product_specs WHERE id NOT IN (SELECT MIN(id) FROM product_specs GROUP BY product_id, name);
DELETE FROM products WHERE id NOT IN (SELECT MIN(id) FROM products GROUP BY title);
DELETE FROM coupons WHERE id NOT IN (SELECT MIN(id) FROM coupons GROUP BY title)
    AND id NOT IN (SELECT coupon_id FROM user_coupons);

-- 用户（username 唯一约束保证不会重复）
INSERT INTO users (username, password, nickname, role) VALUES
('admin', '$2a$10$PQBc2RerCod1MnnLCRa8CuG8L1.ULV1UlGI1viiIaKUXCQjZGvhpu', 'admin', 'admin'),
('test', '$2a$10$PQBc2RerCod1MnnLCRa8CuG8L1.ULV1UlGI1viiIaKUXCQjZGvhpu', 'testuser', 'user');

-- 幂等修复：老库 admin/test 的密码 hash 是手工拼接的假值（任何密码都无法匹配），
-- 导致这两个种子账号永远登录失败。此处强制把密码重置为 123456，保证演示可登录。
UPDATE users SET password = '$2a$10$PQBc2RerCod1MnnLCRa8CuG8L1.ULV1UlGI1viiIaKUXCQjZGvhpu'
WHERE username IN ('admin', 'test');

-- 商品（表非空则跳过）
INSERT INTO products (title, category, sub_category, cover_image, min_price, max_price, stock, tags)
SELECT * FROM (
    SELECT '星空研学营地','camp','tech','https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=800&q=80&auto=format&fit=crop',299.00,499.00,50,'["亲子","6-12岁"]'
    UNION ALL SELECT '森林探险营地','camp','outdoor','https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800&q=80&auto=format&fit=crop',399.00,599.00,30,'["亲子","8-14岁"]'
    UNION ALL SELECT '科学探索营地','camp','science','https://images.unsplash.com/photo-1532094349884-543bc11b234d?w=800&q=80&auto=format&fit=crop',199.00,399.00,40,'["单飞","5-10岁"]'
    UNION ALL SELECT '文化传承营地','camp','culture','https://images.unsplash.com/photo-1526080652727-5b77f74eacd2?w=800&q=80&auto=format&fit=crop',259.00,459.00,35,'["亲子","7-15岁"]'
    UNION ALL SELECT '云岭山居民宿','hotel','mountain','https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&q=80&auto=format&fit=crop',388.00,688.00,20,'["山景","大床房"]'
    UNION ALL SELECT '湖畔观景民宿','hotel','lake','https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800&q=80&auto=format&fit=crop',428.00,728.00,15,'["湖景","家庭房"]'
    UNION ALL SELECT '古镇风情民宿','hotel','town','https://images.unsplash.com/photo-1528127269322-539801943592?w=800&q=80&auto=format&fit=crop',288.00,488.00,25,'["古镇","标准间"]'
    UNION ALL SELECT '温泉度假民宿','hotel','spring','https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=800&q=80&auto=format&fit=crop',588.00,888.00,10,'["温泉","豪华房"]'
) seed
WHERE NOT EXISTS (SELECT 1 FROM products);

-- 幂等补图：老库已有商品时，按 title 补上封面图（避免商品无图）
UPDATE products SET cover_image = 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=800&q=80&auto=format&fit=crop' WHERE title = '星空研学营地';
UPDATE products SET cover_image = 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800&q=80&auto=format&fit=crop' WHERE title = '森林探险营地';
UPDATE products SET cover_image = 'https://images.unsplash.com/photo-1532094349884-543bc11b234d?w=800&q=80&auto=format&fit=crop' WHERE title = '科学探索营地';
UPDATE products SET cover_image = 'https://images.unsplash.com/photo-1526080652727-5b77f74eacd2?w=800&q=80&auto=format&fit=crop' WHERE title = '文化传承营地';
UPDATE products SET cover_image = 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&q=80&auto=format&fit=crop' WHERE title = '云岭山居民宿';
UPDATE products SET cover_image = 'https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800&q=80&auto=format&fit=crop' WHERE title = '湖畔观景民宿';
UPDATE products SET cover_image = 'https://images.unsplash.com/photo-1528127269322-539801943592?w=800&q=80&auto=format&fit=crop' WHERE title = '古镇风情民宿';
UPDATE products SET cover_image = 'https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=800&q=80&auto=format&fit=crop' WHERE title = '温泉度假民宿';

-- 商品规格（表非空则跳过）
INSERT INTO product_specs (product_id, name, price, original_price, stock)
SELECT * FROM (
    SELECT 1,'标准班',499.00,599.00,30
    UNION ALL SELECT 1,'早鸟价',299.00,NULL,20
    UNION ALL SELECT 2,'标准班',599.00,NULL,20
    UNION ALL SELECT 2,'早鸟价',399.00,499.00,10
    UNION ALL SELECT 3,'标准班',399.00,NULL,25
    UNION ALL SELECT 3,'团购价',199.00,299.00,15
    UNION ALL SELECT 4,'标准班',459.00,NULL,20
    UNION ALL SELECT 4,'早鸟价',259.00,359.00,15
    UNION ALL SELECT 5,'大床房',688.00,NULL,10
    UNION ALL SELECT 5,'经济房',388.00,488.00,10
    UNION ALL SELECT 6,'家庭房',728.00,NULL,8
    UNION ALL SELECT 6,'标准房',428.00,528.00,7
    UNION ALL SELECT 7,'标准房',488.00,NULL,15
    UNION ALL SELECT 7,'经济房',288.00,388.00,10
    UNION ALL SELECT 8,'豪华房',888.00,NULL,5
    UNION ALL SELECT 8,'标准房',588.00,688.00,5
) seed
WHERE NOT EXISTS (SELECT 1 FROM product_specs);

-- 优惠券（表非空则跳过）
INSERT INTO coupons (title, type, discount_value, min_amount, total_count, valid_days, start_time, end_time)
SELECT * FROM (
    SELECT '新人专享券','cash',50.00,200.00,500,7,'2026-08-01 00:00:00','2026-12-31 23:59:59'
    UNION ALL SELECT '满300减30','cash',30.00,300.00,300,14,'2026-08-01 00:00:00','2026-12-31 23:59:59'
    UNION ALL SELECT '营地满500减100','cash',100.00,500.00,200,7,'2026-08-01 00:00:00','2026-12-31 23:59:59'
    UNION ALL SELECT '民宿8.5折券','discount',0.85,0,100,30,'2026-08-01 00:00:00','2026-12-31 23:59:59'
    UNION ALL SELECT '积分礼券','gift',100.00,0,200,30,'2026-08-01 00:00:00','2026-12-31 23:59:59'
) seed
WHERE NOT EXISTS (SELECT 1 FROM coupons);

-- 兑换码：每次重启重置（清除已使用记录，确保演示时兑换码始终可用）
DELETE FROM redemption_codes;
INSERT INTO redemption_codes (code, type, code_value) VALUES
('WELCOME2024', 'coupon', '1'),
('SUMMER2024', 'points', '200'),
('CAMP2024', 'coupon', '3'),
('FREEROOM', 'free_room', '5');

-- ===== 老库数据升级（幂等）：英文名称改为中文 =====
UPDATE products SET title='星空研学营地', tags='["亲子","6-12岁"]' WHERE id=1 AND title='study camp 1';
UPDATE products SET title='森林探险营地', tags='["亲子","8-14岁"]' WHERE id=2 AND title='outdoor camp';
UPDATE products SET title='科学探索营地', tags='["单飞","5-10岁"]' WHERE id=3 AND title='science camp';
UPDATE products SET title='文化传承营地', tags='["亲子","7-15岁"]' WHERE id=4 AND title='culture camp';
UPDATE products SET title='云岭山居民宿', tags='["山景","大床房"]' WHERE id=5 AND title='mountain hotel';
UPDATE products SET title='湖畔观景民宿', tags='["湖景","家庭房"]' WHERE id=6 AND title='lake hotel';
UPDATE products SET title='古镇风情民宿', tags='["古镇","标准间"]' WHERE id=7 AND title='old town inn';
UPDATE products SET title='温泉度假民宿', tags='["温泉","豪华房"]' WHERE id=8 AND title='hot spring hotel';

UPDATE product_specs SET name='标准班' WHERE product_id=1 AND name='standard';
UPDATE product_specs SET name='早鸟价' WHERE product_id=1 AND name='early bird';
UPDATE product_specs SET name='标准班' WHERE product_id=2 AND name='standard';
UPDATE product_specs SET name='早鸟价' WHERE product_id=2 AND name='early bird';
UPDATE product_specs SET name='标准班' WHERE product_id=3 AND name='standard';
UPDATE product_specs SET name='团购价' WHERE product_id=3 AND name='group';
UPDATE product_specs SET name='标准班' WHERE product_id=4 AND name='standard';
UPDATE product_specs SET name='早鸟价' WHERE product_id=4 AND name='early bird';
UPDATE product_specs SET name='大床房' WHERE product_id=5 AND name='big bed';
UPDATE product_specs SET name='经济房' WHERE product_id=5 AND name='economy';
UPDATE product_specs SET name='家庭房' WHERE product_id=6 AND name='family room';
UPDATE product_specs SET name='标准房' WHERE product_id=6 AND name='standard';
UPDATE product_specs SET name='标准房' WHERE product_id=7 AND name='standard';
UPDATE product_specs SET name='经济房' WHERE product_id=7 AND name='economy';
UPDATE product_specs SET name='豪华房' WHERE product_id=8 AND name='luxury';
UPDATE product_specs SET name='标准房' WHERE product_id=8 AND name='standard';

UPDATE coupons SET title='新人专享券' WHERE title='new user coupon';
UPDATE coupons SET title='满300减30' WHERE title='discount 30';
UPDATE coupons SET title='营地满500减100' WHERE title='camp coupon';
UPDATE coupons SET title='民宿8.5折券' WHERE title='hotel discount';
UPDATE coupons SET title='积分礼券' WHERE title='points bonus';
