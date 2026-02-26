package com.example.internship;

import java.sql.*;

public class DatabaseChecker {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/internship";
        String username = "root";
        String password = "root_password";

        try {
            // MySQLドライバーをリフレクションで動的にロード
            Class.forName("com.mysql.cj.jdbc.Driver");

            // データベースに接続
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ MySQLに接続しました！");
            System.out.println("========================================");

            // テーブルの一覧を表示
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            System.out.println("\n📋 データベース内のテーブル一覧:");
            boolean hasTable = false;
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("  - " + tableName);
                hasTable = true;
            }

            if (!hasTable) {
                System.out.println("  (テーブルが見つかりません)");
            }

            // bankLoan_table テーブルが存在する場合
            tables = metaData.getTables(null, null, "bankLoan_table", new String[]{"TABLE"});
            if (tables.next()) {
                System.out.println("\n📊 bankLoan_table テーブルのデータ:");
                System.out.println("========================================");

                String query = "SELECT * FROM bankLoan_table";
                Statement stmt = conn.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);
                ResultSetMetaData rsMetaData = resultSet.getMetaData();

                // カラム名を表示
                int columnCount = rsMetaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rsMetaData.getColumnName(i) + " | ");
                }
                System.out.println();
                System.out.println("========================================");

                // データを表示
                int rowCount = 0;
                while (resultSet.next()) {
                    rowCount++;
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(resultSet.getString(i) + " | ");
                    }
                    System.out.println();
                }

                System.out.println("========================================");
                System.out.println("📈 合計レコード数: " + rowCount);

                stmt.close();
            } else {
                System.out.println("\n⚠️  bankLoan_table テーブルはまだ作成されていません");
            }

            conn.close();
            System.out.println("\n✅ 接続を閉じました");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQLドライバーが見つかりません:");
            System.err.println("   pom.xmlでmysql-connector-jが依存関係に追加されているか確認してください");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ SQLエラーが発生しました:");
            e.printStackTrace();
        }
    }
}

