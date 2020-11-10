/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.recommend.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.job.db.MySQLConnection;
import com.recommend.job.entity.LoginRequestBody;
import com.recommend.job.entity.LoginResponseBody;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        LoginRequestBody body = mapper.readValue(request.getReader(), LoginRequestBody.class);
        MySQLConnection connection = new MySQLConnection();
        LoginResponseBody loginResponseBody;
        // if success then use getSession to automatically
        // 1.create a session
        // 2.store session_id into cookies
        // 3.store session in memory
        if (connection.verifyLogin(body.userId, body.password)) {
            HttpSession session = request.getSession();
            session.setAttribute("user_id", body.userId);
            loginResponseBody = new LoginResponseBody("OK", body.userId, connection.getFullname(body.userId));
        } else {
            loginResponseBody = new LoginResponseBody("Login failed, user id and passcode do not exist.", null, null);
            response.setStatus(401); // 401 Unauthorized, used when pwd is not match
        }
        connection.close();
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), loginResponseBody);
    }
}

