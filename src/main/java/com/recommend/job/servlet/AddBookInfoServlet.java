package com.recommend.job.servlet;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AddBookInfoServlet", urlPatterns = {"/add_book"})
public class AddBookInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String content = IOUtils.toString(request.getReader());
        JSONObject jsonRequest = new JSONObject(content);
        String name = jsonRequest.getString("name");
        String category = jsonRequest.getString("category");
        float price = jsonRequest.getFloat("price");
        String author = jsonRequest.getString("author");
        System.out.println(name);
        System.out.println(category);
        System.out.println(price);
        System.out.println(author);

        JSONObject json = new JSONObject();
        json.put("status","added successfully");
        response.setContentType("application/json");
        response.getWriter().print(json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
