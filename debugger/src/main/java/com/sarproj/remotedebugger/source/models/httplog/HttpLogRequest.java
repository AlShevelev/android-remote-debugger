package com.sarproj.remotedebugger.source.models.httplog;

import java.util.Map;

public class HttpLogRequest {
    public long id;
    public long queryId;
    public String method;
    public long requestStartTime;
    public String requestContentType;
    public long requestBodySize;
    public String baseUrl;
    public String port;
    public String ip;
    public String fullUrl;
    public String shortUrl;
    public String requestBody;
    public Map<String, String> requestHeaders;
    public Map<String, String> queryParams;
}
