package com.charles.mianti.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库连接器
 * 用于直接连接数据库并执行SQL文件
 */
public class DatabaseConnector {

    public static void main(String[] args) {
        // 数据库连接信息
        String host = "209.38.31.91";
        int port = 3306;
        String username = "root";
        String password = "root";
        String databaseName = "mianti";

        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL驱动加载成功");

            // 连接到MySQL服务器（不指定数据库）
            String connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            System.out.println("连接到MySQL服务器: " + connectionUrl);

            try (Connection connection = DriverManager.getConnection(connectionUrl, username, password);
                 Statement statement = connection.createStatement()) {

                // 创建数据库
                String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + databaseName;
                statement.executeUpdate(createDatabaseSql);
                System.out.println("数据库创建成功: " + databaseName);

                // 切换到创建的数据库
                statement.executeUpdate("USE " + databaseName);
                System.out.println("切换到数据库: " + databaseName);

                // 执行SQL文件
                String sqlFilePath = "/Users/a1234/Desktop/mianti-next-backend/sql/create_table.sql";
                try {
                    executeSqlFile(statement, sqlFilePath);
                    System.out.println("SQL文件执行完成！");
                } catch (IOException e) {
                    System.err.println("SQL文件读取失败: " + e.getMessage());
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                System.err.println("数据库操作失败: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            System.err.println("数据库驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行SQL文件
     * @param statement SQL语句执行对象
     * @param filePath SQL文件路径
     * @throws SQLException SQL异常
     * @throws IOException IO异常
     */
    private static void executeSqlFile(Statement statement, String filePath) throws SQLException, IOException {
        List<String> sqlStatements = readSqlFile(filePath);
        for (String sql : sqlStatements) {
            if (!sql.trim().isEmpty() && !sql.trim().startsWith("--")) {
                // 使用execute()方法执行SQL语句，可以处理各种类型的SQL
                statement.execute(sql);
                System.out.println("执行SQL: " + sql);
            }
        }
    }

    /**
     * 读取SQL文件
     * @param filePath SQL文件路径
     * @return SQL语句列表
     * @throws IOException IO异常
     */
    private static List<String> readSqlFile(String filePath) throws IOException {
        List<String> sqlStatements = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sb.append(line);
                if (line.endsWith(";")) {
                    sqlStatements.add(sb.toString());
                    sb.setLength(0);
                }
            }
        }

        return sqlStatements;
    }
}
