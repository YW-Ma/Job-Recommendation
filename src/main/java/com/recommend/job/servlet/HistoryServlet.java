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
import com.recommend.job.entity.HistoryRequestBody;
import com.recommend.job.entity.Item;
import com.recommend.job.entity.ResultResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebServlet(name = "HistoryServlet", urlPatterns = {"/history"})
public class HistoryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. acquire the user_id & item that the user want to set favorite
        ObjectMapper mapper = new ObjectMapper();
        HistoryRequestBody body = mapper.readValue(request.getReader(), HistoryRequestBody.class);

        // 2. set favorite
        MySQLConnection connection = new MySQLConnection();
        try {
            connection.setFavoriteItems(body.userId, body.favorite);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("DB Connection related exception");
            response.setStatus(502);
            connection.close();
            return;
        }
        connection.close();

        // 3. success
        ResultResponse resultResponse = new ResultResponse("SUCCESS");
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), resultResponse);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // delete the super.doDelete here.
        // 1. get body
        ObjectMapper mapper = new ObjectMapper();
        HistoryRequestBody body = mapper.readValue(req.getReader(), HistoryRequestBody.class);

        // 2. unset
        MySQLConnection connection = new MySQLConnection();
        try {
            connection.unsetFavoriteItems(body.userId, body.favorite.getId());
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("DB Connection related exception");
            resp.setStatus(502);
            connection.close();
            return;
        }

        // 3. success
        ResultResponse resultResponse = new ResultResponse("SUCCESS");
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), resultResponse);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get user_id
        String userId = request.getParameter("user_id");
        // get data using user_id
        MySQLConnection connection = new MySQLConnection();
        Set<Item> items = connection.getFavoriteItems(userId);
        connection.close();
        // return
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), items);
    }
}
