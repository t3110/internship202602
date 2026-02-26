# bankLoan アプリケーション

ローン申込の入力・確認・完了までの一連のフローを提供する Spring Boot アプリです。確認画面では外部の Python 推論 API から「通過/要確認」「スコア」「根拠」を取得して表示します。

## 特徴

- 申込入力 → 確認 → 完了の 3 画面フロー
- ローン種別/期間に応じた金利自動計算
- Python 推論 API による簡易審査（スコア＋根拠）表示
- 申込内容を MySQL に保存

## 画面とURL

- 入力画面: `http://localhost:8081/bankLoan`
- 確認画面: 入力画面の送信で遷移
- 完了画面: 確認画面の申込ボタンで遷移

## 外部審査API（Python）

確認画面の JavaScript が `http://localhost:8000/screening` に直接 POST します。
デフォルトの base URL は `bankLoan/src/main/resources/templates/bankLoanConfirmation.html` の
`data-screening-api-base` 属性で指定しています。

### エンドポイント

- `POST /screening`

### リクエスト例

```json
{
  "loanAmount": 3000000,
  "annualIncome": 5000000,
  "loanPeriod": 20,
  "loanType": "住宅ローン"
}
```

### レスポンス例

```json
{
  "result": "通過",
  "score": 0.742,
  "threshold": 0.6,
  "modelVersion": "demo-v1",
  "reasons": [
    {"label": "借入比率", "direction": "negative", "detail": "年収に対して借入が高め"},
    {"label": "返済期間", "direction": "positive", "detail": "十分な期間が確保"}
  ]
}
```

## 起動手順

### 1. Python 推論 API を起動

`ml-screening-api` で FastAPI を起動します。

```powershell
cd C:\Users\ffg-training\dev\internship202602\ml-screening-api
uvicorn app:app --host 0.0.0.0 --port 8000
```

必要に応じて学習を先に実行できます。

```powershell
python train_model.py
```

詳細は `ml-screening-api/README.md` を参照してください。

### 2. Spring Boot を起動

IntelliJ から `bankLoan/src/main/java/com/example/internship/InternshipApplication.java` を起動するか、
コマンドラインで起動します。

```powershell
cd C:\Users\ffg-training\dev\internship202602\bankLoan
./mvnw spring-boot:run
```

### 3. ブラウザでアクセス

`http://localhost:8081/bankLoan` を開きます。

## 設定

設定は `bankLoan/src/main/resources/application.yml` で管理しています。

- サーバーポート: `server.port`
- DB 接続: `spring.datasource.*`
- 審査 API: `screening.api.base-url` / `screening.api.path`

## DB（MySQL）

申込内容は `bankLoan_table` に保存します。ローカル MySQL を使う場合の例です。

```sql
CREATE TABLE bankLoan_table (
  id INT AUTO_INCREMENT PRIMARY KEY,
  bankName VARCHAR(100),
  branchName VARCHAR(100),
  bankAccountType VARCHAR(50),
  bankAccountNum INT,
  name VARCHAR(100),
  loanType VARCHAR(50),
  loanAmount INT,
  annualIncome INT,
  loanPeriod INT,
  interestRate DECIMAL(6, 2)
);
```

※ DB が起動していない場合、確認画面の申込ボタンで保存に失敗します。

## 主要ソース

- コントローラ: `bankLoan/src/main/java/com/example/internship/controller/BankLoanController.java`
- 審査 API 呼び出し: `bankLoan/src/main/java/com/example/internship/service/ScreeningService.java`
- 審査表示（JS）: `bankLoan/src/main/resources/static/js/bankLoanConfirmation.js`
- 画面テンプレート: `bankLoan/src/main/resources/templates/*.html`

## トラブルシュート

- 審査が取得できない場合: Python API の起動、`data-screening-api-base` の URL、CORS を確認してください。
- スコアが表示されない場合: ブラウザの開発者ツールで `/screening` のレスポンスを確認してください。

