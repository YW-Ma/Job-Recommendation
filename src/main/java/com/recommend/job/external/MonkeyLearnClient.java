/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.recommend.job.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.job.entity.ExtractRequestBody;
import com.recommend.job.entity.ExtractResponseItem;
import com.recommend.job.entity.Extraction;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MonkeyLearnClient {
    // 1. constant strings
    private static final String EXTRACT_URL = "https://api.monkeylearn.com/v3/extractors/ex_YCya9nrn/extract/";
    private static final String AUTH_TOKEN = "cbaf46159d7f12ee68f29e68654726d7ab455470";
    private static final int MAX_KEYWORDS = 3;

    // 2. implement the extract method
    public List<Set<String>> extract(List<String> articles) {
        // input: a list of articles
        // output: a list of keywords of these articles.
        //         <=3 keywords in a set.

        // 1 - mapper & http client
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient httpClient = HttpClients.createDefault();


        // 2 - POST Request - preparation
        // 2.1 request object
        HttpPost postRequest = new HttpPost(EXTRACT_URL);
        // 2.2 Header
        postRequest.setHeader("Content-type", "application/json");
        postRequest.setHeader("Authorization", "token " + AUTH_TOKEN);
        // 2.3 body - object.        body will be added when created entity
        ExtractRequestBody body = new ExtractRequestBody(articles, MAX_KEYWORDS);
        String jsonBody;
        // body - object --> json string
        try {
            jsonBody = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        // body - set entity using json string
        try {
            postRequest.setEntity(new StringEntity(jsonBody));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }


        // 3 responseHandler
        ResponseHandler<List<Set<String>>> responseHandler = response -> {
            // sanity check
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Invalid status code from monkeyLearn");
                return Collections.emptyList();
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                System.out.println("Empty return entity");
                return Collections.emptyList();
            }
            // convert the returned keywords JSON into a list.
            ExtractResponseItem[] results = mapper.readValue(entity.getContent(), ExtractResponseItem[].class);
            List<Set<String>> keywordList = new ArrayList<>();
            for (ExtractResponseItem result : results) {
                // for each article's result
                Set<String> keywords = new HashSet<>();
                for (Extraction extraction : result.extractions) {
                    // for each keywords
                    keywords.add(extraction.parsedValue);
                }
                keywordList.add(keywords);
            }
            return keywordList;
        };

        // 4 execute
        try {
            return httpClient.execute(postRequest, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
