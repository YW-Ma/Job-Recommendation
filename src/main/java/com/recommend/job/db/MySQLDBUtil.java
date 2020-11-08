/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.recommend.job.db;

public class MySQLDBUtil {
    // DB_NAME & URL are public
    private static final String INSTANCE = "job-project-instance.c5cogveqk32k.us-east-2.rds.amazonaws.com"; // instance address - endpoint - API
    private static final String PORT_NUM = "3306";
    public static final String DB_NAME = "jobproject";
    private static final String USERNAME = CredentialInfo.USERNAME; // If you want to use this project, you should create your personal database.
    private static final String PASSWORD = CredentialInfo.PASSWORD;
    public static final String URL = "jdbc:mysql://"
            + INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
            + "?user=" + USERNAME + "&password=" + PASSWORD
            + "&autoReconnect=true&serverTimezone=UTC";
}
