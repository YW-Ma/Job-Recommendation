/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.recommend.job.db;

import com.recommend.job.entity.Item;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLConnection {
    private Connection conn; // NOTICE THE TYPE

    public void MySQLConnection(Connection conn) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            this.conn = DriverManager.getConnection(MySQLDBUtil.URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // 1. saveItem
    public void saveItem(Item item) throws Exception {
        // sanity check
        if (conn == null) {
            System.out.println("DB Connection failed");
            throw new Exception("DB Connection Failed");
        }

        // insert a record into items table
        String insertItemSql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(insertItemSql);
        statement.setString(1, item.getId());
        statement.setString(2, item.getTitle());
        statement.setString(3, item.getLocation());
        statement.setString(4, item.getCompanyLogo());
        statement.setString(5, item.getUrl());
        statement.executeUpdate();

        // insert a record into keywords table
        String insertKeywordSql = "INSERT IGNORE INTO items VALUES (?, ?)";
        for (String keyword : item.getKeywords()) {
            statement = conn.prepareStatement(insertKeywordSql);
            statement.setString(1, item.getId());
            statement.setString(2, keyword);
            statement.executeUpdate();
        }
    }

    // 2. setFavoriteItems
    public void setFavoriteItems(String userId, Item item) throws Exception {
        // sanity check
        if (conn == null) {
            System.out.println("DB Connection failed");
            throw new Exception("DB Connection Failed");
        }
        if (item.getId() == "" || item.getLocation() == "") {
            return;
        }

        // save the item
        saveItem(item);

        // insert a new record into history table
        String insertHistorySql = "INSERT IGNORE INTO history VALUES (?, ?)";
        PreparedStatement statement = conn.prepareStatement(insertHistorySql);
        statement.setString(1, userId);
        statement.setString(2, item.getId());
        statement.executeUpdate();
    }

    // 3. unsetFavoriteItems
    public void unsetFavoriteItems(String userId, String itemId) throws Exception {
        // sanity check
        if (conn == null) {
            System.out.println("DB Connection failed");
            throw new Exception("DB Connection Failed");
        }

        // insert a new record into history table
        String insertHistorySql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
        PreparedStatement statement = conn.prepareStatement(insertHistorySql);
        statement.setString(1, userId);
        statement.setString(2, itemId);
        statement.executeUpdate();
    }

    // 4.
}
