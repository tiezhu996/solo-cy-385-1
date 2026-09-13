-- 与 database/init.sql 保持一致；后端启动时自动执行（IF NOT EXISTS，幂等），
-- 用于修复持久化数据卷中缺失的表，不影响已有表和数据。
CREATE TABLE IF NOT EXISTS baby (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL,
  birthday DATE NOT NULL,
  blood_type VARCHAR(10),
  initial_height DECIMAL(5,2),
  initial_weight DECIMAL(5,2)
);

CREATE TABLE IF NOT EXISTS growth_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  baby_id BIGINT NOT NULL,
  recorded_at DATE NOT NULL,
  height_cm DECIMAL(5,2),
  weight_kg DECIMAL(5,2),
  percentile VARCHAR(40)
);

CREATE TABLE IF NOT EXISTS vaccine_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  baby_id BIGINT NOT NULL,
  vaccine_name VARCHAR(120) NOT NULL,
  planned_date DATE NOT NULL,
  completed BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS food_recipe (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  month_age_min INT NOT NULL,
  month_age_max INT NOT NULL,
  name VARCHAR(120) NOT NULL,
  ingredients TEXT,
  steps TEXT,
  nutrition TEXT,
  allergens VARCHAR(160)
);
