-- 贷款小程序数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS credit_miniapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE credit_miniapp;

-- 银行表
CREATE TABLE IF NOT EXISTS bank (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '银行名称',
    logo_url VARCHAR(255) COMMENT '银行Logo URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='银行表';

-- 产品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bank_id BIGINT NOT NULL COMMENT '银行ID',
    name VARCHAR(100) NOT NULL COMMENT '产品名称',
    amount_min DECIMAL(15,2) COMMENT '最小额度',
    amount_max DECIMAL(15,2) COMMENT '最大额度',
    rate_min DECIMAL(5,2) COMMENT '最低利率',
    rate_max DECIMAL(5,2) COMMENT '最高利率',
    tags VARCHAR(255) COMMENT '标签，逗号分隔',
    description TEXT COMMENT '产品描述',
    requirements TEXT COMMENT '申请条件',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bank_id) REFERENCES bank(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='贷款产品表';

-- 顾问表
CREATE TABLE IF NOT EXISTS consultant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '顾问姓名',
    phone_encrypted VARCHAR(255) NOT NULL COMMENT '手机号（加密存储）',
    bank_id BIGINT COMMENT '所属银行ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bank_id) REFERENCES bank(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='顾问表';

-- 用户收藏表
CREATE TABLE IF NOT EXISTS user_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(100) NOT NULL COMMENT '用户openid',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_openid_product (openid, product_id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 用户浏览历史表
CREATE TABLE IF NOT EXISTS user_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(100) NOT NULL COMMENT '用户openid',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_openid_created (openid, created_at),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';

-- 意见反馈表
CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(100) COMMENT '用户openid',
    content TEXT NOT NULL COMMENT '反馈内容',
    contact VARCHAR(100) COMMENT '联系方式',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未处理，1-已处理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意见反馈表';

-- 管理员用户表
CREATE TABLE IF NOT EXISTS admin_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- 插入初始数据
INSERT INTO bank (name, logo_url, status) VALUES
('中国银行', '/images/banks/boc.png', 1),
('工商银行', '/images/banks/icbc.png', 1),
('建设银行', '/images/banks/ccb.png', 1),
('农业银行', '/images/banks/abc.png', 1),
('招商银行', '/images/banks/cmb.png', 1);

INSERT INTO product (bank_id, name, amount_min, amount_max, rate_min, rate_max, tags, description, requirements, status) VALUES
(1, '信用贷款', 50000, 500000, 3.85, 6.25, '审批快,无抵押', '纯信用贷款，无需抵押担保，审批快速', '年龄22-55周岁，信用记录良好', 1),
(1, '抵押贷款', 100000, 5000000, 3.65, 5.39, '额度高,利率低', '房产抵押贷款，额度高利率低', '有房产可抵押，年龄18-65周岁', 1),
(2, '工银信用贷', 30000, 300000, 4.35, 7.20, '线上申请,秒批', '工商银行信用贷款，全线上申请', '工商银行代发工资客户，信用良好', 1),
(3, '快贷', 10000, 200000, 4.10, 5.60, '秒到账,随借随还', '建设银行信用贷款，快速到账', '建设银行存量客户，信用记录良好', 1),
(4, '助业贷款', 50000, 1000000, 3.90, 6.15, '经营贷,循环贷', '农业银行经营性贷款', '有经营实体，流水充足', 1),
(5, '闪电贷', 20000, 500000, 3.78, 6.50, '全程线上,30秒放款', '招商银行纯信用贷款', '招商银行信用卡客户', 1);

INSERT INTO admin_user (username, password, real_name, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1);

-- 注意：上面密码是 admin123 的BCrypt加密结果
-- 默认账号: admin / admin123
