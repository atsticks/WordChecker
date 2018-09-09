package com.github.atsticks.handler.wordcount;

import com.amazonaws.serverless.proxy.model.ApiGatewayRequestContext;

import java.util.Map;

public class WordCounterInput {

    private ApiGatewayRequestContext requestContext;
    private String body;
    private String url;

    public ApiGatewayRequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(ApiGatewayRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
