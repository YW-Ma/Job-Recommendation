package com.recommend.job.servlet;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "GetBookInfoServlet", urlPatterns = {"/get_book_info"})
public class GetBookInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the parameters in url
        String keyword = request.getParameter("keyword");
        String category = request.getParameter("category");
        System.out.println("keyword is: " + keyword); // we can see it in our server's console
        System.out.println("category is: " + category);

        // return a json object
        JSONObject json = new JSONObject();
        json.put("name","Three Body");
        json.put("category", "sci-fi");
        json.put("price", "12");
        json.put("author", "Cixin Liu");
        response.setContentType("application/json");
        response.getWriter().print(json);
    }
}
