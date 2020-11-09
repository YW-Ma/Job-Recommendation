/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.recommend.job.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLTableCreation {
    public static void main(String[] args) {
        // put codes in try, to prevent many small blocks of try-catch
        try {
            // Step 1: Connect to MySQL
            // load Driver to DriverManager
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            // Get connection using DriverManager.
            Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);

            // Step 2: Drop tables if any
            Statement statement = conn.createStatement();
            // drop in reference order
            String sql = "DROP TABLE IF EXISTS history";
            statement.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS keywords";
            statement.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS users";
            statement.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS items";
            statement.executeUpdate(sql);

            // Step 3: Create new tables
            sql = "CREATE TABLE items (" +
                    "item_id VARCHAR(255) NOT NULL," +
                    "name VARCHAR(255)," +
                    "address VARCHAR(255)," +
                    "image_url VARCHAR(255)," +
                    "url VARCHAR(255)," +
                    "PRIMARY KEY (item_id)" +
                    ")";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE users (" +
                    "user_id VARCHAR(255) NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "first_name VARCHAR(255)," +
                    "last_name VARCHAR(255)," +
                    "PRIMARY KEY (user_id)" +
                    ")";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE keywords (" +
                    "item_id VARCHAR(255) NOT NULL," +
                    "keyword VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (item_id, keyword)," +
                    "FOREIGN KEY (item_id) REFERENCES items(item_id)" +
                    ")";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE history (" +
                    "user_id VARCHAR(255) NOT NULL," +
                    "item_id VARCHAR(255) NOT NULL," +
                    "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (user_id, item_id)," +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                    "FOREIGN KEY (item_id) REFERENCES items(item_id)" +
                    ")";
            statement.executeUpdate(sql);

            // Step 4: Insert a fake user in order to test.
            sql = "INSERT INTO users VALUES('2020', '7b7a53e239400a13bd6be6c91c4f6c4e', 'Morgan', 'Ma')";
            statement.executeUpdate(sql);

            conn.close();
            System.out.println("Database tables created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
