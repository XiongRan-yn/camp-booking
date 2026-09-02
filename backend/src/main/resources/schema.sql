-- ============================================
-- Database Schema
-- ============================================

CREATE TABLE IF NOT EXISTS users (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    nickname       VARCHAR(50)  DEFAULT '',
    avatar         VARCHAR(500) DEFAULT '',
    phone          VARCHAR(20)  DEFAULT '',
    email          VARCHAR(100) DEFAULT '',
    points_balance INT          DEFAULT 0,
    role           VARCHAR(20)  DEFAULT 'user',
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(200)  NOT NULL,
    category      VARCHAR(20)   NOT NULL,
    sub_category  VARCHAR(50)   DEFAULT '',
    cover_image   VARCHAR(500)  DEFAULT '',
    images        TEXT          DEFAULT NULL,
    description   TEXT          DEFAULT NULL,
    tags          TEXT          DEFAULT NULL,
    min_price     DECIMAL(10,2) DEFAULT 0,
    max_price     DECIMAL(10,2) DEFAULT 0,
    stock         INT           DEFAULT 0,
    status        VARCHAR(10)   DEFAULT 'on',
    is_full       SMALLINT      DEFAULT 0,
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category, status);

CREATE TABLE IF NOT EXISTS product_specs (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id     BIGINT        NOT NULL,
    name           VARCHAR(100)  NOT NULL,
    price          DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2) DEFAULT NULL,
    stock          INT           DEFAULT 0,
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS orders (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no        VARCHAR(32)   NOT NULL UNIQUE,
    user_id         BIGINT        NOT NULL,
    product_id      BIGINT        NOT NULL,
    spec_id         BIGINT        NOT NULL,
    spec_name       VARCHAR(100)  NOT NULL,
    quantity        INT           DEFAULT 1,
    unit_price      DECIMAL(10,2) NOT NULL,
    total_price     DECIMAL(10,2) NOT NULL,
    coupon_id       BIGINT        DEFAULT NULL,
    coupon_discount DECIMAL(10,2) DEFAULT 0,
    points_discount DECIMAL(10,2) DEFAULT 0,
    actual_price    DECIMAL(10,2) NOT NULL,
    contact_name    VARCHAR(50)   NOT NULL,
    contact_phone   VARCHAR(20)   NOT NULL,
    remark          VARCHAR(500)  DEFAULT '',
    status          VARCHAR(20)   DEFAULT 'pending',
    paid_at         TIMESTAMP     DEFAULT NULL,
    completed_at    TIMESTAMP     DEFAULT NULL,
    created_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)    REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (spec_id)    REFERENCES product_specs(id)
);
CREATE INDEX IF NOT EXISTS idx_orders_user_status ON orders(user_id, status);

CREATE TABLE IF NOT EXISTS coupons (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(100)  NOT NULL,
    type            VARCHAR(20)   NOT NULL,
    discount_value  DECIMAL(10,2) NOT NULL,
    min_amount      DECIMAL(10,2) DEFAULT 0,
    total_count     INT           NOT NULL,
    received_count  INT           DEFAULT 0,
    valid_days      INT           NOT NULL,
    rules           TEXT          DEFAULT NULL,
    status          VARCHAR(10)   DEFAULT 'on',
    start_time      TIMESTAMP     NOT NULL,
    end_time        TIMESTAMP     NOT NULL
);

CREATE TABLE IF NOT EXISTS user_coupons (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    coupon_id    BIGINT       NOT NULL,
    coupon_title VARCHAR(100) NOT NULL,
    status       VARCHAR(20)  DEFAULT 'usable',
    received_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    used_at      TIMESTAMP    DEFAULT NULL,
    expire_at    TIMESTAMP    NOT NULL,
    FOREIGN KEY (user_id)   REFERENCES users(id),
    FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);
CREATE INDEX IF NOT EXISTS idx_uc_user_status ON user_coupons(user_id, status);

CREATE TABLE IF NOT EXISTS points_records (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    amount     INT         NOT NULL,
    type       VARCHAR(10) NOT NULL,
    source     VARCHAR(50) NOT NULL,
    remark     VARCHAR(200) DEFAULT '',
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS favorites (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    target_id   BIGINT      NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (user_id, target_id, target_type)
);

CREATE TABLE IF NOT EXISTS reviews (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    product_id BIGINT      NOT NULL,
    order_id   BIGINT      NOT NULL,
    rating     SMALLINT    NOT NULL,
    content    TEXT        DEFAULT NULL,
    images     TEXT        DEFAULT NULL,
    type       VARCHAR(20) DEFAULT 'hotel',
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)    REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (order_id)   REFERENCES orders(id)
);

CREATE TABLE IF NOT EXISTS travelers (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    name       VARCHAR(50) NOT NULL,
    phone      VARCHAR(20) DEFAULT '',
    id_card    VARCHAR(18) DEFAULT '',
    age        INT         DEFAULT NULL,
    gender     VARCHAR(10) DEFAULT NULL,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS redemption_codes (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(32)  NOT NULL UNIQUE,
    type       VARCHAR(20)  NOT NULL,
    code_value VARCHAR(200) NOT NULL,
    is_used    SMALLINT     DEFAULT 0,
    used_by    BIGINT       DEFAULT NULL,
    used_at    TIMESTAMP    DEFAULT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS feedbacks (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    content    VARCHAR(500) NOT NULL,
    email      VARCHAR(100) DEFAULT '',
    status     VARCHAR(20)  DEFAULT 'pending',
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
