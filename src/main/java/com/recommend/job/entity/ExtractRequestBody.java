package com.recommend.job.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// We create this object. For POST body
// Jackson convert it into JSON.
public class ExtractRequestBody {
    public List<String> data;

    @JsonProperty("max_keywords") // write annotation here.
    public int maxKeywords;

    public ExtractRequestBody(List<String> data, int maxKeywords) {
        this.data = data;
        this.maxKeywords = maxKeywords;
    }
}
