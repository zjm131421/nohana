package com.nohana.http;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class UnconditionalRequestRetryHandler implements HttpRequestRetryHandler {

    private int retryTimes;

    public UnconditionalRequestRetryHandler(int retryTimes){this.retryTimes = retryTimes;}


    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        return executionCount < retryTimes;
    }
}
