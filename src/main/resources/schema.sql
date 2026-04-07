CREATE DATABASE IF NOT EXISTS shopping_cart_localization
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;

CREATE TABLE IF NOT EXISTS cart_records (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    total_items  INT NOT NULL,
    total_cost   DOUBLE NOT NULL,
    language     VARCHAR(10),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    cart_record_id INT,
    item_number    INT NOT NULL,
    price          DOUBLE NOT NULL,
    quantity       INT NOT NULL,
    subtotal       DOUBLE NOT NULL,
    FOREIGN KEY (cart_record_id) REFERENCES cart_records(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS localization_strings (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    `key`    VARCHAR(100) NOT NULL,
    value    VARCHAR(255) NOT NULL,
    language VARCHAR(10) NOT NULL
);

INSERT INTO localization_strings (`key`, value, language) VALUES
('ui.selectLanguage',       'Select language:',                          'en'),
('prompt.itemCount',        'Enter the number of items to purchase:',    'en'),
('prompt.price',            'Price',                                     'en'),
('prompt.quantity',         'Quantity',                                  'en'),
('button.enterItems',       'Enter items',                               'en'),
('button.calculate',        'Calculate total',                           'en'),
('message.totalCost',       'Total cost:',                               'en'),
('error.nonNegativeInteger','Please enter a non-negative whole number.',  'en'),
('error.nonNegativeDecimal','Please enter a non-negative price.',         'en'),
('label.itemNumber',        'Item %d',                                   'en'),

('ui.selectLanguage',       'Valitse kieli:',                            'fi'),
('prompt.itemCount',        'Syötä ostettavien tuotteiden määrä:',       'fi'),
('prompt.price',            'Hinta',                                     'fi'),
('prompt.quantity',         'Määrä',                                     'fi'),
('button.enterItems',       'Syötä tuotteet',                            'fi'),
('button.calculate',        'Laske kokonaishinta',                       'fi'),
('message.totalCost',       'Kokonaishinta:',                            'fi'),
('error.nonNegativeInteger','Syötä ei-negatiivinen kokonaisluku.',        'fi'),
('error.nonNegativeDecimal','Syötä ei-negatiivinen hinta.',               'fi'),
('label.itemNumber',        'Tuote %d',                                  'fi'),

('ui.selectLanguage',       'Välj språk:',                               'sv'),
('prompt.itemCount',        'Ange antalet varor att köpa:',               'sv'),
('prompt.price',            'Pris',                                      'sv'),
('prompt.quantity',         'Mängd',                                     'sv'),
('button.enterItems',       'Ange varor',                                'sv'),
('button.calculate',        'Beräkna totalt',                            'sv'),
('message.totalCost',       'Total kostnad:',                            'sv'),
('error.nonNegativeInteger','Ange ett icke-negativt heltal.',             'sv'),
('error.nonNegativeDecimal','Ange ett icke-negativt pris.',               'sv'),
('label.itemNumber',        'Vara %d',                                   'sv'),

('ui.selectLanguage',       '言語を選択:',                                'ja'),
('prompt.itemCount',        '購入する商品の数を入力してください:',           'ja'),
('prompt.price',            '価格',                                      'ja'),
('prompt.quantity',         '数量',                                      'ja'),
('button.enterItems',       '商品を入力',                                 'ja'),
('button.calculate',        '合計を計算',                                 'ja'),
('message.totalCost',       '合計金額:',                                  'ja'),
('error.nonNegativeInteger','非負の整数を入力してください。',                'ja'),
('error.nonNegativeDecimal','非負の価格を入力してください。',                'ja'),
('label.itemNumber',        '商品 %d',                                   'ja'),

('ui.selectLanguage',       'اختر اللغة:',                              'ar'),
('prompt.itemCount',        'أدخل عدد العناصر:',                        'ar'),
('prompt.price',            'السعر',                                    'ar'),
('prompt.quantity',         'الكمية',                                   'ar'),
('button.enterItems',       'إدخال العناصر',                            'ar'),
('button.calculate',        'احسب الإجمالي',                            'ar'),
('message.totalCost',       'التكلفة الإجمالية:',                       'ar'),
('error.nonNegativeInteger','الرجاء إدخال عدد صحيح غير سالب.',          'ar'),
('error.nonNegativeDecimal','الرجاء إدخال سعر غير سالب.',               'ar'),
('label.itemNumber',        'العنصر %d',                                'ar');
