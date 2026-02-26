# インターンシップ課題
インターンの課題として、下記の簡素的なWebアプリを作成しています
- ローン申し込み
- 銀行振込
- 投信購入

## 前提条件
- Java 17以上がインストールされていること
- MySQLデータベースがローカルで起動していること（localhost:3306）
  - データベース名: `internship`
  - ユーザー名: `root`
  - パスワード: `root_password`
  
### MySQLのセットアップ

**オプション1: 自動セットアップスクリプト（推奨）**
```bash
./setup-mysql.sh
```

**オプション2: 手動セットアップ**
詳細な手順は [MYSQL_SETUP.md](./MYSQL_SETUP.md) を参照してください。

**オプション3: クイックスタート**
```bash
# Homebrewでインストール
brew install mysql

# MySQLサーバーを起動
brew services start mysql

# データベースを作成
mysql -u root -e "CREATE DATABASE internship;"
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root_password';"
```

## 動作方法
ローンの場合
1. `internship\bankLoan\src\main\java\com\example\internship\InternshipApplication.java`に行く
2. ▶ を押し、実行を押す（二回目からは右上の▶や`shift`+`F10`で最近起動したものを再起動できる）
3. 実行後、実行ログで `Started InternshipApplication`を確認する
4. `http://localhost:8081/bankLoan` にブラウザでアクセス

銀行振込の場合
1. `internship\bankTransfer\src\main\java\com\example\internship\InternshipApplication.java`に行く
2. ▶ を押し、実行を押す（二回目からは右上の▶や`shift`+`F10`で最近起動したものを再起動できる）
3. 実行後、実行ログで `Started InternshipApplication`を確認する
4. `http://localhost:8082/bankTransfer` にブラウザでアクセス

投資信託の場合
1. `internship\investmentTrust\src\main\java\com\example\internship\InternshipApplication.java`に行く
2. ▶ を押し、実行を押す（二回目からは右上の▶や`shift`+`F10`で最近起動したものを再起動できる）
3. 実行後、実行ログで `Started InternshipApplication`を確認する
4. `http://localhost:8083/investmentTrust` にブラウザでアクセス

## トラブルシューティング

### エラー: リリース・バージョン21はサポートされていません
**原因**: プロジェクトがJava 21を要求していますが、システムにインストールされているJavaのバージョンが古い場合に発生します。

**解決方法**: 各プロジェクトの`pom.xml`ファイルで`<java.version>`を17に変更しました。
- `/bankLoan/pom.xml`
- `/bankTransfer/pom.xml`
- `/investmentTrust/pom.xml`

### エラー: Failed to obtain JDBC Connection / Communications link failure
**原因**: MySQLデータベースに接続できない場合に発生します。

**解決方法**: 
1. MySQLサーバーが起動しているか確認してください
2. データベース`internship`が作成されているか確認してください
3. `application.yml`の接続情報（ユーザー名、パスワード、URL）が正しいか確認してください

MySQLの起動方法:
```bash
# Homebrewでインストールした場合
brew services start mysql

# または
mysql.server start
```

データベースの作成:
```sql
CREATE DATABASE internship;
```
