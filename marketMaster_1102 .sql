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
	verify_status BIT NOT NULL, --審核狀態 1 已審核 0 未審核
	verify_employee_id VARCHAR(30) --審核人
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
	customer_tel VARCHAR(30), -- 會員手機
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
    schedule_id INT IDENTITY(1,1) PRIMARY KEY, -- 排班ID
    employee_id VARCHAR(30) NOT NULL,          -- 員工編號
	schedule_date DATE NOT NULL ,              -- 排班日期
    start_time TIME NOT NULL,                   -- 班次開始時間
	end_time TIME NOT NULL,                     -- 班次結束時間
	schedule_hour INT,                          -- 時數
	schedule_active BIT NOT NULL DEFAULT 1     --排班意願
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
	leave_hours INT NOT NULL,					--請假時數
	rejection_reason NVARCHAR(255)	           -- 駁回原因
);

-- 創建 請假類別表
CREATE TABLE leave_category (
    category_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,  -- id
    leave_type NVARCHAR(50) NOT NULL,                   -- 請假類別
	max_hours INT NULL				                     --最大時數 
);

-- 創建請假紀錄表
CREATE TABLE leave_record (
    record_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,  -- id
    employee_id VARCHAR(30) NOT NULL,                  -- 員工編號
    category_id INT NOT NULL,                          -- 請假類別ID
    expiration_date Date NOT NULL,                       -- 到期日
    actual_hours INT NULL DEFAULT 0 ,                  -- 實際時數，預設為 0
	limit_hours INT										-- 額度時數
);

-- 創建會員表
CREATE TABLE customer (
    customer_tel VARCHAR(30) NOT NULL PRIMARY KEY, -- 會員手機
    customer_name VARCHAR(30) NOT NULL, -- 會員姓名
    customer_email VARCHAR(30) NOT NULL, -- email
    date_of_registration DATE NOT NULL, -- 註冊日期
    total_points INT  NOT NULL -- 累積紅利點數 --Create時先設定為0
);

-- 創建可兌換商品管理表
CREATE TABLE item_management (
    item_id VARCHAR(30) NOT NULL PRIMARY KEY,         -- 可兌換商品編號 開頭為IM
    product_id VARCHAR(30) NOT NULL,                  -- 商品編號 FOREIGN KEY REFERENCES product(product_id),
    item_points INT NOT NULL,                         -- 所需點數
    item_maximum INT NOT NULL,                        -- 可兌換數量
    start_date DATE NOT NULL,                         -- 開始日期
    end_date DATE NOT NULL,                           -- 結束日期
    is_active BIT NOT NULL DEFAULT 1                 -- 是否可用 1可兌換活動期間 0 非活動期間
);

-- 創建紅利兌換商品表
CREATE TABLE bonus_exchange (
    exchange_id VARCHAR(30) NOT NULL PRIMARY KEY,  -- 兌換編號 開頭為EX
    customer_tel VARCHAR(30) NOT NULL,             -- 會員電話
    item_id VARCHAR(30) NOT NULL,                  -- 可兌換商品編號 FOREIGN KEY REFERENCES item_management(item_id),
    use_points INT NOT NULL,                       -- 使用紅利點數
    number_of_exchange INT NOT NULL,               -- 兌換數量
    exchange_date DATE NOT NULL                    -- 兌換日期
);


-- 創建紅利紀錄表
CREATE TABLE points_history (
    points_history_id INT IDENTITY(1,1) PRIMARY KEY, -- 紅利編號
    customer_tel VARCHAR(30) NOT NULL,-- 會員手機REFERENCES customer(customer_tel)
    checkout_id VARCHAR(30),-- 結帳編號REFERENCES checkout(checkout_id)
    exchange_id VARCHAR(30),-- 兌換編號REFERENCES bonus_exchange(exchange_id)
    points_change INT NOT NULL, -- 紅利點數變動量
    transaction_date DATE NOT NULL,-- 紅利記錄日期
    transaction_type VARCHAR(10) NOT NULL,-- 紅利狀態
    CONSTRAINT points_history_chk_1 
        CHECK (transaction_type IN ('earn', 'use', 'expire', 'refund'))
);


--======================================================================
--插入資料

-- 插入商品資料
INSERT INTO products ( product_id, product_category, product_name, product_price, product_safeinventory, Number_of_shelve, Number_of_inventory, Number_of_sale, Number_of_exchange, Number_of_destruction, Number_of_remove, product_available, is_perishable
) VALUES 
('PMS001', '肉品海鮮', '豬肉', 180, 30, 10, 25, 12, 2, 3, 0, 1, 1), 
('PMS002', '肉品海鮮', '牛肉', 320, 25, 12, 37, 15, 1, 2, 0, 1, 1), 
('PMS003', '肉品海鮮', '雞肉', 150, 35, 15, 30, 18, 0, 2, 0, 1, 1), 
('PMS004', '肉品海鮮', '蛤蠣', 160, 28, 10, 50, 16, 1, 3, 0, 1, 1),
('PMS005', '肉品海鮮', '魚', 200, 32, 16, 25, 14, 2, 4, 0, 1, 1), 
('PVF001', '蔬菜水果', '玉米', 35, 40, 15, 50, 20, 3, 5, 0, 1, 1),
('PVF002', '蔬菜水果', '番茄', 25, 45, 20, 50, 25, 2, 4, 0, 1, 1),
('PVF003', '蔬菜水果', '小黃瓜', 20, 40, 25, 65, 22, 1, 3, 0, 1, 1),
('PVF004', '蔬菜水果', '空心菜', 30, 42, 18, 45, 24, 2, 5, 0, 1, 1),
('PVF005', '蔬菜水果', '高麗菜', 45, 38, 22, 58, 18, 1, 4, 0, 1, 1),
('PSN001', '零食點心', '乖乖', 25, 80, 40, 100, 30, 2, 1, 0, 1, 0),
('PSN002', '零食點心', '蝦味先', 25, 75, 25, 75, 25, 1, 1, 0, 1, 0),
('PSN003', '零食點心', '可樂果', 20, 80, 30, 70, 28, 2, 1, 0, 1, 0),
('PSN004', '零食點心', '蘇打餅乾', 35, 78, 38, 92, 26, 1, 1, 0, 1, 0),
('PSN005', '零食點心', '多力多滋', 30, 82, 35, 75, 32, 2, 1, 0, 1, 0),
('PRN001', '米飯麵條', '池上米', 150, 60, 20, 52, 15, 1, 0, 0, 1, 0),
('PRN002', '米飯麵條', '義大利麵', 85, 70, 25, 80, 20, 0, 1, 0, 1, 0),
('PRN003', '米飯麵條', '冬粉', 45, 65, 30, 85, 18, 1, 0, 0, 1, 0),
('PRN004', '米飯麵條', '手打麵', 95, 68, 22, 72, 22, 1, 1, 0, 1, 0),
('PRN005', '米飯麵條', '關廟麵', 45, 62, 28, 82, 16, 0, 0, 0, 1, 0),
('PDR001', '飲品', '可樂', 25, 90, 35, 92, 35, 1, 1, 0, 1, 0),
('PDR002', '飲品', '雪碧', 25, 85, 30, 95, 30, 1, 1, 0, 1, 0),
('PDR003', '飲品', '伯朗咖啡', 35, 95, 35, 100, 40, 2, 1, 0, 1, 0),
('PDR004', '飲品', '原萃', 20, 100, 40, 70, 45, 1, 1, 0, 1, 0),
('PDR005', '飲品', '黑松FIN補給飲料', 25, 92, 32, 100, 38, 2, 1, 0, 1, 0);

-- 插入盤點資料
INSERT INTO inventory_check (inventory_check_id, employee_id, inventory_check_date,verify_status)
VALUES 
('IC001', 'E001', '2024-10-01',1),
('IC002', 'E002', '2024-10-02',1),
('IC003', 'E003', '2024-10-03',1);

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
('S005', '南亞', '640 雲林縣斗六市棒球十八街13號', '70974461', '莊旭誠', '0938327361', 'softspoken@gmail.com', '111-0939979294191'),
('S006', '統一', '100 臺北市中正區信義路二段1號', '12345678', '張三', '0911111111', 'zhangsan@uni.com.tw', '123-4567890123456'),
('S007', '味全', '104 臺北市中山區南京東路二段2號', '87654321', '李四', '0922222222', 'lisi@wei.com.tw', '654-3210987654321'),
('S008', '台糖', '106 臺北市大安區忠孝東路三段3號', '23456789', '王五', '0933333333', 'wangwu@ts.com.tw', '234-5678901234567'),
('S009', '福壽', '110 臺北市信義區松仁路4號', '34567890', '趙六', '0944444444', 'zhaoliu@fs.com.tw', '345-6789012345678'),
('S010', '三洋', '114 臺北市內湖區瑞光路5號', '45678901', '孫七', '0955555555', 'sunqi@sy.com.tw', '456-7890123456789');

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
                                                                                                        ('SP029', 'S005', 'PDR001', 60, 1),
                                                                                                        ('SP030', 'S006', 'PDR001', 28, 1),
                                                                                                        ('SP031', 'S006', 'PDR002', 32, 1),
                                                                                                        ('SP032', 'S006', 'PDR003', 35, 1),
                                                                                                        ('SP033', 'S007', 'PDR004', 38, 1),
                                                                                                        ('SP034', 'S007', 'PDR005', 42, 1),
                                                                                                        ('SP035', 'S008', 'PMS001', 105, 1),
                                                                                                        ('SP036', 'S008', 'PMS002', 155, 1),
                                                                                                        ('SP037', 'S009', 'PMS003', 205, 1),
                                                                                                        ('SP038', 'S009', 'PMS004', 125, 1),
                                                                                                        ('SP039', 'S010', 'PMS005', 135, 1),
                                                                                                        ('SP040', 'S006', 'PRN001', 52, 1),
                                                                                                        ('SP041', 'S006', 'PRN002', 62, 1),
                                                                                                        ('SP042', 'S007', 'PRN003', 72, 1),
                                                                                                        ('SP043', 'S007', 'PRN004', 82, 1),
                                                                                                        ('SP044', 'S008', 'PRN005', 92, 1),
                                                                                                        ('SP045', 'S008', 'PSN001', 16, 1),
                                                                                                        ('SP046', 'S009', 'PSN002', 19, 1),
                                                                                                        ('SP047', 'S009', 'PSN003', 21, 1),
                                                                                                        ('SP048', 'S010', 'PSN004', 23, 1),
                                                                                                        ('SP049', 'S010', 'PSN005', 26, 1),
                                                                                                        ('SP050', 'S006', 'PVF001', 11, 1),
                                                                                                        ('SP051', 'S006', 'PVF002', 13, 1),
                                                                                                        ('SP052', 'S007', 'PVF003', 16, 1),
                                                                                                        ('SP053', 'S007', 'PVF004', 19, 1),
                                                                                                        ('SP054', 'S008', 'PVF005', 21, 1),
                                                                                                        ('SP055', 'S009', 'PDR001', 30, 1),
                                                                                                        ('SP056', 'S010', 'PDR001', 35, 1),
                                                                                                        ('SP057', 'S006', 'PDR001', 27, 1),
                                                                                                        ('SP058', 'S007', 'PDR002', 33, 1);


-- 插入供應商帳戶資料
INSERT INTO supplier_accounts (account_id, supplier_id, total_amount, paid_amount, unpaid_amount) VALUES
                                                                                                      ('ACC001', 'S001', 2000, 2000, 0),
                                                                                                      ('ACC002', 'S002', 4250, 3250, 1000),
                                                                                                      ('ACC003', 'S003', 2400, 2400, 0),
                                                                                                      ('ACC004', 'S004', 2450, 2450, 0),
                                                                                                      ('ACC005', 'S005', 3600, 1200, 2400),
                                                                                                      ('ACC006', 'S006', 2800, 2800, 0),
                                                                                                      ('ACC007', 'S007', 3200, 2000, 1200),
                                                                                                      ('ACC008', 'S008', 2560, 2560, 0),
                                                                                                      ('ACC009', 'S009', 2730, 2730, 0),
                                                                                                      ('ACC010', 'S010', 3780, 1500, 2280);

-- 插入進貨資料
INSERT INTO restocks (restock_id, restock_total_price, restock_date, employee_id) VALUES
                                                                                      ('20241002001', 5000, '2024-10-02', 'E001'),
                                                                                      ('20241002002', 1250, '2024-10-02', 'E001'),
                                                                                      ('20241002003', 2400, '2024-10-02', 'E001'),
                                                                                      ('20241002004', 2450, '2024-10-02', 'E001'),
                                                                                      ('20241002005', 3600, '2024-10-02', 'E001'),
                                                                                      ('20241002006', 4700, '2024-10-02', 'E001'),
                                                                                      ('20241002007', 1300, '2024-10-02', 'E001'),
                                                                                      ('20241002008', 2560, '2024-10-02', 'E001'),
                                                                                      ('20241002009', 2730, '2024-10-02', 'E001'),
                                                                                      ('20241002010', 3780, '2024-10-02', 'E001');

-- 插入進貨明細資料，直接包含 supplier_id
INSERT INTO restock_details (detail_id, restock_id, supplier_id, supplier_product_id, number_of_restock,price_at_restock,restock_total_price, production_date, due_date, restock_date) VALUES
                                                                                                                                                                                           ('RD001', '20241002001', 'S001', 'SP001', 100,20, 2000, '2024-09-01', '2025-09-01', '2024-10-02'),
                                                                                                                                                                                           ('RD002', '20241002001', 'S002', 'SP012', 50,60, 3000, '2024-09-01', '2025-09-01', '2024-10-02'),
                                                                                                                                                                                           ('RD003', '20241002002', 'S002', 'SP012', 50,25, 1250, '2024-09-01', '2025-09-01', '2024-10-02'),
                                                                                                                                                                                           ('RD004', '20241002003', 'S003', 'SP003', 80, 30,2400, '2024-07-20', '2025-07-20', '2024-10-02'),
                                                                                                                                                                                           ('RD005', '20241002004', 'S004', 'SP004', 70,35, 2450, '2024-06-10', '2025-06-10', '2024-10-02'),
                                                                                                                                                                                           ('RD006', '20241002005', 'S005', 'SP005', 90,40, 3600, '2024-05-25', '2025-05-25', '2024-10-02'),
                                                                                                                                                                                           ('RD007', '20241002006', 'S006', 'SP030', 100, 28, 2800, '2024-09-01', '2025-09-01', '2024-10-02'),
                                                                                                                                                                                           ('RD008', '20241002006', 'S007', 'SP033', 50, 38, 1900, '2024-09-01', '2025-09-01', '2024-10-02'),
                                                                                                                                                                                           ('RD009', '20241002007', 'S007', 'SP033', 50, 26, 1300, '2024-09-01', '2025-09-01', '2024-10-02'),
                                                                                                                                                                                           ('RD010', '20241002008', 'S008', 'SP035', 80, 32, 2560, '2024-07-20', '2025-07-20', '2024-10-02'),
                                                                                                                                                                                           ('RD011', '20241002009', 'S009', 'SP038', 70, 39, 2730, '2024-06-10', '2025-06-10', '2024-10-02'),
                                                                                                                                                                                           ('RD012', '20241002010', 'S010', 'SP039', 90, 42, 3780, '2024-05-25', '2025-05-25', '2024-10-02');

-- 插入付款資料
INSERT INTO payments (payment_id, account_id, payment_date, payment_method, total_amount, payment_status) VALUES
                                                                                                              ('PMT006', 'ACC001', '2024-10-10', '銀行轉帳', 11300, '已支付'),
                                                                                                              ('PMT007', 'ACC006', '2024-10-11', '銀行轉帳', 2800, '已支付'),
                                                                                                              ('PMT008', 'ACC007', '2024-10-11', '銀行轉帳', 2000, '部分支付'),
                                                                                                              ('PMT009', 'ACC008', '2024-10-11', '銀行轉帳', 2560, '已支付'),
                                                                                                              ('PMT010', 'ACC009', '2024-10-11', '銀行轉帳', 2730, '已支付'),
                                                                                                              ('PMT011', 'ACC010', '2024-10-11', '銀行轉帳', 1500, '部分支付');

-- 插入付款記錄資料，對應 detail_id
INSERT INTO payment_records (record_id, payment_id, detail_id, payment_amount) VALUES
                                                                                   ('REC001', 'PMT006', 'RD001', 2000), -- S001: RD001
                                                                                   ('REC002', 'PMT006', 'RD002', 2000), -- S002: RD002 部分支付
                                                                                   ('REC003', 'PMT006', 'RD003', 1250), -- S002: RD003
                                                                                   ('REC004', 'PMT006', 'RD004', 2400), -- S003: RD004
                                                                                   ('REC005', 'PMT006', 'RD005', 2450), -- S004: RD005
                                                                                   ('REC006', 'PMT006', 'RD006', 1200),
                                                                                   ('REC007', 'PMT007', 'RD007', 2800),  -- S006: RD007
                                                                                   ('REC008', 'PMT008', 'RD008', 1900),  -- S007: RD008 部分支付
                                                                                   ('REC009', 'PMT008', 'RD009', 100),   -- S007: RD009 部分支付
                                                                                   ('REC010', 'PMT009', 'RD010', 2560),  -- S008: RD010
                                                                                   ('REC011', 'PMT010', 'RD011', 2730),  -- S009: RD011
                                                                                   ('REC012', 'PMT011', 'RD012', 1500);  -- S010: RD012 部分支付; -- S005: RD006 部分支付

-- 插入職級和員工
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
	('E006', '阿鼎', '0900987987', 'F123456789', 'aac0005@gmail.com', '0000', 'M02', '2024-06-13', NULL,1, NULL),
	('E007', '阿文', '0933654654', 'G123456789', 'aaa0006@gmail.com', '0000', 'M03', '2024-06-20', '2024-08-31',0, NULL),
	('E008', '阿航', '0974147414', 'H123456789', 'aaa0007@gmail.com', '0000', 'M02', '2024-06-20', '2024-10-15',0, NULL),
	('E009', '阿棟', '0955885885', 'J123456789', 'aaa0008@gmail.com', '0000', 'M03', '2024-06-21', '2024-10-20',0, NULL),
	('E010', '阿璋', '0911236236', 'K123456789', 'aaa0009@gmail.com', '0000', 'M03', '2024-07-12', NULL,1, NULL),
	('E011', '阿貓', '0914236556', 'L123456789', 'aaa0010@gmail.com', '0000', 'M03', '2024-09-18', NULL,1, NULL),
	('E012', '卅囉', '0914244556', 'M123456789', 'aaa0011@gmail.com', '0000', 'M03', '2024-09-23', NULL,1, NULL),
	('E013', '尤離子', '0932246577', 'N123456789', 'aaa0012@gmail.com', '0000', 'M03', '2024-10-07', NULL,1, NULL),
	('E014', '一沐日', '0936226577', 'O123456789', 'aaa0013@gmail.com', '0000', 'M02', '2024-10-07', NULL,1, NULL),
	('E015', '柯珊芻', '0930611630', 'P123456789', 'aaa0014@gmail.com', '0000', 'M02', '2024-10-16', NULL,1, NULL);

-- 插入聊天
INSERT INTO chat_messages (from_user, to_user, content, timestamp)
VALUES 
-- 阿綸(E001)發送給其他人的訊息
('E001', 'E002', '哲哥早安，今天下午兩點的專案會議記得要準備第一季的銷售報表喔', '2024-06-15 08:30:00'),
('E001', 'E003', '瑋瑋，可以幫我看一下系統維護的時程安排嗎？客戶在催了', '2024-07-05 09:15:00'),
('E001', 'E006', '阿鼎，下週的部門預算會議的資料準備得如何了？', '2024-08-12 10:20:00'),
('E001', 'E010', '璋哥，新來的實習生我先安排在你那邊學習可以嗎？', '2024-09-05 11:30:00'),
('E001', 'E002', '記得下班前把會議紀錄傳給我過目一下，謝謝', '2024-10-15 16:45:00'),

-- 阿哲(E002)發送給其他人的訊息
('E002', 'E001', '好的綸哥，我已經整理好報表了，等等會先傳給你過目', '2024-06-15 08:35:00'),
('E002', 'E006', '鼎哥，技術部那邊的新系統測試結果出來了嗎？', '2024-07-18 13:20:00'),
('E002', 'E003', '瑋瑋，麻煩幫我看一下伺服器負載的問題，客戶反應系統有點慢', '2024-08-25 14:15:00'),
('E002', 'E010', '下週二的教育訓練你要來分享業務開發經驗對吧？簡報準備好了嗎', '2024-09-18 15:30:00'),
('E002', 'E001', '綸哥，會議紀錄已經發到你信箱了，請過目', '2024-10-15 17:30:00'),

-- 阿瑋(E003)發送給其他人的訊息
('E003', 'E001', '好的主管，我正在處理系統維護的事情，預計下午就能給你時程表', '2024-06-20 09:20:00'),
('E003', 'E002', '哲哥，我剛檢查過了，是資料庫需要優化，我已經在處理了', '2024-07-28 14:20:00'),
('E003', 'E006', '鼎哥，新功能的測試環境我已經架設好了，你要來驗收嗎？', '2024-08-15 11:45:00'),
('E003', 'E010', '璋哥，幫我看一下這個客戶的需求規格是不是有點問題？', '2024-09-22 13:50:00'),
('E003', 'E001', '主管，維護時程表已經放在共用資料夾了，麻煩你過目', '2024-10-18 15:00:00'),

-- 阿鼎(E006)發送給其他人的訊息
('E006', 'E001', '主管，部門預算報告已經完成初稿，我等等傳給你', '2024-06-25 10:25:00'),
('E006', 'E002', '哲哥，系統測試還在進行中，預計明天上午能出結果', '2024-08-02 13:25:00'),
('E006', 'E003', '收到，我下午三點過去看測試環境', '2024-09-10 11:50:00'),
('E006', 'E010', '小璋，聽說你接了一個大案子？需要支援嗎？', '2024-09-28 14:30:00'),
('E006', 'E001', '主管，預算報告已經發到你信箱了，請查收', '2024-10-20 16:15:00'),

-- 阿璋(E010)發送給其他人的訊息
('E010', 'E001', '好的主管，我很樂意帶新人，請安排他過來吧', '2024-07-15 11:35:00'),
('E010', 'E002', '是的，簡報已經準備好了，主要會分享最近幾個成功案例', '2024-08-20 15:35:00'),
('E010', 'E003', '謝謝提醒，我看過規格書了，確實有幾個點需要釐清，我再和客戶確認', '2024-09-15 14:00:00'),
('E010', 'E006', '謝謝關心，目前還可以應付，如果需要支援我再告訴你', '2024-10-01 14:35:00'),
('E010', 'E001', '主管，新人的學習計劃我已經打好草稿，等等傳給你參考', '2024-10-22 16:00:00');

-- 插入會員
INSERT INTO customer (customer_tel, customer_name, customer_email, date_of_registration, total_points)
VALUES
('0912345678', '陳大文', 'dawen.chen@email.com', '2024-06-15', 8),
('0923456789', '林小美', 'xiaomei.lin@email.com', '2024-06-18', 12),
('0934567890', '王建國', 'jianguo.wang@email.com', '2024-06-21', 5),
('0945678901', '張美玲', 'meiling.zhang@email.com', '2024-06-24', 15),
('0956789012', '李志明', 'zhiming.li@email.com', '2024-06-27', 3),
('0967890123', '吳淑芬', 'shufen.wu@email.com', '2024-06-30', 7),
('0978901234', '黃麗華', 'lihua.huang@email.com', '2024-07-03', 11),
('0989012345', '劉俊傑', 'junjie.liu@email.com', '2024-07-06', 4),
('0990123456', '周雅婷', 'yating.zhou@email.com', '2024-07-09', 9),
('0901234567', '謝宗翰', 'zonghan.xie@email.com', '2024-07-12', 13),
('0912345679', '楊佳琳', 'jialin.yang@email.com', '2024-07-15', 6),
('0923456788', '郭雅文', 'yawen.guo@email.com', '2024-07-18', 2),
('0934567899', '許志豪', 'zhihao.xu@email.com', '2024-07-21', 14),
('0945678900', '朱美珍', 'meizhen.zhu@email.com', '2024-07-24', 8),
('0956789013', '潘建志', 'jianzhi.pan@email.com', '2024-07-27', 11),
('0967890124', '范文芳', 'wenfang.fan@email.com', '2024-07-30', 5),
('0978901235', '蔡明宏', 'minghong.cai@email.com', '2024-08-02', 15),
('0989012346', '沈雅琪', 'yaqi.shen@email.com', '2024-08-05', 7),
('0990123457', '馮志偉', 'zhiwei.feng@email.com', '2024-08-08', 3),
('0901234568', '鄭惠美', 'huimei.zheng@email.com', '2024-08-11', 12),
('0912345670', '盧建宇', 'jianyu.lu@email.com', '2024-08-14', 6),
('0923456781', '唐淑華', 'shuhua.tang@email.com', '2024-08-17', 9),
('0934567892', '葉俊賢', 'junxian.ye@email.com', '2024-08-20', 4),
('0945678903', '施雅芳', 'yafang.shi@email.com', '2024-08-23', 13),
('0956789014', '彭建國', 'jianguo.peng@email.com', '2024-08-26', 8),
('0967890125', '董麗娟', 'lijuan.dong@email.com', '2024-08-29', 2),
('0978901236', '江志成', 'zhicheng.jiang@email.com', '2024-09-01', 10),
('0989012347', '侯雅玲', 'yaling.hou@email.com', '2024-09-04', 15),
('0990123458', '韓建華', 'jianhua.han@email.com', '2024-09-07', 7),
('0901234569', '傅淑惠', 'shuhui.fu@email.com', '2024-09-10', 5),
('0912345671', '趙明德', 'mingde.zhao@email.com', '2024-09-13', 11),
('0923456782', '童雅萍', 'yaping.tong@email.com', '2024-09-16', 3),
('0934567893', '方志遠', 'zhiyuan.fang@email.com', '2024-09-19', 14),
('0945678904', '白淑芳', 'shufang.bai@email.com', '2024-09-22', 6),
('0956789015', '石建龍', 'jianlong.shi@email.com', '2024-09-25', 9),
('0967890126', '龔雅如', 'yaru.gong@email.com', '2024-09-28', 12),
('0978901237', '何志強', 'zhiqiang.he@email.com', '2024-10-01', 4),
('0989012348', '湯雅婷', 'yating.tang@email.com', '2024-10-04', 8),
('0990123459', '任建成', 'jiancheng.ren@email.com', '2024-10-07', 15),
('0901234560', '巫淑玲', 'shuling.wu@email.com', '2024-10-10', 7),
('0912345672', '康明山', 'mingshan.kang@email.com', '2024-10-13', 10),
('0923456783', '章雅惠', 'yahui.zhang@email.com', '2024-10-16', 5),
('0934567894', '馬志豪', 'zhihao.ma@email.com', '2024-10-18', 13),
('0945678905', '時淑貞', 'shuzhen.shi@email.com', '2024-10-19', 2),
('0956789016', '嚴建文', 'jianwen.yan@email.com', '2024-10-20', 11),
('0967890127', '米雅莉', 'yali.mi@email.com', '2024-10-21', 6),
('0978901238', '梁志明', 'zhiming.liang@email.com', '2024-10-22', 9),
('0989012349', '湯雅晴', 'yaqing.tang@email.com', '2024-10-23', 4),
('0990123450', '邱建安', 'jianan.qiu@email.com', '2024-10-24', 14),
('0901234561', '溫淑儀', 'shuyi.wen@email.com', '2024-10-24', 8),
('0955234563', '李姍姍', 'delete.li@email.com', '2024-10-26', 10);

-- 插入 ask_for_leave 請假表資料
INSERT INTO ask_for_leave (leave_id, employee_id, start_time, end_time, reason_leave, proof_image, approved_status, category_id, leave_hours, rejection_reason)
VALUES
('L00001', 'E001', '2024-10-21 08:00', '2024-10-21 12:00', '因健康問題請假', NULL, '已批准', 2, 4, NULL),
('L00002', 'E002', '2024-10-21 12:00', '2024-10-21 16:00', '因家庭需求請假', NULL, '待審核', 3, 4, NULL),
('L00003', 'E003', '2024-10-22 08:00', '2024-10-22 12:00', '參加婚禮請假', NULL, '已退簽', 4, 4, '缺證明'),
('L00004', 'E004', '2024-10-22 12:00', '2024-10-22 16:00', '因個人事務請假', NULL, '已批准', 1, 4, NULL),
('L00005', 'E005', '2024-10-23 08:00', '2024-10-23 12:00', '因父母探病請假', NULL, '已批准', 2, 4, NULL),
('L00006', 'E006', '2024-10-23 12:00', '2024-10-23 16:00', '因生病請假', NULL, '待審核', 2, 4, NULL),
('L00007', 'E010', '2024-10-24 08:00', '2024-10-24 12:00', '因家事請假', NULL, '已批准', 1, 4, NULL),
('L00008', 'E011', '2024-10-24 12:00', '2024-10-24 16:00', '因朋友婚禮請假', NULL, '已批准', 4, 4, NULL),
('L00009', 'E012', '2024-10-25 08:00', '2024-10-25 12:00', '因健康問題請假', NULL, '待審核', 2, 4, NULL),
('L00010', 'E013', '2024-10-25 12:00', '2024-10-25 16:00', '因家裡有事請假', NULL, '已批准', 1, 4, NULL),
('L00011', 'E014', '2024-10-26 08:00', '2024-10-26 12:00', '因生病請假', NULL, '已批准', 2, 4, NULL),
('L00012', 'E001', '2024-10-26 12:00', '2024-10-26 16:00', '因參加講座請假', NULL, '已批准', 3, 4, NULL),
('L00013', 'E002', '2024-10-27 08:00', '2024-10-27 12:00', '因旅行請假', NULL, '待審核', 5, 4, NULL),
('L00014', 'E003', '2024-10-27 12:00', '2024-10-27 16:00', '因家庭需求請假', NULL, '已批准', 1, 4, NULL),
('L00015', 'E004', '2024-10-28 08:00', '2024-10-28 12:00', '因生病請假', NULL, '已批准', 2, 4, NULL),
('L00016', 'E005', '2024-10-28 12:00', '2024-10-28 16:00', '因健康問題請假', NULL, '已批准', 3, 4, NULL),
('L00017', 'E010', '2024-10-29 08:00', '2024-10-29 12:00', '因家庭需求請假', NULL, '已批准', 4, 4, NULL),
('L00018', 'E011', '2024-10-29 12:00', '2024-10-29 16:00', '因親人婚禮請假', NULL, '已批准', 5, 4, NULL),
('L00019', 'E012', '2024-10-30 08:00', '2024-10-30 12:00', '因病假請假', NULL, '已批准', 2, 4, NULL),
('L00020', 'E013', '2024-10-30 12:00', '2024-10-30 16:00', '因事務請假', NULL, '已批准', 1, 4, NULL),
('L00021', 'E003', '2024-10-22 08:00', '2024-10-22 12:00', '因健康問題請假', NULL, '已批准', 2, 4, NULL),
('L00022', 'E003', '2024-10-24 12:00', '2024-10-24 16:00', '因家庭需求請假', NULL, '已批准', 3, 4, NULL),
('L00023', 'E003', '2024-10-26 08:00', '2024-10-26 12:00', '因參加婚禮請假', NULL, '已批准', 4, 4, NULL),
('L00024', 'E003', '2024-10-28 12:00', '2024-10-28 16:00', '因旅行請假', NULL, '待審核', 5, 4, NULL),
('L00025', 'E003', '2024-11-01 08:00', '2024-11-01 12:00', '因家庭需求請假', NULL, '已批准', 1, 4, NULL),
('L00026', 'E003', '2024-11-02 12:00', '2024-11-02 16:00', '因工作需求請假', NULL, '已批准', 2, 4, NULL),
('L00027', 'E003', '2024-11-05 08:00', '2024-11-05 12:00', '因健康問題請假', NULL, '已批准', 3, 4, NULL),
('L00028', 'E003', '2024-11-10 12:00', '2024-11-10 16:00', '因家庭需求請假', NULL, '已批准', 4, 4, NULL),
('L00029', 'E003', '2024-11-15 08:00', '2024-11-15 12:00', '因事務請假', NULL, '已批准', 2, 4, NULL);

-- 插入請假記錄
INSERT INTO leave_record (employee_id, category_id, expiration_date, actual_hours, limit_hours)
VALUES
('E001', 1, '2025-06-13', 4, 112),
('E001', 2, '2025-06-13', 8, 240),
('E002', 2, '2025-06-13', 4, 240),
('E002', 3, '2025-06-13', 8, 24),
('E003', 1, '2025-06-13', 8, 112),
('E003', 4, '2025-06-13', 20, 64),
('E004', 1, '2025-06-13', 8, 112),
('E004', 5, '2025-06-13', 4, 64),
('E005', 1, '2025-06-13', 4, 112),
('E005', 2, '2025-06-13', 8, 240),
('E010', 2, '2025-06-13', 8, 240),
('E011', 1, '2025-06-13', 8, 112),
('E012', 4, '2025-06-13', 8, 64),
('E012', 2, '2025-06-13', 4, 240),
('E013', 1, '2025-06-13', 8, 112),
('E013', 2, '2025-06-13', 4, 240),
('E014', 3, '2025-06-13', 8, 24);

--- 插入請假類別
INSERT INTO leave_category (leave_type, max_hours)
VALUES
('事假', 112),
('病假', 240),           
('特休假', 240),
('婚假', 64),         
('喪假', 64),          
('公傷假', 240),        
('公假', 240);          

-- 插入排班表資料
INSERT INTO schedule (employee_id, schedule_date, start_time, end_time, schedule_hour, schedule_active) VALUES
('E001', '2024-10-21', '08:00', '12:00', 4, 1),
('E002', '2024-10-21', '08:00', '12:00', 4, 1),
('E003', '2024-10-21', '12:00', '16:00', 4, 1),
('E004', '2024-10-21', '12:00', '16:00', 4, 1),
('E005', '2024-10-22', '08:00', '12:00', 4, 1),
('E006', '2024-10-22', '08:00', '12:00', 4, 1),
('E010', '2024-10-22', '12:00', '16:00', 4, 1),
('E001', '2024-10-22', '12:00', '16:00', 4, 1),
('E002', '2024-10-23', '08:00', '12:00', 4, 1),
('E010', '2024-10-23', '08:00', '12:00', 4, 1),
('E011', '2024-10-23', '12:00', '16:00', 4, 1),
('E001', '2024-10-23', '12:00', '16:00', 4, 1),
('E002', '2024-10-24', '08:00', '12:00', 4, 1),
('E003', '2024-10-24', '08:00', '12:00', 4, 1),
('E004', '2024-10-24', '12:00', '16:00', 4, 1),
('E005', '2024-10-24', '12:00', '16:00', 4, 1),
('E006', '2024-10-25', '08:00', '12:00', 4, 1),
('E004', '2024-10-25', '08:00', '12:00', 4, 1),
('E005', '2024-10-25', '12:00', '16:00', 4, 1),
('E006', '2024-10-25', '12:00', '16:00', 4, 1),
('E010', '2024-10-26', '08:00', '12:00', 4, 1),
('E011', '2024-10-26', '08:00', '12:00', 4, 1),
('E001', '2024-10-26', '12:00', '16:00', 4, 1),
('E002', '2024-10-26', '12:00', '16:00', 4, 1),
('E003', '2024-10-27', '08:00', '12:00', 4, 1),
('E004', '2024-10-27', '08:00', '12:00', 4, 1),
('E005', '2024-10-27', '12:00', '16:00', 4, 1),
('E006', '2024-10-27', '12:00', '16:00', 4, 1),
('E011', '2024-10-28', '08:00', '12:00', 4, 1),
('E005', '2024-10-28', '08:00', '12:00', 4, 1),
('E009', '2024-10-28', '12:00', '16:00', 4, 1),
('E010', '2024-10-28', '12:00', '16:00', 4, 1),
('E011', '2024-10-29', '08:00', '12:00', 4, 1),
('E001', '2024-10-29', '08:00', '12:00', 4, 1),
('E002', '2024-10-29', '12:00', '16:00', 4, 1),
('E003', '2024-10-29', '12:00', '16:00', 4, 1),
('E004', '2024-10-30', '08:00', '12:00', 4, 1),
('E005', '2024-10-30', '08:00', '12:00', 4, 1),
('E006', '2024-10-30', '12:00', '16:00', 4, 1),
('E003', '2024-10-30', '12:00', '16:00', 4, 1),
('E004', '2024-10-31', '08:00', '12:00', 4, 1),
('E005', '2024-10-31', '08:00', '12:00', 4, 1),
('E010', '2024-10-31', '12:00', '16:00', 4, 1),
('E011', '2024-10-31', '12:00', '16:00', 4, 1),
('E001', '2024-11-01', '08:00', '12:00', 4, 1),
('E002', '2024-11-01', '08:00', '12:00', 4, 1),
('E003', '2024-11-01', '12:00', '16:00', 4, 1),
('E004', '2024-11-01', '12:00', '16:00', 4, 1),
('E005', '2024-11-02', '08:00', '12:00', 4, 1),
('E006', '2024-11-02', '08:00', '12:00', 4, 1),
('E005', '2024-11-02', '12:00', '16:00', 4, 1),
('E003', '2024-11-02', '12:00', '16:00', 4, 1),
('E001', '2024-11-03', '08:00', '12:00', 4, 1),
('E010', '2024-11-03', '08:00', '12:00', 4, 1),
('E011', '2024-11-03', '12:00', '16:00', 4, 1),
('E001', '2024-11-03', '12:00', '16:00', 4, 1),
('E002', '2024-11-04', '08:00', '12:00', 4, 1),
('E003', '2024-11-04', '08:00', '12:00', 4, 1),
('E004', '2024-11-04', '12:00', '16:00', 4, 1),
('E005', '2024-11-04', '12:00', '16:00', 4, 1),
('E006', '2024-11-05', '08:00', '12:00', 4, 1),
('E002', '2024-11-05', '08:00', '12:00', 4, 1),
('E003', '2024-11-05', '12:00', '16:00', 4, 1),
('E005', '2024-11-05', '12:00', '16:00', 4, 1),
('E010', '2024-11-06', '08:00', '12:00', 4, 1),
('E011', '2024-11-06', '08:00', '12:00', 4, 1),
('E001', '2024-11-06', '12:00', '16:00', 4, 1),
('E002', '2024-11-06', '12:00', '16:00', 4, 1),
('E003', '2024-11-07', '08:00', '12:00', 4, 1),
('E004', '2024-11-07', '08:00', '12:00', 4, 1),
('E005', '2024-11-07', '12:00', '16:00', 4, 1),
('E006', '2024-11-07', '12:00', '16:00', 4, 1),
('E004', '2024-11-08', '08:00', '12:00', 4, 1),
('E010', '2024-11-08', '08:00', '12:00', 4, 1),
('E004', '2024-11-08', '12:00', '16:00', 4, 1),
('E010', '2024-11-08', '12:00', '16:00', 4, 1),
('E011', '2024-11-09', '08:00', '12:00', 4, 1),
('E001', '2024-11-09', '08:00', '12:00', 4, 1),
('E002', '2024-11-09', '12:00', '16:00', 4, 1),
('E003', '2024-11-09', '12:00', '16:00', 4, 1),
('E004', '2024-11-10', '08:00', '12:00', 4, 1),
('E005', '2024-11-10', '08:00', '12:00', 4, 1),
('E006', '2024-11-10', '12:00', '16:00', 4, 1),
('E011', '2024-11-10', '12:00', '16:00', 4, 1),
('E001', '2024-11-11', '08:00', '12:00', 4, 1),
('E002', '2024-11-11', '08:00', '12:00', 4, 1),
('E010', '2024-11-11', '12:00', '16:00', 4, 1),
('E011', '2024-11-11', '12:00', '16:00', 4, 1),
('E001', '2024-11-12', '08:00', '12:00', 4, 1),
('E002', '2024-11-12', '08:00', '12:00', 4, 1),
('E003', '2024-11-12', '12:00', '16:00', 4, 1),
('E004', '2024-11-12', '12:00', '16:00', 4, 1),
('E005', '2024-11-13', '08:00', '12:00', 4, 1),
('E006', '2024-11-13', '08:00', '12:00', 4, 1),
('E003', '2024-11-13', '12:00', '16:00', 4, 1),
('E004', '2024-11-13', '12:00', '16:00', 4, 1),
('E005', '2024-11-14', '08:00', '12:00', 4, 1),
('E010', '2024-11-14', '08:00', '12:00', 4, 1),
('E011', '2024-11-14', '12:00', '16:00', 4, 1),
('E001', '2024-11-14', '12:00', '16:00', 4, 1),
('E002', '2024-11-15', '08:00', '12:00', 4, 1),
('E003', '2024-11-15', '08:00', '12:00', 4, 1),
('E004', '2024-11-15', '12:00', '16:00', 4, 1),
('E005', '2024-11-15', '12:00', '16:00', 4, 1),
('E006', '2024-11-16', '08:00', '12:00', 4, 1),
('E010', '2024-11-16', '08:00', '12:00', 4, 1),
('E011', '2024-11-16', '12:00', '16:00', 4, 1),
('E009', '2024-11-16', '12:00', '16:00', 4, 1),
('E010', '2024-11-17', '08:00', '12:00', 4, 1),
('E011', '2024-11-17', '08:00', '12:00', 4, 1),
('E001', '2024-11-17', '12:00', '16:00', 4, 1),
('E002', '2024-11-17', '12:00', '16:00', 4, 1),
('E003', '2024-11-18', '08:00', '12:00', 4, 1),
('E004', '2024-11-18', '08:00', '12:00', 4, 1),
('E005', '2024-11-18', '12:00', '16:00', 4, 1),
('E006', '2024-11-18', '12:00', '16:00', 4, 1),
('E001', '2024-11-19', '08:00', '12:00', 4, 1),
('E002', '2024-11-19', '08:00', '12:00', 4, 1),
('E003', '2024-11-19', '12:00', '16:00', 4, 1),
('E010', '2024-11-19', '12:00', '16:00', 4, 1),
('E011', '2024-11-20', '08:00', '12:00', 4, 1),
('E001', '2024-11-20', '08:00', '12:00', 4, 1),
('E002', '2024-11-20', '12:00', '16:00', 4, 1),
('E003', '2024-11-20', '12:00', '16:00', 4, 1),
('E004', '2024-11-21', '08:00', '12:00', 4, 1),
('E005', '2024-11-21', '08:00', '12:00', 4, 1),
('E006', '2024-11-21', '12:00', '16:00', 4, 1),
('E004', '2024-11-21', '12:00', '16:00', 4, 1);

-- 插入結帳單
INSERT INTO checkout (checkout_id,invoice_number , customer_tel, employee_id, checkout_total_price,checkout_date,bonus_points,points_due_date, checkout_status, related_return_id)
VALUES
	('C00000001','IN00000001','0912345678', 'E001', 3260 , '2024-10-28',32, '2025-10-28','已退貨','T00000001'),
	('C00000002','IN00000002','', 'E002', 125 , '2024-10-28',0, '','已退貨','T00000002'),
	('C00000003','IN00000003','0912345678', 'E003', 1500 , '2024-10-29',15, '2025-10-29','已退貨','T00000003'),
	('C00000004', 'IN00000004', '0923456789', 'E001', 1280, '2024-10-29', 12, '2025-10-29', '正常', ''),
    ('C00000005', 'IN00000005', '0934567890', 'E002', 750, '2024-10-29', 7, '2025-10-29', '正常', ''),
    ('C00000006', 'IN00000006', '0912345678', 'E004', 960, '2024-10-29', 9, '2025-10-29', '正常', ''),
    ('C00000007', 'IN00000007', '', 'E005', 175, '2024-10-30', 0, '', '正常', ''),
    ('C00000008', 'IN00000008', '0912345678', 'E006', 880, '2024-10-30', 8, '2025-10-30', '正常', ''),
    ('C00000009', 'IN00000009', '0923456789', 'E002', 785, '2024-10-30', 7, '2025-10-30', '正常', ''),
    ('C00000010', 'IN00000010', '', 'E003', 320, '2024-10-31', 0, '', '正常', ''),
    ('C00000011', 'IN00000011', '0934567890', 'E004', 1250, '2024-10-31', 12, '2025-10-31', '正常', ''),
    ('C00000012', 'IN00000012', '0945678901', 'E001', 460, '2024-10-31', 4, '2025-10-31', '正常', ''),
    ('C00000013', 'IN00000013', '0956789012', 'E005', 890, '2024-11-01', 8, '2025-11-01', '正常', ''),
    ('C00000014', 'IN00000014', '', 'E002', 155, '2024-11-01', 0, '', '正常', ''),
    ('C00000015', 'IN00000015', '0967890123', 'E003', 2450, '2024-11-01', 24, '2025-11-01', '正常', ''),
    ('C00000016', 'IN00000016', '0978901234', 'E004', 680, '2024-11-02', 6, '2025-11-02', '正常', ''),
    ('C00000017', 'IN00000017', '0989012345', 'E001', 1580, '2024-11-02', 15, '2025-11-02', '正常', ''),
    ('C00000018', 'IN00000018', '0990123456', 'E005', 920, '2024-11-02', 9, '2025-11-02', '正常', ''),
    ('C00000019', 'IN00000019', '', 'E002', 245, '2024-11-03', 0, '', '正常', ''),
    ('C00000020', 'IN00000020', '0901234567', 'E003', 1890, '2024-11-03', 18, '2025-11-03', '正常', ''),
    ('C00000021', 'IN00000021', '0912345679', 'E004', 730, '2024-11-03', 7, '2025-11-03', '正常', ''),
    ('C00000022', 'IN00000022', '0923456788', 'E001', 3240, '2024-11-04', 32, '2025-11-04', '正常', ''),
    ('C00000023', 'IN00000023', '0934567899', 'E005', 450, '2024-11-04', 4, '2025-11-04', '正常', ''),
    ('C00000024', 'IN00000024', '0945678900', 'E002', 1670, '2024-11-05', 16, '2025-11-05', '正常', ''),
    ('C00000025', 'IN00000025', '0956789013', 'E003', 980, '2024-11-05', 9, '2025-11-05', '正常', '');
	
-- 插入結帳明細        
INSERT INTO checkout_details(checkout_id,product_id,number_of_checkout,product_price,checkout_price)
VALUES
	('C00000001', 'PMS002', 10, 320, 3200),
	('C00000001', 'PVF001', 3, 20, 60),
	('C00000002', 'PSN001', 5, 25, 125),
	('C00000003', 'PRN001', 8, 150, 1200),
	('C00000003', 'PDR001', 10, 30, 300),
    ('C00000004', 'PMS001', 5, 180, 900),
    ('C00000004', 'PVF001', 8, 35, 280),
    ('C00000004', 'PDR001', 4, 25, 100),
    ('C00000005', 'PRN001', 5, 150, 750),
    ('C00000006', 'PMS003', 5, 150, 750),
    ('C00000006', 'PVF002', 6, 25, 150),
    ('C00000006', 'PDR002', 2, 25, 60),
    ('C00000007', 'PSN001', 7, 25, 175),
    ('C00000008', 'PMS004', 4, 160, 640),
    ('C00000008', 'PVF003', 12, 20, 240),
    ('C00000009', 'PMS001', 3, 180, 540),
    ('C00000009', 'PVF001', 7, 35, 245),
    ('C00000010', 'PMS002', 1, 320, 320),
    ('C00000011', 'PRN001', 5, 150, 750),
    ('C00000011', 'PDR001', 20, 25, 500),
    ('C00000012', 'PSN001', 8, 25, 200),
    ('C00000012', 'PVF002', 6, 25, 150),
    ('C00000012', 'PDR002', 4, 25, 110),
    ('C00000013', 'PMS003', 4, 150, 600),
    ('C00000013', 'PVF003', 15, 20, 290),
    ('C00000014', 'PSN002', 5, 25, 125),
    ('C00000014', 'PDR004', 2, 20, 30),
    ('C00000015', 'PMS002', 5, 320, 1600),
    ('C00000015', 'PRN002', 10, 85, 850),
    ('C00000016', 'PVF004', 12, 30, 360),
    ('C00000016', 'PDR003', 8, 35, 320),
    ('C00000017', 'PMS004', 7, 160, 1120),
    ('C00000017', 'PSN003', 23, 20, 460),
    ('C00000018', 'PMS005', 3, 200, 600),
    ('C00000018', 'PVF005', 7, 45, 320),
    ('C00000019', 'PSN004', 7, 35, 245),
    ('C00000020', 'PMS001', 8, 180, 1440),
    ('C00000020', 'PRN003', 10, 45, 450),
    ('C00000021', 'PVF001', 15, 35, 525),
    ('C00000021', 'PDR005', 8, 25, 205),
    ('C00000022', 'PMS002', 8, 320, 2560),
    ('C00000022', 'PVF002', 25, 25, 680),
    ('C00000023', 'PSN005', 15, 30, 450),
    ('C00000024', 'PMS003', 8, 150, 1200),
    ('C00000024', 'PRN004', 5, 95, 470),
    ('C00000025', 'PVF003', 25, 20, 500),
    ('C00000025', 'PDR001', 18, 25, 480);

-- 插入退貨單
INSERT INTO return_products (return_id,original_checkout_id,original_invoice_number, employee_id , return_total_price , return_date)
VALUES
	('T00000001', 'C00000001', 'IN00000001', 'E001', 320 , '2024-10-31'),
	('T00000002', 'C00000002', 'IN00000002', 'E002', 50,  '2024-11-01'),
	('T00000003', 'C00000003', 'IN00000003', 'E003', 750 , '2024-11-02');

-- 插入退貨明細
INSERT INTO return_details(return_id,original_checkout_id,product_id,reason_for_return,number_of_return,product_price,return_price,return_status,return_photo) 
VALUES
('T00000001', 'C00000001', 'PMS002', '牛肉臭酸',1, 320, 320,'商品品質異常',NULL),
('T00000002', 'C00000002', 'PSN001' , '乖乖買錯口味',2, 25, 50,'顧客因素',NULL),
('T00000003', 'C00000003','PRN001' , '米裡面有蟲',5, 150, 750,'商品品質異常',NULL);

-- 插入有會員點數做紅利兌換商品測試
INSERT INTO customer (customer_tel, customer_name, customer_email, date_of_registration, total_points)
VALUES
    ('0938123456', '陳平偉', 'cus021@email.com', CAST('2024-07-01' AS DATE), 500),
    ('0927456123', '林竣煒', 'cus022@email.com', CAST('2024-07-01' AS DATE), 200),
    ('0956234587', '陳曉明', 'chenxm@email.com', CAST('2024-01-05' AS DATE), 350),
    ('0935162783', '林美玲', 'linml@email.com', CAST('2024-02-10' AS DATE), 400),
    ('0968456237', '王大衛', 'wangdw@email.com', CAST('2024-03-15' AS DATE), 450),
    ('0912387564', '張惠美', 'zhanghm@email.com', CAST('2024-04-20' AS DATE), 500),
    ('0945784623', '李國強', 'ligq@email.com', CAST('2024-05-25' AS DATE), 600),
    ('0976789463', '趙子隆', 'zhaozl@email.com', CAST('2024-06-30' AS DATE), 700);

-- 插入紅利兌換商品表
INSERT INTO bonus_exchange
    (exchange_id, item_id, customer_tel, use_points, number_of_exchange, exchange_date)
VALUES
    ('EX00000001', 'IM005', '0912345678', 60, 1, '2024-10-15'),  -- 兌換雞肉
    ('EX00000002', 'IM006', '0923456789', 25, 2, '2024-10-16'),  -- 兌換小黃瓜
    ('EX00000003', 'IM007', '0934567890', 70, 1, '2024-10-17'),  -- 兌換蛤蠣
    ('EX00000004', 'IM008', '0945678901', 90, 1, '2024-10-18'),  -- 兌換空心菜
    ('EX00000005', 'IM009', '0956789012', 30, 2, '2024-10-19'),  -- 兌換魚
    ('EX00000006', 'IM005', '0967890123', 60, 1, '2024-10-20'),  -- 兌換雞肉
    ('EX00000007', 'IM007', '0978901234', 70, 1, '2024-10-21'),  -- 兌換蛤蠣
    ('EX00000008', 'IM008', '0989012345', 90, 1, '2024-10-22'),  -- 兌換空心菜
    ('EX00000009', 'IM009', '0990123456', 30, 1, '2024-10-23'),  -- 兌換魚
    ('EX00000010', 'IM010', '0901234567', 110, 1, '2024-10-24'); -- 兌換高麗菜

-- 插入可兌換商品管理表
-- 第一批活動商品(已結束) -- 6月到8月（已過期，因此狀態設為 0）
INSERT INTO item_management
    (item_id, product_id, item_points, item_maximum, start_date, end_date, is_active)
VALUES
    ('IM001', 'PMS001', 50, 100, '2024-06-01', '2024-08-31', 0),
    ('IM002', 'PVF001', 20, 50, '2024-06-01', '2024-08-31', 0),
    ('IM003', 'PMS002', 80, 30, '2024-06-01', '2024-08-31', 0),
    ('IM004', 'PVF002', 100, 60, '2024-06-01', '2024-08-31', 0),
    -- 第二批活動商品(進行中)  -- 10月到12月（目前為活動期間，狀態設為 1）
    ('IM005', 'PMS003', 60, 120, '2024-10-01', '2024-12-31', 1),
    ('IM006', 'PVF003', 25, 80, '2024-10-01', '2024-12-31', 1),
    ('IM007', 'PMS004', 70, 40, '2024-10-01', '2024-12-31', 1),
    ('IM008', 'PVF004', 90, 70, '2024-10-01', '2024-12-31', 1),
    ('IM009', 'PMS005', 30, 150, '2024-10-01', '2024-12-31', 1),
    ('IM010', 'PVF005', 110, 45, '2024-10-01', '2024-12-31', 1),
    -- 第三批活動商品(尚未開始)  -- 1月到2月（活動未開始，狀態設為 0）
    ('IM011', 'PMS001', 50, 100, '2025-01-01', '2025-02-28', 0),
    ('IM012', 'PMS002', 60, 90, '2025-01-01', '2025-02-28', 0),
    ('IM013', 'PVF001', 20, 50, '2025-01-01', '2025-02-28', 0),
    ('IM014', 'PVF002', 80, 60, '2025-01-01', '2025-02-28', 0);

-- 插入紅利紀錄表
INSERT INTO points_history
    (customer_tel, checkout_id, exchange_id, points_change, transaction_date, transaction_type)
VALUES
    -- 消費累積點數記錄
    ('0912345678', 'C00000001', NULL, 32, '2024-10-28', 'earn'),
    ('0912345678', 'C00000003', NULL, 15, '2024-10-29', 'earn'),
    ('0923456789', 'C00000004', NULL, 12, '2024-10-29', 'earn'),
    ('0934567890', 'C00000005', NULL, 7, '2024-10-29', 'earn'),
    ('0912345678', 'C00000006', NULL, 9, '2024-10-29', 'earn'),
    ('0912345678', 'C00000008', NULL, 8, '2024-10-30', 'earn'),
    ('0923456789', 'C00000009', NULL, 7, '2024-10-30', 'earn'),
    ('0934567890', 'C00000011', NULL, 12, '2024-10-31', 'earn'),
    ('0945678901', 'C00000012', NULL, 4, '2024-10-31', 'earn'),
    ('0956789012', 'C00000013', NULL, 8, '2024-11-01', 'earn'),
    ('0967890123', 'C00000015', NULL, 24, '2024-11-01', 'earn'),
    ('0978901234', 'C00000016', NULL, 6, '2024-11-02', 'earn'),
    ('0989012345', 'C00000017', NULL, 15, '2024-11-02', 'earn'),
    ('0990123456', 'C00000018', NULL, 9, '2024-11-02', 'earn'),
    ('0901234567', 'C00000020', NULL, 18, '2024-11-03', 'earn'),
    ('0912345679', 'C00000021', NULL, 7, '2024-11-03', 'earn'),
    ('0923456788', 'C00000022', NULL, 32, '2024-11-04', 'earn'),
    ('0934567899', 'C00000023', NULL, 4, '2024-11-04', 'earn'),
    ('0945678900', 'C00000024', NULL, 16, '2024-11-05', 'earn'),
    ('0956789013', 'C00000025', NULL, 9, '2024-11-05', 'earn'),
    -- 兌換使用點數記錄
    ('0912345678', NULL, 'EX00000001', -60, '2024-11-08', 'use'),
    ('0923456789', NULL, 'EX00000002', -25, '2024-11-08', 'use'),
    ('0934567890', NULL, 'EX00000003', -70, '2024-11-08', 'use'),
    ('0945678901', NULL, 'EX00000004', -90, '2024-11-09', 'use'),
    ('0956789012', NULL, 'EX00000005', -30, '2024-11-09', 'use');

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

ALTER TABLE schedule
ADD CONSTRAINT schedule_employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

ALTER TABLE schedule
ADD CONSTRAINT FK_shift_period
FOREIGN KEY (shift_id) REFERENCES shift_period(shift_id);

ALTER TABLE schedule
ADD CONSTRAINT FK_shift_quota
FOREIGN KEY (quota_id) REFERENCES shift_quota(quota_id);

ALTER TABLE shift_quota
ADD CONSTRAINT shift_quota_employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

-- 建立請假的employee_id與員工的employee_id外鍵關係
ALTER TABLE ask_for_leave
ADD CONSTRAINT ask_for_leave_employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

-- 建立請假的category_id與請假類別的category_id外鍵關係
ALTER TABLE ask_for_leave
ADD CONSTRAINT ask_for_leave_category_id_FK
FOREIGN KEY (category_id)
REFERENCES leave_category(category_id);

-- 外鍵關聯到員工表的 employee_id
ALTER TABLE leave_record
ADD CONSTRAINT leave_record_employee_id_FK
FOREIGN KEY (employee_id)
REFERENCES employee(employee_id);

-- 外鍵關聯到請假類別表的 category_id
ALTER TABLE leave_record
ADD CONSTRAINT leave_record_category_id_FK
FOREIGN KEY (category_id)
REFERENCES leave_category(category_id);

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

-- 紅利兌換商品的customer_tel與會員的customer_tel建立外鍵關係
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
SELECT * FROM inventory_check;
SELECT * FROM inventory_check_details;
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
SELECT * FROM notification;
SELECT * FROM schedule;
SELECT * FROM ask_for_leave;
SELECT * FROM leave_record;
SELECT * FROM leave_category;
SELECT * FROM customer;
SELECT * FROM item_management ;
SELECT * FROM bonus_exchange;
SELECT * FROM points_history;

--TRUNCATE TABLE products;
--TRUNCATE TABLE inventory_check;
--TRUNCATE TABLE inventory_check_details;
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
--TRUNCATE TABLE notification;
--TRUNCATE TABLE chat_messages;
--TRUNCATE TABLE schedule;
--TRUNCATE TABLE ask_for_leave;
--TRUNCATE TABLE leave_record;
--TRUNCATE TABLE leave_category;
--TRUNCATE TABLE customer;
--TRUNCATE TABLE item_management;
--TRUNCATE TABLE bonus_exchange;
--TRUNCATE TABLE points_history;

DROP TABLE products;
DROP TABLE inventory_check;
DROP TABLE inventory_check_details;
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
DROP TABLE notification;
DROP TABLE chat_messages;
DROP TABLE schedule;
DROP TABLE ask_for_leave;
DROP TABLE leave_record;
DROP TABLE leave_category;
DROP TABLE customer;
DROP TABLE item_management;
DROP TABLE bonus_exchange;
DROP TABLE points_history;