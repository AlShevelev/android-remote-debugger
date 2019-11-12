package com.zerobranch.remotedebugger.source.models.httplog;

import java.util.Map;

public class HttpLogResponse {
    public long id;
    public long time;
    public String queryId;
    public String url;
    public String port;
    public String ip;
    public String method;
    public int code = -1;
    public String message;
    public String duration;
    public String bodySize;
    public String body;
    public String errorMessage;
    public Map<String, String> headers;
}
