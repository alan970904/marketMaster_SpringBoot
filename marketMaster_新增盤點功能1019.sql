--創建表格
use ispan;

-- 創建商品表
CREATE TABLE products (
    product_id VARCHAR(30) NOT NULL PRIMARY KEY, -- 商品編號，編號開頭用P
    product_category VARCHAR(30) NOT NULL, -- 商品類別名稱
    product_name NVARCHAR(30) NOT NULL, -- 商品名稱
    product_price INT NOT NULL, -- 商品售價
    product_safeinventory INT NOT NULL, -- 安全庫存量
    Number_of_shelve INT NOT NULL, -- 上架數量
    Number_of_inventory INT NOT NULL, -- 庫存數量
    Number_of_sale INT NOT NULL, -- 銷售數量
    Number_of_exchange INT NOT NULL, -- 兌換數量
    Number_of_destruction INT NOT NULL, -- 銷毀數量
    Number_of_remove INT NOT NULL, -- 下架數量
	product_available BIT NOT NULL, -- 是否 1 商品在販售中 0商品已下架
	is_perishable BIT NOT NULL, --是否 為生鮮食品 0表示非生鮮食品 1表示生鮮食品
	product_photo VARBINARY(MAX) -- 商品圖片
);

--創建盤點表
CREATE TABLE inventory_check (
    inventory_check_id VARCHAR(30) NOT NULL PRIMARY KEY, -- 盤點編號
	employee_id VARCHAR(30) NOT NULL, --負責員工ID，外鍵，指向
	inventory_check_date DATE NOT NULL, --盤點日期 
);

--創建盤點明細
CREATE TABLE inventory_check_details (
	detail_id VARCHAR(30) NOT NULL PRIMARY KEY, --盤點明細編號
    inventory_check_id VARCHAR(30) NOT NULL, -- 盤點編號 外鍵
	product_id VARCHAR(30) NOT NULL, --商品編號 外鍵
	current_inventory INT  NOT NULL, --當前庫存數量
	actual_inventory INT NOT NULL, --盤點後實際數量
	differential_inventory INT  NOT NULL, --庫存數量差異
    remark NVARCHAR(255) --備註 商品異動原因
);

-- 創建員工表
CREATE TABLE employee (
    employee_id VARCHAR(30) NOT NULL PRIMARY KEY, -- 員工編號開頭用E
    employee_name NVARCHAR(30) NOT NULL, -- 員工姓名
    employee_tel VARCHAR(30) NOT NULL, -- 員工手機
    employee_idcard VARCHAR(30) NOT NULL, -- 員工身分證號碼
    employee_email VARCHAR(30) NOT NULL, -- 員工email
    password VARCHAR(60) NOT NULL, -- 員工密碼
    position_id VARCHAR(30) NOT NULL, -- 職位編號
    hiredate DATE NOT NULL, -- 入職日
    resigndate DATE, -- 離職日
    is_first_login BIT NOT NULL DEFAULT 1, -- 判斷員工登入，表示尚未修改密碼
    image_path NVARCHAR(255) -- 圖片路徑欄位
);

-- 創建消息通知
CREATE TABLE notification (
    id INT IDENTITY(1,1) PRIMARY KEY,
    employee_id VARCHAR(30) NOT NULL,
    message NVARCHAR(500) NOT NULL,
	notification_type VARCHAR(30),
    is_read BIT NOT NULL,
	created_at DATETIME2 NOT NULL,
);

-- 創建聊天訊息
CREATE TABLE chat_messages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    from_user VARCHAR(30) NOT NULL,
    to_user VARCHAR(30) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    timestamp DATETIME2 NOT NULL
);

-- 建立供應商表，存儲供應商的基本信息
CREATE TABLE suppliers (
	supplier_id VARCHAR(30) NOT NULL,       -- 供應商ID，主鍵
	supplier_name NVARCHAR(50) NOT NULL,     -- 供應商名稱
	address NVARCHAR(50) NOT NULL,           -- 供應商地址
	tax_number VARCHAR(30) NOT NULL,        -- 供應商稅號
	contact_person NVARCHAR(30) NOT NULL,    -- 聯絡人
	phone VARCHAR(30) NOT NULL,             -- 聯絡電話
	email VARCHAR(30) NOT NULL,             -- 聯絡電子郵件
	bank_account VARCHAR(30),               -- 銀行帳號
	PRIMARY KEY (supplier_id)               -- 設定主鍵為 supplier_id，唯一標識供應商
);

-- 建立供應商商品表，記錄供應商提供的商品及其價格
CREATE TABLE supplier_products (
	supplier_product_id VARCHAR(30) NOT NULL, -- 供應商商品ID，主鍵
	supplier_id VARCHAR(30) NOT NULL,         -- 供應商ID，外鍵，指向 suppliers 表
	product_id VARCHAR(30) NOT NULL,          -- 商品ID，外鍵，指向 products 表
	product_price INT NOT NULL,               -- 商品價格，由供應商設定
	status INT NOT NULL,                      -- 商品狀態（0: 不可用，1: 可用）
	PRIMARY KEY (supplier_product_id),        -- 設定主鍵為 supplier_product_id
	--FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id), -- 建立與 suppliers 表的外鍵關聯
	--FOREIGN KEY (product_id) REFERENCES products(product_id)     -- 建立與 products 表的外鍵關聯
);

-- 建立供應商帳戶表，記錄供應商的帳戶狀況
CREATE TABLE supplier_accounts (
	account_id VARCHAR(30) NOT NULL,          -- 帳戶ID，主鍵
	supplier_id VARCHAR(30) NOT NULL,         -- 供應商ID，外鍵，指向 suppliers 表
	total_amount INT NOT NULL,                -- 供應商總應付金額
	paid_amount INT NOT NULL,                 -- 供應商已付款金額
	unpaid_amount INT NOT NULL,               -- 供應商未付款金額
	PRIMARY KEY (account_id),                 -- 設定主鍵為 account_id
	--FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) -- 建立與 suppliers 表的外鍵關聯
);

-- 建立進貨表，記錄進貨的基本信息
CREATE TABLE restocks (
	restock_id VARCHAR(30) NOT NULL,          -- 進貨ID，主鍵
	restock_total_price INT NOT NULL,         -- 進貨總價格
	restock_date DATE NOT NULL,               -- 進貨日期
	employee_id VARCHAR(30),                  -- 員工ID，外鍵，指向 employee 表
	PRIMARY KEY (restock_id),                 -- 設定主鍵為 restock_id
	--FOREIGN KEY (employee_id) REFERENCES employee(employee_id) -- 建立與 employee 表的外鍵關聯
);

-- 建立進貨明細表，記錄每次進貨中包含的商品詳情，直接加入 supplier_id 欄位
CREATE TABLE restock_details (
	detail_id VARCHAR(30) NOT NULL,           -- 明細ID，主鍵
	restock_id VARCHAR(30) NOT NULL,          -- 進貨ID，外鍵，指向 restocks 表
	supplier_id VARCHAR(30) NOT NULL,         -- 供應商ID，外鍵，指向 suppliers 表
	supplier_product_id VARCHAR(30) NOT NULL, -- 供應商商品ID，外鍵，指向 supplier_products 表
	number_of_restock INT NOT NULL,           -- 進貨商品數量
	price_at_restock INT NOT NULL,            -- 進貨時的價格
	restock_total_price INT NOT NULL,         -- 進貨商品總價格
	production_date DATE NOT NULL,            -- 商品的生產日期
	due_date DATE NOT NULL,                   -- 商品的到期日期
	restock_date DATE NOT NULL,               -- 商品的進貨日期
	PRIMARY KEY (detail_id),                  -- 設定主鍵為 detail_id
	--FOREIGN KEY (restock_id) REFERENCES restocks(restock_id), -- 建立與 restocks 表的外鍵關聯
	--FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id), -- 建立與 suppliers 表的外鍵關聯
	--FOREIGN KEY (supplier_product_id) REFERENCES supplier_products(supplier_product_id) -- 建立與 supplier_products 表的外鍵關聯
);

-- 建立付款表，記錄支付的相關資訊
CREATE TABLE payments (
	payment_id VARCHAR(30) NOT NULL,          -- 付款ID，主鍵
	account_id VARCHAR(30) NOT NULL,          -- 帳戶ID，外鍵，指向 supplier_accounts 表
	payment_date DATE NOT NULL,               -- 付款日期
	payment_method NVARCHAR(30) NOT NULL,      -- 支付方式
	total_amount INT NOT NULL,                -- 付款總額
	payment_status NVARCHAR(30) NOT NULL,      -- 支付狀態
	PRIMARY KEY (payment_id),                 -- 設定主鍵為 payment_id
	--FOREIGN KEY (account_id) REFERENCES supplier_accounts(account_id) -- 建立與 supplier_accounts 表的外鍵關聯
);

-- 建立付款記錄表，記錄每次付款的細節
CREATE TABLE payment_records (
	record_id VARCHAR(30) NOT NULL,           -- 記錄ID，主鍵
	payment_id VARCHAR(30) NOT NULL,          -- 付款ID，外鍵，指向 payments 表
	detail_id VARCHAR(30) NOT NULL,          -- 進貨ID，外鍵，指向 restocks 表
	payment_amount INT NOT NULL,              -- 付款金額
	PRIMARY KEY (record_id),                  -- 設定主鍵為 record_id
	--FOREIGN KEY (payment_id) REFERENCES payments(payment_id),   -- 建立與 payments 表的外鍵關聯
	--FOREIGN KEY (detail_id) REFERENCES restock_details(detail_id)    -- 建立與 restocks 表的外鍵關聯
);

-- 創建退貨明細表
CREATE TABLE return_details (
    return_id VARCHAR(30) NOT NULL, -- 退貨編號
    original_checkout_id VARCHAR(30) NOT NULL, -- 原始結帳編號
    product_id VARCHAR(30) NOT NULL, -- 商品編號
    reason_for_return NVARCHAR(30) NOT NULL, -- 退貨原因
    number_of_return INT NOT NULL, -- 商品數量
	product_price INT NOT NULL, -- 商品價格
    return_price INT NOT NULL, -- 小計
	return_status NVARCHAR(30) NOT NULL, -- 退貨商品狀態
    return_photo NVARCHAR(255), -- 退貨商品照片
	CONSTRAINT PK_return_details PRIMARY KEY (return_id, original_checkout_id, product_id),
	CONSTRAINT CHK_return_status 
        CHECK (return_status IN (N'顧客因素', N'商品外觀損傷', N'商品品質異常'))
);

-- 創建退貨表
CREATE TABLE return_products (
    return_id VARCHAR(30) NOT NULL PRIMARY KEY, -- 退貨編號，編號開頭用T
	original_checkout_id VARCHAR(30) NOT NULL, -- 原始結帳編號
	original_invoice_number VARCHAR(30) NOT NULL, -- 原始發票號碼，編號用IN開頭，後面接8號碼 
    employee_id VARCHAR(30) NOT NULL, -- 員工編號
    return_total_price INT NOT NULL, -- 退貨金額
    return_date DATE NOT NULL, -- 退貨日期
);

-- 創建結帳明細表
CREATE TABLE checkout_details (
    checkout_id VARCHAR(30) NOT NULL, -- 結帳編號
    product_id VARCHAR(30) NOT NULL, -- 商品編號
    number_of_checkout INT NOT NULL, -- 商品數量
    product_price INT NOT NULL, -- 商品價格
    checkout_price INT NOT NULL, -- 小計
	CONSTRAINT checkout_details_checkout_id_product_id_PK PRIMARY KEY(checkout_id,product_id)
);

-- 創建結帳表
CREATE TABLE checkout (
    checkout_id VARCHAR(30) NOT NULL PRIMARY KEY, -- 結帳編號，編號開頭用C
	invoice_number VARCHAR(30) NOT NULL, -- 發票號碼，編號用IN開頭，後面接8號碼
	customer_tel VARCHAR(30), -- 顧客手機
    employee_id VARCHAR(30) NOT NULL, -- 員工編號
    checkout_total_price INT NOT NULL, -- 結帳金額
    checkout_date DATE NOT NULL, -- 結帳日
	bonus_points INT , -- 紅利點數
    points_due_date DATE, -- 紅利點數到期日
	checkout_status NVARCHAR(10) NOT NULL, -- 結帳單狀態(正常,已退貨)
	related_return_id VARCHAR(30), -- 對應退貨編號
	CONSTRAINT CHK_checkout_status CHECK (checkout_status IN (N'正常', N'已退貨'))
);

-- 創建職級表
CREATE TABLE ranklevel (
    position_id VARCHAR(30) NOT NULL PRIMARY KEY, -- 職位編號開頭M
	position_name NVARCHAR(30) NOT NULL,	--職位名稱
    limits_of_authority INT NOT NULL, -- 權限
    salary_level VARCHAR(30) NOT NULL -- 薪資級數
);

-- 創建排班表
CREATE TABLE schedule (
    schedule_id INT IDENTITY(1,1) PRIMARY KEY,
    employee_id VARCHAR(30) NOT NULL, -- 員工編號
    start_time DATETIME2 NOT NULL, -- 開始時間
    end_time DATETIME2 NOT NULL -- 結束時間
);

-- 創建請假表
CREATE TABLE ask_for_leave (
    leave_id VARCHAR(30) NOT NULL PRIMARY KEY,  -- 請假編號
    employee_id VARCHAR(30) NOT NULL,          -- 員工編號
    start_time DATETIME2 NOT NULL,             -- 請假開始時間
    end_time DATETIME2 NOT NULL,               -- 請假結束時間
    reason_leave NVARCHAR(255),                -- 請假原因
    proof_image VARBINARY(MAX),                -- 請假證明
    approved_status NVARCHAR(30) NOT NULL,     -- 批准狀態
    category_id INT NOT NULL,                  -- 請假類別ID
    record_id INT ,                            -- 請假記錄ID
	rejection_reason NVARCHAR(255)	           -- 駁回原因
);

-- 創建請假額度表
CREATE TABLE leave_category (
    category_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,  -- id
    leave_type NVARCHAR(50) NOT NULL,                    -- 請假類別
    limit_hours INT NOT NULL                             -- 額度時數
);

-- 創建請假記錄表
CREATE TABLE leave_record (
    record_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,  -- id
    employee_id VARCHAR(30) NOT NULL,                  -- 員工編號
    category_id INT NOT NULL,                          -- 請假類別ID
    year INT NOT NULL,                                 -- 年份
    actual_hours INT NULL DEFAULT 0                    -- 實際時數，預設為 0
);

-- 創建會員表
CREATE TABLE customer (
    customer_tel VARCHAR(30) NOT NULL PRIMARY KEY, -- 顧客手機
    customer_name VARCHAR(30) NOT NULL, -- 顧客姓名
    customer_email VARCHAR(30) NOT NULL, -- email
    date_of_registration DATE NOT NULL, -- 註冊日期
    total_points INT  NOT NULL -- 累積紅利點數 --Create時先設定為0
);

-- 創建紅利兌換商品表
CREATE TABLE bonus_exchange (
    exchange_id VARCHAR(30) NOT NULL PRIMARY KEY, -- 兌換編號，編號開頭用H
    product_id VARCHAR(30) NOT NULL, -- 商品編號REFERENCES products(product_id)
	customer_tel VARCHAR(30) NOT NULL, -- 會員手機REFERENCES customer(customer_tel)
    use_points INT NOT NULL, -- 使用紅利點數
    number_of_exchange INT NOT NULL, -- 兌換數量
    exchange_date DATE NOT NULL, -- 兌換日期
);

--創建紅利紀錄表
CREATE TABLE points_history (
    points_history_id INT IDENTITY(1,1) PRIMARY KEY, -- 紅利編號
    customer_tel VARCHAR(30) NOT NULL, -- 會員手機REFERENCES customer(customer_tel)
    checkout_id VARCHAR(30), -- 結帳編號REFERENCES checkout(checkout_id)
    exchange_id VARCHAR(30), -- 兌換編號REFERENCES bonus_exchange(exchange_id)
    points_change INT NOT NULL, -- 紅利點數變動量
    transaction_date DATE NOT NULL, -- 紅利記錄日期
    transaction_type VARCHAR(10) NOT NULL CHECK (transaction_type IN ('earn', 'use', 'expire')) -- 紅利狀態
);

--======================================================================
--新增資料

-- 插入商品資料
INSERT INTO products (product_id, product_category, product_name, product_price, product_safeinventory, Number_of_shelve, Number_of_inventory, Number_of_sale, Number_of_exchange, Number_of_destruction, Number_of_remove,product_available,is_perishable)
VALUES
('PMS001', '肉品海鮮', '豬肉', 200, 60, 20, 120, 26, 9, 5, 0,1,1),
('PMS002', '肉品海鮮', '牛肉', 200, 60, 20, 120, 90, 26, 1, 0,1,1),
('PMS003', '肉品海鮮', '雞肉', 200, 60, 20, 120, 15, 0, 4, 0,1,1),
('PMS004', '肉品海鮮', '蛤蠣', 200, 60, 20, 120, 39, 16, 5, 0,1,1),
('PMS005', '肉品海鮮', '魚', 200, 60, 20, 120, 71, 5, 0, 0,1,1),
('PVF001', '蔬菜水果', '玉米', 20, 50, 30, 120, 19, 13, 4, 0,1,1),
('PVF002', '蔬菜水果', '番茄', 30, 50, 30, 120, 79, 6, 2, 0,1,1),
('PVF003', '蔬菜水果', '小黃瓜', 15, 50, 30, 120, 76, 11, 0, 0,1,1),
('PVF004', '蔬菜水果', '空心菜', 60, 50, 30, 120, 66, 27, 5, 0,1,1),
('PVF005', '蔬菜水果', '高麗菜', 70, 50, 15, 120, 55, 0, 0, 0,1,1),
('PSN001', '零食點心', '乖乖', 20, 100, 20, 120, 97, 12, 4, 0,1,0),
('PSN002', '零食點心', '蝦味先', 25, 100, 20, 120, 31, 13, 4, 0,1,0),
('PSN003', '零食點心', '可樂果', 15, 100, 20, 120, 42, 13, 4, 0,1,0),
('PSN004', '零食點心', '蘇打餅乾', 35, 100, 20, 120, 51, 1, 4, 0,1,0),
('PSN005', '零食點心', '多力多滋', 35, 100, 20, 120, 93, 4, 1, 0,1,0),
('PRN001', '米飯麵條', '池上米', 150, 100, 10, 120, 44, 9, 1, 0,1,0),
('PRN002', '米飯麵條', '義大利麵', 70, 100, 20, 120, 85, 0, 3, 0,1,0),
('PRN003', '米飯麵條', '冬粉', 30, 100, 20, 120, 24, 21, 0, 0,1,0),
('PRN004', '米飯麵條', '手打麵', 40, 100, 20, 120, 15, 1, 3, 0,1,0),
('PRN005', '米飯麵條', '關廟麵', 35, 100, 20, 120, 88, 23, 0, 0,1,0),
('PDR001', '飲品', '可樂', 30, 120, 20, 120, 9, 1, 3, 0,1,0),
('PDR002', '飲品', '雪碧', 30, 120, 20, 120, 11, 3, 2, 0,1,0),
('PDR003', '飲品', '伯朗咖啡', 45, 120, 20, 120, 12, 29, 0, 0,1,0),
('PDR004', '飲品', '原萃', 25, 120, 20, 120, 62, 28, 1, 0,1,0),
('PDR005', '飲品', '黑松FIN補給飲料', 25, 120, 20, 120, 20, 11, 5, 0,1,0);

-- 插入盤點資料
INSERT INTO inventory_check (inventory_check_id, employee_id, inventory_check_date)
VALUES 
('IC001', 'E001', '2024-10-01'),
('IC002', 'E002', '2024-10-02'),
('IC003', 'E003', '2024-10-03');

-- 插入到盤點明細
INSERT INTO inventory_check_details (detail_id, inventory_check_id, product_id, current_inventory, actual_inventory, differential_inventory, remark)
VALUES 
('ICD00000001', 'IC001', 'PMS001', 50, 48, -2, '數量減少，商品損壞'),
('ICD00000002', 'IC001', 'PMS002', 30, 30, 0, '無差異'),
('ICD00000003', 'IC002', 'PMS003', 100, 98, -2, '商品破損'),
('ICD00000004', 'IC002', 'PMS004', 25, 27, 2, '調整記錄錯誤'),
('ICD00000005', 'IC003', 'PMS005', 60, 60, 0, '無差異');


-- 插入供應商資料
INSERT INTO suppliers (supplier_id, supplier_name, address, tax_number, contact_person, phone, email, bank_account) VALUES
	('S001', '聯華', '812 高雄市小港區宮仁街14號', '07569627', '曹宗桂', '0912345678', 'tomlin@lianhwa.com.tw', '808-0939979294191'),
	('S002', '泰山', '950 臺東縣臺東市漢中街11號', '10508879', '江為帆', '0915001903', 'fascinated@yahoo.com', '777-0939979294191'),
	('S003', '光泉', '732 臺南市白河區五汴頭11號', '22346371', '詹長合', '0920049630', 'ourtordered@outlook.com', '555-0939979294191'),
	('S004', '金車', '412 臺中市大里區甲堤三街35號', '90458083', '莊恆月', '0924410258', 'graying@gmail.com', '333-0939979294191'),
	('S005', '南亞', '640 雲林縣斗六市棒球十八街13號', '70974461', '莊旭誠', '0938327361', 'softspoken@gmail.com', '111-0939979294191');

-- 插入供應商商品資料
-- 假設已有 products 表和相關商品資料
INSERT INTO supplier_products (supplier_product_id, supplier_id, product_id, product_price, status) VALUES
	('SP001', 'S001', 'PDR001', 20, 1),
	('SP002', 'S002', 'PDR002', 25, 1),
	('SP003', 'S003', 'PDR003', 30, 1),
	('SP004', 'S004', 'PDR004', 35, 1),
	('SP005', 'S005', 'PDR005', 40, 1),
	('SP006', 'S001', 'PMS001', 100, 1),
	('SP007', 'S002', 'PMS002', 150, 1),
	('SP008', 'S003', 'PMS003', 200, 1),
	('SP009', 'S004', 'PMS004', 120, 1),
	('SP010', 'S005', 'PMS005', 130, 1),
	('SP011', 'S001', 'PRN001', 50, 1),
	('SP012', 'S002', 'PRN002', 60, 1),
	('SP013', 'S003', 'PRN003', 70, 1),
	('SP014', 'S004', 'PRN004', 80, 1),
	('SP015', 'S005', 'PRN005', 90, 1),
	('SP016', 'S001', 'PSN001', 15, 1),
	('SP017', 'S002', 'PSN002', 18, 1),
	('SP018', 'S003', 'PSN003', 20, 1),
	('SP019', 'S004', 'PSN004', 22, 1),
	('SP020', 'S005', 'PSN005', 25, 1),
	('SP021', 'S001', 'PVF001', 10, 1),
	('SP022', 'S002', 'PVF002', 12, 1),
	('SP023', 'S003', 'PVF003', 15, 1),
	('SP024', 'S004', 'PVF004', 18, 1),
	('SP025', 'S005', 'PVF005', 20, 1),
	('SP026', 'S002', 'PDR001', 30, 1),
	('SP027', 'S003', 'PDR001', 40, 0),
	('SP028', 'S004', 'PDR001', 50, 0),
	('SP029', 'S005', 'PDR001', 60, 1);

-- 插入供應商帳戶資料
INSERT INTO supplier_accounts (account_id, supplier_id, total_amount, paid_amount, unpaid_amount) VALUES
	('ACC001', 'S001', 2000, 2000, 0),
	('ACC002', 'S002', 3000, 2000, 1000),
	('ACC003', 'S003', 2400, 2400, 0),
	('ACC004', 'S004', 2450, 2450, 0),
	('ACC005', 'S005', 3600, 3600, 0);

-- 插入進貨資料
INSERT INTO restocks (restock_id, restock_total_price, restock_date, employee_id) VALUES
	('20241002001', 5000, '2024-10-02', 'E001'),
	('20241002002', 1250, '2024-10-02', 'E001'),
	('20241002003', 2400, '2024-10-02', 'E001'),
	('20241002004', 2450, '2024-10-02', 'E001'),
	('20241002005', 3600, '2024-10-02', 'E001');

-- 插入進貨明細資料，直接包含 supplier_id
INSERT INTO restock_details (detail_id, restock_id, supplier_id, supplier_product_id, number_of_restock, price_at_restock, restock_total_price, production_date, due_date, restock_date) VALUES
	('RD001', '20241002001', 'S001', 'SP001', 100,20, 2000, '2024-09-01', '2025-09-01', '2024-10-02'),
	('RD002', '20241002001', 'S002', 'SP012', 50,60, 3000, '2024-09-01', '2025-09-01', '2024-10-02'),
	('RD003', '20241002002', 'S002', 'SP012', 50,25, 1250, '2024-09-01', '2025-09-01', '2024-10-02'),
	('RD004', '20241002003', 'S003', 'SP003', 80, 30,2400, '2024-07-20', '2025-07-20', '2024-10-02'),
	('RD005', '20241002004', 'S004', 'SP004', 70,35, 2450, '2024-06-10', '2025-06-10', '2024-10-02'),
	('RD006', '20241002005', 'S005', 'SP005', 90,40, 3600, '2024-05-25', '2025-05-25', '2024-10-02');

-- 插入付款資料
INSERT INTO payments (payment_id, account_id, payment_date, payment_method, total_amount, payment_status) VALUES
    ('PMT006', 'ACC001', '2024-10-10', '銀行轉帳', 12250, '已支付');

-- 插入付款記錄資料，對應 detail_id
INSERT INTO payment_records (record_id, payment_id, detail_id, payment_amount) VALUES
	('REC001', 'PMT006', 'RD001', 2000), -- S001: RD001
	('REC002', 'PMT006', 'RD002', 2000), -- S002: RD002 部分支付
	('REC003', 'PMT006', 'RD003', 1250), -- S002: RD003
	('REC004', 'PMT006', 'RD004', 2400), -- S003: RD004
	('REC005', 'PMT006', 'RD005', 2450), -- S004: RD005
	('REC006', 'PMT006', 'RD006', 1200); -- S005: RD006 部分支付

--新增職級和員工
INSERT INTO ranklevel (position_id, position_name, limits_of_authority, salary_level)
VALUES
	('M01', '經理', 3, 'SSS'),
	('M02', '主管', 2, 'SS'),
	('M03', '員工', 1, 'S');

INSERT INTO employee (employee_id, employee_name, employee_tel, employee_idcard, employee_email, password, position_id, hiredate, resigndate, is_first_login, image_path)
VALUES
	('E001', '阿綸', '0900123123', 'A123456789', 'aaa0001@gmail.com', '0000', 'M01', '2024-06-13', NULL,1, NULL),
	('E002', '阿哲', '0900456456', 'B123456789', 'aaa0002@gmail.com', '0000', 'M02', '2024-06-13', NULL,1, NULL),
	('E003', '阿瑋', '0900789789', 'C123456789', 'aaa0003@gmail.com', '0000', 'M03', '2024-06-13', NULL,1, NULL),
	('E004', '阿翔', '0900321321', 'D123456789', 'aaa0004@gmail.com', '0000', 'M03', '2024-06-13', NULL,1, NULL),
	('E005', '阿翰', '0900654654', 'E123456789', 'aaa0005@gmail.com', '0000', 'M03', '2024-06-13', NULL,1, NULL),
	('E006', '阿鼎', '0900987987', 'F123456789', 'ricekevin22@gmail.com', '0000', 'M02', '2024-06-13', NULL,1, NULL);

INSERT INTO chat_messages (from_user, to_user, content, timestamp)
VALUES 
	('E006', 'E001', N'Hey, how are you?', '2024-10-12 10:00:00'),
	('E001', 'E006', N'I am good, thanks! How about you?', '2024-10-12 10:05:00'),
	('E006', 'E001', N'Pretty busy with work. Lots to do.', '2024-10-12 10:10:00'),
	('E001', 'E006', N'I can imagine. Let me know if you need help.', '2024-10-12 10:15:00'),
	('E006', 'E001', N'Thanks, I appreciate it.', '2024-10-12 10:20:00'),
	('E006', 'E002', N'Good morning! Are you joining the meeting today?', '2024-10-12 09:30:00'),
	('E002', 'E006', N'Good morning! Yes, I will be there.', '2024-10-12 09:35:00'),
	('E006', 'E002', N'Great! See you there.', '2024-10-12 09:40:00'),
	('E002', 'E006', N'Looking forward to it.', '2024-10-12 09:45:00'),
	('E006', 'E002', N'Let me know if you need any preparation.', '2024-10-12 09:50:00');

--新增會員
INSERT INTO customer (customer_tel, customer_name, customer_email, date_of_registration, total_points)
VALUES
	('0912123123', '假挖', 'cus001@gmail.com', '2024-07-01', '0'),
	('0912321321', '系窩', 'cus002@gmail.com', '2024-07-01', '0'),
	('0912231231', '嗨博內', 'cus003@gmail.com', '2024-07-01', '0'),
	('0933345345', '斯拚', 'cus004@gmail.com', '2024-07-01', '0'),
	('0933543543', '色類', 'cus005@gmail.com', '2024-07-01', '0'),
	('0933534534', '張三', 'cus006@gmail.com', '2024-07-01', '0'),
	('0966789789', '李四', 'cus007@gmail.com', '2024-07-01', '0'),
	('0966987987', '王五', 'cus008@gmail.com', '2024-07-01', '0'),
	('0966879879', '全六', 'cus009@gmail.com', '2024-07-01', '0'),
	('0955951951', '小七', 'cus010@gmail.com', '2024-07-01', '0'),
	('0955159159', '老八', 'cus011@gmail.com', '2024-07-01', '0'),
	('0955591591', '好九', 'cus012@gmail.com', '2024-07-01', '0'),
	('0988753753', '吳十', 'cus013@gmail.com', '2024-07-01', '0'),
	('0988537537', '新一', 'cus014@gmail.com', '2024-07-01', '0'),
	('0944852258', '有二', 'cus015@gmail.com', '2024-07-01', '0');

-- 新增請假單
INSERT INTO ask_for_leave (leave_id, employee_id, start_time, end_time, reason_leave, proof_image, approved_status, category_id, record_id) VALUES 
('L0001', 'E001', '2024-10-01 08:00', '2024-10-01 12:00', '因病無法上班', NULL, '已批准', 1, 1),
('L0002', 'E001', '2024-10-02 09:00', '2024-10-02 17:00', '家中有急事需要處理', NULL, '待審核', 2, 2),
('L0003', 'E002', '2024-09-15 08:00', '2024-09-15 12:00', '利用特休休假', NULL, '已批准', 3, 3),
('L0004', 'E003', '2024-10-05 08:00', '2024-10-05 17:00', '結婚需要請假', NULL, '已批准', 4, 4),
('L0005', 'E003', '2024-10-10 08:00', '2024-10-10 12:00', '突發病情需要請假', NULL, '已批准', 1, 5),
('L0006', 'E004', '2024-09-20 08:00', '2024-09-20 17:00', '家庭事務繁忙', NULL, '待審核', 2, 6),
('L0007', 'E005', '2024-08-25 08:00', '2024-08-25 12:00', '年假使用', NULL, '已批准', 3, 7),
('L0008', 'E005', '2024-09-30 09:00', '2024-09-30 17:00', '親人過世需請喪假', NULL, '已批准', 5, 8),
('L0009', 'E006', '2024-10-05 08:00', '2024-10-05 12:00', '因病無法上班', NULL, '待審核', 1, 9),
('L0010', 'E002', '2024-09-16 08:00', '2024-09-16 17:00', '家中有事需要請假', NULL, '已批准', 2, 10),
('L0011', 'E001', '2024-09-20 09:00', '2024-09-20 17:00', '利用特休休假', NULL, '已批准', 3, 11),
('L0012', 'E003', '2024-10-12 08:00', '2024-10-12 12:00', '結婚需要請假', NULL, '已批准', 4, 12),
('L0013', 'E006', '2024-09-25 08:00', '2024-09-25 17:00', '突發疾病需要請病假', NULL, '待審核', 1, 13),
('L0014', 'E004', '2024-10-01 08:00', '2024-10-01 12:00', '親人過世需請喪假', NULL, '已批准', 5, 14),
('L0015', 'E005', '2024-10-08 08:00', '2024-10-08 17:00', '家中事務需要請事假', NULL, '已批准', 2, 15),
('L0016', 'E001', '2024-10-15 08:00', '2024-10-15 12:00', '因病無法上班', NULL, '待審核', 1, 16),
('L0017', 'E001', '2024-10-18 09:00', '2024-10-18 17:00', '家中有事需要處理', NULL, '已批准', 2, 17),
('L0018', 'E001', '2024-10-20 08:00', '2024-10-20 12:00', '利用特休休假', NULL, '待審核', 3, 18),
('L0019', 'E001', '2024-10-22 09:00', '2024-10-22 17:00', '結婚需要請假', NULL, '已批准', 4, 19),
('L0020', 'E001', '2024-10-25 08:00', '2024-10-25 12:00', '因病無法上班', NULL, '待審核', 1, 20),
('L0021', 'E001', '2024-10-28 09:00', '2024-10-28 17:00', '家中有事需要處理', NULL, '已批准', 2, 21),
('L0022', 'E001', '2024-10-30 08:00', '2024-10-30 12:00', '利用特休休假', NULL, '待審核', 3, 22),
('L0023', 'E001', '2024-11-01 09:00', '2024-11-01 17:00', '結婚需要請假', NULL, '已批准', 4, 23),
('L0024', 'E001', '2024-11-05 08:00', '2024-11-05 12:00', '因病無法上班', NULL, '待審核', 1, 24),
('L0025', 'E001', '2024-11-08 09:00', '2024-11-08 17:00', '家中有事需要處理', NULL, '已批准', 2, 25),
('L0026', 'E001', '2024-11-10 08:00', '2024-11-10 12:00', '利用特休休假', NULL, '待審核', 3, 26),
('L0027', 'E001', '2024-11-12 09:00', '2024-11-12 17:00', '結婚需要請假', NULL, '已批准', 4, 27);


-- 插入 leave_category 表
INSERT INTO leave_category (leave_type, limit_hours) VALUES 
('病假', 50),     -- 額度時數 50 小時
('事假', 80),     -- 額度時數 80 小時
('特休', 30),     -- 額度時數 30 小時
('婚假', 56),     -- 額度時數 56 小時
('喪假', 40);     -- 額度時數 40 小時


--新增排班表
INSERT INTO schedule (employee_id, start_time, end_time) VALUES
	('E002', '2024-10-01 09:00', '2024-10-01 12:00'),
	('E001', '2024-10-01 09:00', '2024-10-01 12:00'),
	('E003', '2024-10-01 12:00', '2024-10-01 17:00'),
	('E005', '2024-10-01 12:00', '2024-10-01 17:00'),
	('E002', '2024-10-02 10:00', '2024-10-02 18:00'),
	('E003', '2024-10-03 08:30', '2024-10-03 16:30'),
	('E004', '2024-10-04 09:00', '2024-10-04 17:00'),
	('E005', '2024-10-05 10:00', '2024-10-05 18:00'),
	('E006', '2024-10-06 08:00', '2024-10-06 16:00'),
	('E001', '2024-10-07 09:30', '2024-10-07 17:30'),
	('E002', '2024-10-08 10:00', '2024-10-08 18:00'),
	('E003', '2024-10-09 08:00', '2024-10-09 16:00'),
	('E004', '2024-10-10 09:00', '2024-10-10 17:00');

-- 新增結帳單
INSERT INTO checkout (checkout_id,invoice_number , customer_tel, employee_id, checkout_total_price,checkout_date,bonus_points,points_due_date, checkout_status, related_return_id)
VALUES
	('C00000001','IN00000001','0912123123', 'E001', 2060 , '2024-07-14',20, '2025-07-14','已退貨','T00000001'),
	('C00000002','IN00000002','', 'E002', 175 , '2024-07-14',0, '','已退貨','T00000002'),
	('C00000003','IN00000003','0912123123', 'E003', 1500 , '2024-07-14',15, '2025-07-14','已退貨','T00000003'),
	('C00000004','IN00000004','0912321321', 'E001', 4090 , '2024-07-14',40, '2025-07-14','正常',''),
	('C00000005','IN00000005','0933345345', 'E002', 565 , '2024-07-14',5, '2025-07-14','正常',''),
	('C00000006','IN00000006','0912123123', 'E004', 1860 , '2024-07-15',18, '2025-07-15','正常',''),
	('C00000007','IN00000007','', 'E005', 105 , '2024-07-16',0, '','正常',''),
	('C00000008','IN00000008','0912123123', 'E006', 750 , '2024-07-17',7, '2025-07-17','正常','');
	

-- 新增結帳明細        
INSERT INTO checkout_details(checkout_id,product_id,number_of_checkout,product_price,checkout_price)
VALUES
	('C00000001', 'PMS001', 10, 200, 2000),
	('C00000001', 'PVF001', 3, 20, 60),
	('C00000002', 'PSN004', 5, 35, 175),
	('C00000003', 'PRN001', 8, 150, 1200),
	('C00000003', 'PDR001', 10, 30, 300),
	('C00000004', 'PMS002', 20, 200, 4000),
	('C00000004', 'PVF002', 3, 30, 90),
	('C00000005', 'PSN003', 5, 15, 75),
	('C00000005', 'PRN003', 8, 30, 240),
	('C00000005', 'PDR004', 10, 25, 250),
	('C00000006', 'PMS001', 9, 200, 1800),
	('C00000006', 'PVF001', 3, 20, 60),
	('C00000007', 'PSN004', 3, 35, 105),
	('C00000008', 'PRN001', 3, 150, 450),
	('C00000008', 'PDR001', 10, 30, 300);

-- 新增退貨單
INSERT INTO return_products (return_id,original_checkout_id,original_invoice_number, employee_id , return_total_price , return_date)
VALUES
	('T00000001', 'C00000001', 'IN00000001', 'E001', 200 , '2024-07-15'),
	('T00000002', 'C00000002', 'IN00000002', 'E002', 70,  '2024-07-16'),
	('T00000003', 'C00000003', 'IN00000003', 'E003', 750 , '2024-07-17');

-- 新增退貨明細
INSERT INTO return_details(return_id,original_checkout_id,product_id,reason_for_return,number_of_return,product_price,return_price,return_status,return_photo) 
VALUES
('T00000001', 'C00000001', 'PMS001', '豬肉臭酸',1, 200, 200,'商品品質異常',NULL),
('T00000002', 'C00000002', 'PSN004' , '蘇打餅乾包裝完整但輕微壓扁',2, 35, 70,'商品外觀損傷',NULL),
('T00000003', 'C00000003','PRN001' , '米裡面有蟲',5, 150, 750,'商品品質異常',NULL);

-- 新增紅利兌換商品表
INSERT INTO bonus_exchange (exchange_id,product_id ,customer_tel ,use_points ,number_of_exchange,exchange_date )
VALUES
	('H00000001', 'PSN003', '0912123123', 15,1,'2024-7-30'),
	('H00000002', 'PVF001', '0912321321', 20,1,'2024-7-30');

--新增有會員點數做紅利兌換商品測試
INSERT INTO customer (customer_tel, customer_name, customer_email, date_of_registration, total_points)
VALUES
	('0955993322', '阿偉', 'cus021@gmail.com', '2024-07-01', 500),
	('0933333333', '偉哥', 'cus022@gmail.com', '2024-07-01', 200),
	('0911111111', '陳小明', 'chenxm@example.com', '2023-01-05', 350),
	('0922222222', '林美玲', 'linml@example.com', '2023-02-10', 400),
	('0977777777', '王大衛', 'wangdw@example.com', '2023-03-15', 450),
	('0944444444', '張惠美', 'zhanghm@example.com', '2023-04-20', 500),
	('0955555555', '李國強', 'ligq@example.com', '2023-05-25', 600),
	('0966666666', '趙子龍', 'zhaozl@example.com', '2023-06-30', 700);     

-- 新增紅利紀錄表
INSERT INTO points_history (customer_tel ,checkout_id ,exchange_id ,points_change,transaction_date, transaction_type)
VALUES
	('0912123123', 'C00000001','', 20, '2024-7-14','earn'),
	('0912123123', 'C00000003','', 15, '2024-7-15','earn'),
	('0912321321', 'C00000004','', 40, '2024-7-16','earn'),
	('0912123123', 'C00000005','', 5, '2024-7-17','earn'),
	('0912123123','' ,'H00000001', 15, '2024-7-30','use'),
	('0912321321', '','H00000002', 20, '2024-7-30','use');
    
               

--======================================================================
-- 有功能之表格












--======================FK鍵========================

--建立盤點employee_id的和員工employee_id的外鍵
ALTER TABLE inventory_check 
ADD CONSTRAINT employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

--建立盤點inventory_check_id的和盤點明細inventory_check_id的外鍵
ALTER TABLE inventory_check_details 
ADD CONSTRAINT inventory_check_id_FK
FOREIGN KEY (inventory_check_id)
REFERENCES inventory_check(inventory_check_id);

--建立商品product_idd的和盤點明細product_id的外鍵
ALTER TABLE inventory_check_details 
ADD CONSTRAINT product_id_FK
FOREIGN KEY (product_id)
REFERENCES products(product_id);

-- 建立員工的position_id與職級的position_id外鍵關係
ALTER TABLE employee
ADD CONSTRAINT position_id_FK
FOREIGN KEY (position_id)
REFERENCES ranklevel(position_id);

-- 建立通知的employee_id與員工的employee_id外鍵關係
ALTER TABLE notification
ADD CONSTRAINT employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

-- 建立聊天訊息的employee_id與員工的employee_id外鍵關係
ALTER TABLE chat_messages
ADD CONSTRAINT fk_from_user
FOREIGN KEY (from_user)
REFERENCES employee(employee_id)
ON DELETE NO ACTION
ON UPDATE CASCADE;

ALTER TABLE chat_messages
ADD CONSTRAINT fk_to_user
FOREIGN KEY (to_user)
REFERENCES employee(employee_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

-- 建立排班表的employee_id與員工的employee_id外鍵關係
ALTER TABLE schedule
ADD CONSTRAINT schedule_employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

-- 建立請假的employee_id與員工的employee_id外鍵關係
ALTER TABLE ask_for_leave
ADD CONSTRAINT ask_for_leave_employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

-- 設定進貨的employee_id與員工的employee_id建立外鍵關係
ALTER TABLE restocks
ADD CONSTRAINT restocks_employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

-- 設定進貨明細的restock_id與restocks的restock_id建立外鍵關係
ALTER TABLE restock_details
ADD CONSTRAINT restock_details_restock_id_FK
FOREIGN KEY (restock_id)
REFERENCES restocks(restock_id);

-- 設定進貨明細的supplier_id與suppliers 表的supplier_id建立外鍵關係
ALTER TABLE restock_details
ADD CONSTRAINT restock_details_supplier_id_FK
FOREIGN KEY (supplier_id)
REFERENCES suppliers(supplier_id);

-- 設定進貨明細的supplier_product_id與supplier_products 表的supplier_product_id建立外鍵關係
ALTER TABLE restock_details
ADD CONSTRAINT restock_details_supplier_product_id_FK
FOREIGN KEY (supplier_product_id)
REFERENCES supplier_products(supplier_product_id);

-- 設定退貨的employee_id與員工的employee_id建立外鍵關係
ALTER TABLE return_products
ADD CONSTRAINT return_products_employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

-- 設定退貨的original_checkout_id與結帳的checkout_id建立外鍵關係
ALTER TABLE return_products
ADD CONSTRAINT return_products_original_checkout_id_FK
FOREIGN KEY (original_checkout_id)
REFERENCES checkout(checkout_id);

-- 設定退貨的original_invoice_number與結帳的invoice_number建立外鍵關係
ALTER TABLE return_products
ADD CONSTRAINT return_products_original_invoice_number_FK
FOREIGN KEY (original_invoice_number)
REFERENCES checkout(invoice_number);

-- 設定退貨明細的return_id與退貨的return_id建立外鍵關係
ALTER TABLE return_details
ADD CONSTRAINT return_details_return_id_FK
FOREIGN KEY (return_id)
REFERENCES return_products(return_id);

-- 設定退貨明細的original_checkout_id與結帳明細的checkout_id建立外鍵關係
ALTER TABLE return_details
ADD CONSTRAINT return_details_original_checkout_id_FK
FOREIGN KEY (original_checkout_id)
REFERENCES checkout_details(checkout_id);

-- 設定退貨明細的product_id與結帳明細的product_id建立外鍵關係
ALTER TABLE return_details
ADD CONSTRAINT return_details_product_id_FK
FOREIGN KEY (product_id)
REFERENCES checkout_details(product_id);

-- 設定結帳的employee_id與員工的employee_id建立外鍵關係
ALTER TABLE checkout
ADD CONSTRAINT checkout_employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

-- 重新創建外鍵約束，只在 customer_tel 不為 NULL 時生效
ALTER TABLE checkout
ADD CONSTRAINT checkout_customer_tel_FK
FOREIGN KEY (customer_tel)
REFERENCES customer(customer_tel)
ON DELETE SET NULL
ON UPDATE CASCADE;

-- 重新創建外鍵約束，只在 related_return_id 不為 NULL 時生效
ALTER TABLE checkout
ADD CONSTRAINT checkout_related_return_id_FK
FOREIGN KEY (related_return_id)
REFERENCES return_products(return_id)
ON DELETE SET NULL
ON UPDATE CASCADE;

-- 設定結帳明細的checkout_id與結帳的checkout_id建立外鍵關係
ALTER TABLE checkout_details
ADD CONSTRAINT checkout_details_checkout_id_FK
FOREIGN KEY (checkout_id)
REFERENCES checkout(checkout_id);

--ALTER TABLE checkout_details
--DROP CONSTRAINT checkout_details_checkout_id_FK;

-- 設定結帳明細的product_id與商品的product_id建立外鍵關係
ALTER TABLE checkout_details
ADD CONSTRAINT checkout_details_product_id_FK
FOREIGN KEY (product_id)
REFERENCES products(product_id);

-- 紅利兌換商品的product_id與商品的product_id建立外鍵關係
ALTER TABLE bonus_exchange
ADD CONSTRAINT bonus_exchange_product_id_FK
FOREIGN KEY (product_id)
REFERENCES products(product_id);

-- 紅利兌換商品的customer_tel與顧客的customer_tel建立外鍵關係
ALTER TABLE bonus_exchange
ADD CONSTRAINT bonus_exchange_customer_tel_FK
FOREIGN KEY (customer_tel)
REFERENCES customer(customer_tel);

-- 設定紅利紀錄表的customer_tel與會員的customer_tel外鍵關係
ALTER TABLE points_history
ADD CONSTRAINT points_history_customer_tel_FK
FOREIGN KEY (customer_tel)
REFERENCES customer(customer_tel);

-- 設定紅利紀錄表的checkout_id與結帳的checkout_id外鍵關係
-- 創建新的外鍵約束，只在 checkout_id 不為 NULL 時生效
ALTER TABLE points_history
ADD CONSTRAINT points_history_checkout_id_FK
FOREIGN KEY (checkout_id)
REFERENCES checkout(checkout_id)
ON DELETE SET NULL
ON UPDATE CASCADE;

-- 設定紅利紀錄表的exchange_id與兌換商品的exchange_id外鍵關係
-- 創建新的外鍵約束，只在 exchange_id 不為 NULL 時生效
ALTER TABLE points_history
ADD CONSTRAINT points_history_exchange_id_FK
FOREIGN KEY (exchange_id)
REFERENCES bonus_exchange(exchange_id)
ON DELETE SET NULL
ON UPDATE CASCADE;

-- 添加檢查約束，確保 checkout_id 和 exchange_id 不會同時有值
--ALTER TABLE points_history
--ADD CONSTRAINT CHK_points_history_id_exclusivity
--CHECK ((checkout_id IS NULL AND exchange_id IS NOT NULL) OR 
--       (checkout_id IS NOT NULL AND exchange_id IS NULL) OR 
--       (checkout_id IS NULL AND exchange_id IS NULL));

-- 設定供應商商品表的supplier_id與suppliers 表的supplier_id外鍵關係
ALTER TABLE supplier_products
ADD CONSTRAINT supplier_products_supplier_id_FK
FOREIGN KEY (supplier_id)
REFERENCES suppliers(supplier_id);

-- 設定供應商商品表的product_id與products 表的product_id外鍵關係
ALTER TABLE supplier_products
ADD CONSTRAINT supplier_products_product_id_FK
FOREIGN KEY (product_id)
REFERENCES products(product_id);

-- 設定供應商帳戶表的supplier_id與suppliers 表的supplier_id外鍵關係
ALTER TABLE supplier_accounts
ADD CONSTRAINT supplier_accounts_supplier_id_FK
FOREIGN KEY (supplier_id)
REFERENCES suppliers(supplier_id);

-- 設定付款記錄表的restock_id與restocks表的restock_id外鍵關係
ALTER TABLE payment_records
ADD CONSTRAINT supplier_accounts_restock_id_FK
FOREIGN KEY (restock_id)
REFERENCES restocks(restock_id);



--======================================================================
SELECT * FROM products;
SELECT * FROM suppliers;
SELECT * FROM supplier_products;
SELECT * FROM supplier_accounts;
SELECT * FROM restocks;
SELECT * FROM restock_details;
SELECT * FROM payments;
SELECT * FROM payment_records;
SELECT * FROM return_details;
SELECT * FROM return_products;
SELECT * FROM checkout_details;
SELECT * FROM checkout;
SELECT * FROM ranklevel;
SELECT * FROM employee;
SELECT * FROM schedule;
SELECT * FROM ask_for_leave;
SELECT * FROM leave_record;
SELECT * FROM leave_category;
SELECT * FROM customer;
SELECT * FROM bonus_exchange;
SELECT * FROM points_history;

--TRUNCATE TABLE products;
--TRUNCATE TABLE suppliers
--TRUNCATE TABLE supplier_products
--TRUNCATE TABLE supplier_accounts
--TRUNCATE TABLE restocks
--TRUNCATE TABLE restock_details
--TRUNCATE TABLE payments
--TRUNCATE TABLE payment_records
--TRUNCATE TABLE return_details;
--TRUNCATE TABLE return_products;
--TRUNCATE TABLE checkout_details;
--TRUNCATE TABLE checkout;
--TRUNCATE TABLE ranklevel;
--TRUNCATE TABLE employee;
--TRUNCATE TABLE schedule;
--TRUNCATE TABLE ask_for_leave;
--TRUNCATE TABLE leave_record;
--TRUNCATE TABLE leave_category;
--TRUNCATE TABLE customer;
--TRUNCATE TABLE bonus_exchange;
--TRUNCATE TABLE points_history;

DROP TABLE products;
DROP TABLE suppliers;
DROP TABLE supplier_products;
DROP TABLE supplier_accounts;
DROP TABLE restocks;
DROP TABLE restock_details;
DROP TABLE payments;
DROP TABLE payment_records;
DROP TABLE return_details;
DROP TABLE return_products;
DROP TABLE checkout_details;
DROP TABLE checkout;
DROP TABLE ranklevel;
DROP TABLE employee;
DROP TABLE schedule;
DROP TABLE ask_for_leave;
DROP TABLE leave_record;
DROP TABLE leave_category;
DROP TABLE customer;
DROP TABLE bonus_exchange;
DROP TABLE points_history;