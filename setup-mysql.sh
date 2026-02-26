#!/bin/bash

# MySQLセットアップスクリプト
# このスクリプトは、MySQLのインストール、起動、データベース作成を自動化します

set -e  # エラーが発生したら停止

echo "=================================="
echo "MySQL セットアップスクリプト"
echo "=================================="
echo ""

# 色の定義
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Homebrewがインストールされているか確認
echo "🔍 Homebrewの確認中..."
if ! command -v brew &> /dev/null; then
    echo -e "${RED}❌ Homebrewがインストールされていません${NC}"
    echo "Homebrewをインストールするには、以下のコマンドを実行してください:"
    echo '/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"'
    exit 1
fi
echo -e "${GREEN}✅ Homebrewが見つかりました${NC}"
echo ""

# MySQLがインストールされているか確認
echo "🔍 MySQLの確認中..."
if ! command -v mysql &> /dev/null; then
    echo -e "${YELLOW}⚠️  MySQLがインストールされていません${NC}"
    echo ""
    read -p "MySQLをインストールしますか？ (y/n): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "📦 MySQLをインストール中..."
        brew install mysql
        echo -e "${GREEN}✅ MySQLのインストールが完了しました${NC}"
    else
        echo "インストールをキャンセルしました"
        exit 0
    fi
else
    echo -e "${GREEN}✅ MySQLが見つかりました${NC}"
    mysql --version
fi
echo ""

# MySQLサーバーが起動しているか確認
echo "🔍 MySQLサーバーの状態を確認中..."
if pgrep -x mysqld > /dev/null; then
    echo -e "${GREEN}✅ MySQLサーバーは既に起動しています${NC}"
else
    echo -e "${YELLOW}⚠️  MySQLサーバーが起動していません${NC}"
    echo ""
    read -p "MySQLサーバーを起動しますか？ (y/n): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "🚀 MySQLサーバーを起動中..."
        brew services start mysql
        echo "⏳ 起動を待っています（5秒）..."
        sleep 5

        if pgrep -x mysqld > /dev/null; then
            echo -e "${GREEN}✅ MySQLサーバーが起動しました${NC}"
        else
            echo -e "${RED}❌ MySQLサーバーの起動に失敗しました${NC}"
            echo "手動で起動を試してください: brew services start mysql"
            exit 1
        fi
    else
        echo "起動をスキップしました"
        echo "手動で起動するには: brew services start mysql"
        exit 0
    fi
fi
echo ""

# MySQLに接続してデータベースを作成
echo "🔍 データベースのセットアップ中..."
echo ""
echo "MySQLのrootパスワードを入力してください"
echo "（初回インストールの場合、パスワードなしでEnterを押してください）"

# 一時的なSQLファイルを作成
TEMP_SQL=$(mktemp)
cat > "$TEMP_SQL" << 'EOF'
-- パスワードを設定
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root_password';
FLUSH PRIVILEGES;

-- データベースを作成
CREATE DATABASE IF NOT EXISTS internship CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 確認
SHOW DATABASES;
SELECT '✅ データベースのセットアップが完了しました！' AS Status;
EOF

# SQLを実行
if mysql -u root -p < "$TEMP_SQL" 2>/dev/null; then
    echo -e "${GREEN}✅ データベースのセットアップが完了しました${NC}"
elif mysql -u root < "$TEMP_SQL" 2>/dev/null; then
    echo -e "${GREEN}✅ データベースのセットアップが完了しました${NC}"
else
    echo -e "${RED}❌ データベースのセットアップに失敗しました${NC}"
    echo "手動でセットアップしてください"
    rm "$TEMP_SQL"
    exit 1
fi

rm "$TEMP_SQL"
echo ""

# 接続テスト
echo "🔍 接続テスト中..."
if mysql -u root -proot_password -e "USE internship;" 2>/dev/null; then
    echo -e "${GREEN}✅ データベースに正常に接続できました${NC}"
else
    echo -e "${YELLOW}⚠️  接続テストに失敗しました${NC}"
    echo "パスワードが正しいか確認してください"
fi
echo ""

# 完了メッセージ
echo "=================================="
echo -e "${GREEN}🎉 セットアップが完了しました！${NC}"
echo "=================================="
echo ""
echo "接続情報:"
echo "  URL: jdbc:mysql://localhost:3306/internship"
echo "  ユーザー名: root"
echo "  パスワード: root_password"
echo "  データベース: internship"
echo ""
echo "次のステップ:"
echo "1. Spring Bootアプリケーションを起動してください"
echo "2. ブラウザで http://localhost:8081/bankLoan にアクセスしてください"
echo ""
echo "MySQLサーバーの管理:"
echo "  起動: brew services start mysql"
echo "  停止: brew services stop mysql"
echo "  再起動: brew services restart mysql"
echo "  状態確認: brew services list | grep mysql"
echo ""

