#!/bin/bash

# MySQLでの確認スクリプト
# 銀行ローン申請データを確認

echo "=========================================="
echo "銀行ローン申請データベース確認ツール"
echo "=========================================="
echo ""

# MySQLに接続してデータを確認
mysql -u root -proot_password internship << EOF
-- データベースの選択
USE internship;

-- テーブル一覧を表示
SHOW TABLES;

-- テーブルが存在する場合、そのスキーマを表示
SHOW CREATE TABLE bank_loan_form\G

-- データベース内のデータを表示
SELECT * FROM bank_loan_form\G

-- レコード数を表示
SELECT COUNT(*) as '総レコード数' FROM bank_loan_form;

EOF

echo ""
echo "=========================================="
echo "確認完了"
echo "=========================================="

