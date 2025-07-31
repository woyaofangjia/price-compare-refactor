-- 比价宝数据库初始化脚本
-- 数据库名：pricecompare
-- 仅作本地测试使用

-- 创建数据库
CREATE DATABASE IF NOT EXISTS pricecompare DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pricecompare;

-- 设置SQL模式以避免严格模式下的问题
SET sql_mode = '';

-- 用户表
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  isadmin TINYINT(1) DEFAULT 0 COMMENT '是否管理员 1=管理员 0=普通用户',
  avatar VARCHAR(255),
  status ENUM('active','banned') DEFAULT 'active' COMMENT '用户状态',
  activity INT DEFAULT 0 COMMENT '活跃度',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_username (username),
  INDEX idx_email (email)
);

-- 商品表
CREATE TABLE IF NOT EXISTS products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  `desc` TEXT,
  img VARCHAR(255),
  category VARCHAR(100),
  is_hot BOOLEAN DEFAULT FALSE,
  is_drop BOOLEAN DEFAULT FALSE,
  status TINYINT(1) DEFAULT 1 COMMENT '商品状态 1=上架 0=下架',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_category (category),
  INDEX idx_status (status),
  INDEX idx_is_hot (is_hot),
  INDEX idx_is_drop (is_drop)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品价格表
CREATE TABLE IF NOT EXISTS product_prices (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  platform VARCHAR(50) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_product_prices_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  INDEX idx_product_date (product_id, date),
  INDEX idx_platform (platform)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏表
CREATE TABLE IF NOT EXISTS favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  alert_price DECIMAL(10,2) DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_favorites_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  UNIQUE KEY unique_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户日志表
CREATE TABLE IF NOT EXISTS user_logs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  action ENUM('register', 'login', 'logout', 'ban') NOT NULL,
  status ENUM('success', 'fail') DEFAULT 'success',
  ip VARCHAR(64),
  user_agent VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 动态表
CREATE TABLE IF NOT EXISTS posts (
  id INT PRIMARY KEY AUTO_INCREMENT,
  content TEXT NOT NULL COMMENT '动态内容',
  images JSON COMMENT '图片URL数组',
  user_id BIGINT NOT NULL COMMENT '发布者用户ID',
  time DATETIME NOT NULL COMMENT '发布时间',
  tags JSON COMMENT '标签数组',
  location VARCHAR(255) COMMENT '位置信息',
  visibility ENUM('public', 'private', 'friends') DEFAULT 'public' COMMENT '可见性设置',
  product JSON COMMENT '关联商品信息',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_id (user_id),
  INDEX idx_created_at (created_at),
  INDEX idx_visibility (visibility),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态表';

-- 动态点赞表
CREATE TABLE IF NOT EXISTS post_likes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  post_id INT NOT NULL COMMENT '动态ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_post_user (post_id, user_id),
  INDEX idx_post_id (post_id),
  INDEX idx_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞表';

-- 动态收藏表
CREATE TABLE IF NOT EXISTS post_collections (
  id INT PRIMARY KEY AUTO_INCREMENT,
  post_id INT NOT NULL COMMENT '动态ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_post_user (post_id, user_id),
  INDEX idx_post_id (post_id),
  INDEX idx_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态收藏表';

-- 动态评论表
CREATE TABLE IF NOT EXISTS post_comments (
  id INT PRIMARY KEY AUTO_INCREMENT,
  post_id INT NOT NULL COMMENT '动态ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  content TEXT NOT NULL COMMENT '评论内容',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_post_id (post_id),
  INDEX idx_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态评论表';

-- 品牌表
CREATE TABLE IF NOT EXISTS brands (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE,
  logo VARCHAR(500),
  description TEXT,
  status INT NOT NULL DEFAULT 1,
  sort_weight INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_name (name),
  INDEX idx_status (status),
  INDEX idx_sort_weight (sort_weight)
);

-- 系统日志表
CREATE TABLE IF NOT EXISTS logs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  action VARCHAR(50) NOT NULL,
  description TEXT,
  path VARCHAR(500),
  method VARCHAR(10),
  ip VARCHAR(50),
  user_agent VARCHAR(500),
  request_params TEXT,
  response_status INT,
  execution_time BIGINT,
  error_message TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_id (user_id),
  INDEX idx_action (action),
  INDEX idx_created_at (created_at),
  INDEX idx_response_status (response_status),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 插入测试数据

-- 插入测试用户 (使用INSERT IGNORE避免重复用户名错误)
INSERT IGNORE INTO users (username, password, email) VALUES 
('testuser', '123456', 'test@example.com'),
('admin', 'admin123', 'admin@example.com');

-- 插入测试品牌
INSERT INTO brands (name, description) VALUES
('苹果', 'Apple Inc.'),
('华为', '华为技术有限公司'),
('小米', '小米科技有限责任公司'),
('三星', 'Samsung Electronics'),
('OPPO', 'OPPO广东移动通信有限公司');

-- 插入测试商品
INSERT INTO products (title, `desc`, img, category, is_hot, is_drop) VALUES 
('某品牌旗舰手机 8GB+256GB 全网通', '型号：SM-X9000 | 颜色：曜夜黑', 'https://via.placeholder.com/400x400?text=商品大图', '手机', TRUE, FALSE),
('某品牌轻薄笔记本 i7高配 16GB内存', '高性能轻薄本，适合办公和游戏', 'https://via.placeholder.com/400x400?text=笔记本', '电脑', TRUE, FALSE),
('智能健康手表 多功能 续航强', '血氧检测，50米防水', 'https://via.placeholder.com/400x400?text=手表', '数码', TRUE, TRUE),
('运动智能手环 血氧检测 50米防水', '运动健康监测', 'https://via.placeholder.com/400x400?text=手环', '数码', FALSE, TRUE),
('新一代游戏主机 4K高清 1TB存储', '4K游戏体验', 'https://via.placeholder.com/400x400?text=游戏主机', '游戏', FALSE, TRUE),
('高性能平板电脑 10.5英寸 128GB', '轻薄便携，性能强劲', 'https://via.placeholder.com/400x400?text=平板', '电脑', FALSE, TRUE);

-- 插入测试价格数据（最近30天的价格变化）
INSERT INTO product_prices (product_id, platform, price, date) VALUES 
-- 商品1的价格历史
(1, '京东', 3499.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(1, '京东', 3399.00, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(1, '京东', 3349.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(1, '京东', 3299.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(1, '京东', 3399.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(1, '京东', 3299.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(1, '京东', 3299.00, CURDATE()),

(1, '天猫', 3599.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(1, '天猫', 3499.00, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(1, '天猫', 3399.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(1, '天猫', 3399.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(1, '天猫', 3499.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(1, '天猫', 3399.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(1, '天猫', 3399.00, CURDATE()),

(1, '拼多多', 3299.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(1, '拼多多', 3199.00, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(1, '拼多多', 3099.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(1, '拼多多', 3199.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(1, '拼多多', 3099.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(1, '拼多多', 3199.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(1, '拼多多', 3199.00, CURDATE()),

(1, '苏宁', 3499.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(1, '苏宁', 3449.00, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(1, '苏宁', 3399.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(1, '苏宁', 3349.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(1, '苏宁', 3399.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(1, '苏宁', 3349.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(1, '苏宁', 3349.00, CURDATE()),

-- 商品2的价格历史
(2, '京东', 6499.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(2, '京东', 6499.00, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(2, '京东', 6499.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(2, '京东', 6499.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(2, '京东', 6499.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(2, '京东', 6499.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(2, '京东', 6499.00, CURDATE()),

(2, '苏宁', 6499.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(2, '苏宁', 6499.00, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(2, '苏宁', 6499.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(2, '苏宁', 6499.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(2, '苏宁', 6499.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(2, '苏宁', 6499.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(2, '苏宁', 6499.00, CURDATE()),

-- 商品3的价格历史
(3, '天猫', 899.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(3, '天猫', 899.00, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(3, '天猫', 899.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(3, '天猫', 899.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(3, '天猫', 899.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(3, '天猫', 899.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(3, '天猫', 899.00, CURDATE()),

(3, '拼多多', 899.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(3, '拼多多', 899.00, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(3, '拼多多', 899.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(3, '拼多多', 899.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(3, '拼多多', 899.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(3, '拼多多', 899.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(3, '拼多多', 899.00, CURDATE());

-- 插入测试收藏数据
INSERT INTO favorites (user_id, product_id, alert_price) VALUES 
(1, 1, 3000.00),
(1, 2, 6000.00),
(1, 3, 800.00);

-- 外键约束已在表定义中包含，无需重复添加