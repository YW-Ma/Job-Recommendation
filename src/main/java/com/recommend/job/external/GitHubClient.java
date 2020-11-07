package com.recommend.job.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.job.entity.Item;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GitHubClient {
    // 1. template
    private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
    private static final String DEFAULT_KEYWORD = "developer";

    // 2. implement search function
    public List<Item> search(double lat, double lon, String keyword) {
        // sanity check
        if (keyword == null) {
            keyword = DEFAULT_KEYWORD;
        }
        // encode
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // url generation
        String url = String.format(URL_TEMPLATE, keyword, lat, lon);

        // 1 - add a HttpClient to get external resource
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 2 - configure the response handler
        ResponseHandler<List<Item>> responseHandler = response -> {
            // sanity check 1
            if (response.getStatusLine().getStatusCode() != 200) {
                return Collections.emptyList();
            }
            // sanity check 2
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return Collections.emptyList();
            }
            // process entity into List
            ObjectMapper mapper = new ObjectMapper();
            Item[] temp = mapper.readValue(entity.getContent(), Item[].class);
            return Arrays.asList(temp); // ArrayList是动态数组，依然连续，insert的armotized cost 时间复杂度依然是O(n)
        };
        // 3 - make a request
        try {
            return httpClient.execute(new HttpGet(url), responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
