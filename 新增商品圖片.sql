use ispan;

-- 肉品海鮮類商品圖片更新
UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PMS001.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PMS001';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PMS002.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PMS002';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PMS003.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PMS003';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PMS004.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PMS004';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PMS005.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PMS005';

-- 蔬菜水果類商品圖片更新
UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PVF001.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PVF001';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PVF002.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PVF002';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PVF003.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PVF003';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PVF004.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PVF004';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PVF005.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PVF005';

-- 零食點心類商品圖片更新
UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PSN001.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PSN001';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PSN002.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PSN002';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PSN003.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PSN003';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PSN004.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PSN004';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PSN005.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PSN005';

-- 米飯麵條類商品圖片更新
UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PRN001.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PRN001';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PRN002.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PRN002';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PRN003.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PRN003';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PRN004.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PRN004';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PRN005.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PRN005';

-- 飲品類商品圖片更新
UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PDR001.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PDR001';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PDR002.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PDR002';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PDR003.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PDR003';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PDR004.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PDR004';

UPDATE products
SET product_photo = (
    SELECT BulkColumn 
    FROM OPENROWSET(BULK N'D:\photo\PDR005.jpg', SINGLE_BLOB) AS img
)
WHERE product_id = 'PDR005';