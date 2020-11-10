/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.recommend.job.recommendation;

import com.recommend.job.db.MySQLConnection;
import com.recommend.job.entity.Item;
import com.recommend.job.external.GitHubClient;

import java.util.*;

public class Recommendation {
    // 实现的function是recommendItems。
    public List<Item> recommendItems(String userId, double lat, double lon) {
// 装return value 的容器。
        List<Item> recommendedItems = new ArrayList<>();

        // Step 1, get all favorited itemids
// 1 找到favorited item的id。
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);

        // Step 2, get all keywords, sort by count
        // {"software engineer": 6, "backend": 4, "san francisco": 3, "remote": 1}
// 2 - 1 临时去找对应的keywords，每一个item得到对应的keywords、放到allKeywords里，这个hashmap key是keyword，value是在favorite的item的key里出现的次数。
// keyword对应的value是计数，计数实现方法是：allKeywords.getOrDefault(keyword, 0) + 1，第一次是0，后面值每次会加一。
        Map<String, Integer> allKeywords = new HashMap<>();
        for (String itemId : favoritedItemIds) {
            Set<String> keywords = connection.getKeywords(itemId);
            for (String keyword : keywords) {
                allKeywords.put(keyword, allKeywords.getOrDefault(keyword, 0) + 1);
            }
        }
        connection.close();
// 2 - 2 排序，实现compartor的compare，可以变成anonymous class。
        List<Map.Entry<String, Integer>> keywordList = new ArrayList<>(allKeywords.entrySet());
        keywordList.sort((Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) ->
                Integer.compare(e2.getValue(), e1.getValue()));
// 2 - 3 只要最频繁的前三个keywords
        // Cut down search list only top 3
        if (keywordList.size() > 3) {
            keywordList = keywordList.subList(0, 3);
        }

        // Step 3, search based on keywords, filter out favorite items
// 3 search，loop through keywords，用github client去找对应的keyword的结果。然后把结果装进去再return。
// 放item的时候，要先检查一下是不是以前已经favorite了。不装已经favorite的。
// 放item的时候，还要检查是否已经被其他keyword搜出来并添加了。因为keyword搜出来可能有overlap。只用Id检查就好了。
// 我们用visitedItemIds来hold这些Id（只放id），用recommendedItems来hold整个item。
        Set<String> visitedItemIds = new HashSet<>();
        GitHubClient client = new GitHubClient();

        for (Map.Entry<String, Integer> keyword : keywordList) {
            List<Item> items = client.search(lat, lon, keyword.getKey());

            for (Item item : items) {
                if (!favoritedItemIds.contains(item.getId()) && !visitedItemIds.contains(item.getId())) {
                    recommendedItems.add(item);
                    visitedItemIds.add(item.getId());
                }
            }
        }
        return recommendedItems;
    }
}

