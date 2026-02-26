# MySQLデータベース確認方法

## 🌐 ウェブUIでの確認方法（推奨）

アプリケーションが起動している場合、ブラウザで以下のURLにアクセスしてください:

```
http://localhost:8081/database-viewer
```

このページで以下の機能が使用できます:

### 機能一覧

1. **📊 データベース情報確認**
   - MySQLへの接続確認
   - テーブルの存在確認
   - 保存されているレコード数の表示

2. **📋 全データ表示**
   - 保存されたすべてのローン申請データを表示
   - 各申請の詳細情報を確認
   - 見やすい形式で整形して表示

3. **🔄 更新**
   - 最新のデータを再取得
   - リアルタイムにデータベースの状態を確認

## 💻 コマンドラインでの確認方法

### 方法1: MySQLコマンドラインツールを使用

```bash
# MySQLに接続
mysql -u root -proot_password internship

# テーブル一覧を表示
SHOW TABLES;

# bankLoan_tableの構造を確認
DESCRIBE bankLoan_table;

# すべてのデータを表示
SELECT * FROM bankLoan_table;

# レコード数を表示
SELECT COUNT(*) as 総レコード数 FROM bankLoan_table;

# 特定の列だけを表示
SELECT name, loanType, loanAmount, interestRate FROM bankLoan_table;

# MySQLを終了
EXIT;
```

### 方法2: REST APIで確認

```bash
# データベース情報を取得
curl http://localhost:8081/database-info

# すべてのデータを取得
curl http://localhost:8081/database-data
```

## 📊 データベーススキーマ

### bankLoan_tableのカラム構成

| カラム名 | 型 | 説明 |
|---------|-----|------|
| bankName | VARCHAR | 銀行名 |
| branchName | VARCHAR | 支店名 |
| bankAccountType | VARCHAR | 口座種別（普通/当座など） |
| bankAccountNum | INT | 口座番号 |
| name | VARCHAR | 申請者名 |
| loanType | VARCHAR | ローンの種類（住宅/マイカーなど） |
| loanAmount | INT | 借入金額 |
| annualIncome | INT | 年収 |
| loanPeriod | INT | 借入期間（年） |
| interestRate | DECIMAL | 金利（%） |

## 🔧 トラブルシューティング

### MySQLが起動していない場合

```bash
# Homebrewで起動している場合
brew services start mysql

# または
mysql.server start
```

### データベースが見つからない場合

```bash
# データベースを作成
mysql -u root -proot_password -e "CREATE DATABASE internship CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### テーブルが見つからない場合

Spring Bootアプリケーションが初回起動時にテーブルを自動作成していない場合は、以下のSQLを実行してください:

```sql
CREATE TABLE bankLoan_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bankName VARCHAR(100),
    branchName VARCHAR(100),
    bankAccountType VARCHAR(50),
    bankAccountNum INT,
    name VARCHAR(100),
    loanType VARCHAR(100),
    loanAmount INT,
    annualIncome INT,
    loanPeriod INT,
    interestRate DECIMAL(5, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 📝 使用例

### ローン申請を送信後、データベースを確認

1. http://localhost:8081/bankLoan にアクセス
2. ローン申請フォームを入力
3. 「確認」ボタンをクリック
4. 「申請完了」をクリック
5. http://localhost:8081/database-viewer にアクセス
6. 「📋 全データ表示」ボタンをクリック
7. 保存されたデータを確認

---

**更新日時**: 2026年2月26日

