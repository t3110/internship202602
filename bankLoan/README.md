# Bank Loan Application (銀行ローン申し込みシステム)

本プロジェクトは、銀行ローンの申し込みプロセスをWebインターフェース経由で処理し、バックエンドの機械学習モデルを用いてリアルタイムに審査を行う統合システムです。

堅牢なSpring Boot（Java）によるWebアプリケーションと、推論速度に優れたFastAPI（Python）＋LightGBMによる審査APIを連携させた**マイクロサービスアーキテクチャ**を採用しています。

## 🌟 主な機能

- **ローン申し込みフロー**: 顧客情報、口座情報、ローン詳細の入力と確認
- **リアルタイム審査 (AI推論)**: 入力データに基づき、LightGBMモデルが「通過/要確認」を瞬時に自動判定
- **自動金利計算**: ローンタイプと返済期間に基づく金利の算出
- **データ管理**: 申し込み情報と審査結果をMySQLデータベースへ安全に保存
- **管理者用ビューア**: 保存された申し込みデータをWeb上で一覧表示

## 🛠 技術スタック

### Webアプリケーション (Java)
- **Framework**: Spring Boot 3.4.2 / Java 17
- **Database**: MySQL 8.0+ / Spring Data JDBC
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven 3.6+

### スクリーニングAPI (Python)
- **Framework**: FastAPI 0.115.6 / Uvicorn 0.34.0
- **Machine Learning**: LightGBM 4.5.0 / scikit-learn 1.5.2 / numpy 2.1.3
- **Data Validation**: Pydantic 2.10.6

---

## 🚀 環境構築 (セットアップ手順)

システム全体を稼働させるため、以下の順番でセットアップを行います。

### 1. リポジトリのクローン
```bash
git clone <repository-url>
cd internship202602

```

### 2. データベースの初期化

ルートディレクトリにあるスクリプトを実行し、専用のデータベース(`internship`)とテーブル(`bankLoan_table`)を作成します。

```bash
bash setup-mysql.sh

```

### 3. スクリーニングAPIの起動 (Python)

審査を行う推論サーバーを別ターミナルで起動します。

```bash
cd ml-screening-api

# 仮想環境の作成と有効化 (Mac/Linux)
python3 -m venv .venv
source .venv/bin/activate
# Windowsの場合は: .venv\Scripts\activate

# パッケージのインストールと起動
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 8000 --reload

```

> **Note**: `http://localhost:8000/docs` にアクセスすると、Swagger UIでAPIドキュメントが確認できます。初回起動時に学習済みモデル(`model.joblib`)が自動生成されます。

### 4. Webアプリケーションの起動 (Java)

新しいターミナルを開き、Spring Bootアプリを起動します。

```bash
cd bankLoan

# 依存関係のインストールと起動
./mvnw clean install
./mvnw spring-boot:run

```

> **Note**: アプリケーションはデフォルトで `http://localhost:8081` で起動します。

---

## 📱 使用方法

1. **申し込み画面**: `http://localhost:8081/bankLoan` にアクセスし、必要事項を入力します。
2. **確認と審査**: 確認画面から「申し込み」を実行すると、バックエンドでPython APIへデータが送信され、AIによる審査が実行されます。
3. **データ確認**: `http://localhost:8081/database-viewer` から、保存されたレコードと審査結果（通過・要確認、スコアなど）を確認できます。

---

## 🌐 APIリファレンス

### スクリーニングAPI (Python: Port 8000)

| メソッド | エンドポイント | 説明 |
| --- | --- | --- |
| `POST` | `/screening` | LightGBMモデルによる審査を実行 |

**リクエスト例:**

```json
{
  "loanAmount": 3000000,
  "annualIncome": 5000000,
  "loanPeriod": 20,
  "loanType": "住宅ローン"
}

```

### Webアプリ内部API (Java: Port 8081)

| メソッド | エンドポイント | 説明 |
| --- | --- | --- |
| `GET` | `/calculateInterestRate` | 金利の自動計算 |
| `GET` | `/validateBranch` | 支店名の有効性検証 |
| `POST` | `/saveBankLoan` | 申し込みデータと審査結果のDB保存 |

---

## 📁 プロジェクト構成 (抜粋)

```text
internship202602/
├── ml-screening-api/          # 【審査AI】Python推論サーバー
│   ├── app.py                 # FastAPIエンドポイント
│   ├── train_model.py         # LightGBMモデル学習スクリプト
│   └── requirements.txt
│
├── bankLoan/                  # 【Webアプリ】Spring Bootサーバー
│   ├── src/main/java/com/example/internship/
│   │   ├── controller/        # 画面・APIルーティング
│   │   ├── service/           # ビジネスロジック・PythonAPI連携
│   │   └── repository/        # データベースアクセス
│   ├── src/main/resources/
│   │   ├── application.yml    # ポート・DB・API連携設定
│   │   └── templates/         # Thymeleaf HTMLテンプレート
│   └── pom.xml
│
├── setup-mysql.sh             # DB初期化スクリプト
└── check_database.sh          # データベース接続確認スクリプト

```

---

## 🔧 トラブルシューティング

* **Q. Javaアプリが起動しない (Port 8081が既に使用中)**
* `application.yml` の `server.port` を `8082` など別のポートに変更してください。


* **Q. 審査APIに繋がらない (Unable to connect to screening API)**
* PythonサーバーがPort 8000で起動しているか確認してください。
* `application.yml` の `screening.api.base-url` の指定が `http://localhost:8000` になっているか確認してください。


* **Q. DB接続エラー (Access denied for user 'root'@'localhost')**
* MySQLが起動しているか確認し、`bash check_database.sh` を実行して疎通確認を行ってください。



---

*Developed by Takase Saito during Internship Program - February 2026*
