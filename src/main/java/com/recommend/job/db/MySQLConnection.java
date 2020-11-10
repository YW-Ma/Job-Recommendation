/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.recommend.job.db;

import com.recommend.job.entity.Item;

import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MySQLConnection {
    private Connection conn; // NOTICE THE TYPE

    public MySQLConnection() {
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
        String insertKeywordSql = "INSERT IGNORE INTO keywords VALUES (?, ?)";
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

        // insert a new record into history table - timestamp use default.
        String insertHistorySql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
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

    // 4. get IDs and keywords, so as to fill the items
    public Set<String> getFavoriteItemIds(String userId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return new HashSet<>();
        }

        Set<String> favoriteItems = new HashSet<>();

        try {
            String sql = "SELECT item_id FROM history WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                favoriteItems.add(itemId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favoriteItems;
    }

    public Set<String> getKeywords(String itemId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return Collections.emptySet();
        }
        Set<String> keywords = new HashSet<>();
        String sql = "SELECT keyword from keywords WHERE item_id = ? ";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, itemId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String keyword = rs.getString("keyword");
                keywords.add(keyword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return keywords;
    }
    // 5. get favorite items
    public Set<Item> getFavoriteItems(String userId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return new HashSet<>();
        }
        Set<Item> favoriteItems = new HashSet<>();
        Set<String> favoriteItemIds = getFavoriteItemIds(userId); // get id (function call)

        String sql = "SELECT * FROM items WHERE item_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            for (String itemId : favoriteItemIds) {
                statement.setString(1, itemId);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    favoriteItems.add(new Item(rs.getString("item_id")
                            ,rs.getString("name")
                            ,rs.getString("address")
                            ,rs.getString("image_url")
                            ,rs.getString("url")
                            ,null
                            , getKeywords(itemId) // get keywords (function call)
                            ,true));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoriteItems;
    }


}
