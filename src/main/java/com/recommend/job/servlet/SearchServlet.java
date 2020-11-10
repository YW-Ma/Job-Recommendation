package com.recommend.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.job.db.MySQLConnection;
import com.recommend.job.entity.Item;
import com.recommend.job.entity.ResultResponse;
import com.recommend.job.external.GitHubClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // validate session before any action.
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
            return;
        }
        String userId = request.getParameter("user_id"); // used to find favorite items
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));
        String description = request.getParameter("description");
        // get favorite items from DB
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
        connection.close();
        // get the external data
        GitHubClient client = new GitHubClient();
        List<Item> items = client.search(lat, lon, description);
        // use Setter for favorite to change these items' field.
        for (Item item : items) {
            item.setFavorite(favoritedItemIds.contains(item.getId()));
        }
        // map these object List into JSON to return
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), items);
    }
}
