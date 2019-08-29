package com.sarproj.remotedebugger.source.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogModel;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogRequest;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogResponse;
import com.sarproj.remotedebugger.source.models.LogModel;
import com.sarproj.remotedebugger.source.models.httplog.QueryType;
import com.sarproj.remotedebugger.source.repository.HttpLogRepository;
import com.sarproj.remotedebugger.source.repository.LogRepository;

import java.util.List;

public final class ContinuousDataBaseManager {
    private static final String DATABASE_NAME = "remote_debugger_data.db";
    private final static Object LOCK = new Object();
    private final SQLiteDatabase database;
    private static ContinuousDataBaseManager instance;
    private final HttpLogRepository httpLogRepository;
    private final LogRepository logRepository;

    private ContinuousDataBaseManager(Context context) {
        SQLiteDatabase.deleteDatabase(context.getDatabasePath(DATABASE_NAME));
        database = context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
        database.setVersion(Integer.MAX_VALUE);

        httpLogRepository = new HttpLogRepository(database);
        httpLogRepository.createHttpLogsTable(database);

        logRepository = new LogRepository(database);
        logRepository.createLogsTable(database);
    }

    public static ContinuousDataBaseManager getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("RemoteDebugger is not initialized. " +
                        "Please call " + RemoteDebugger.class.getName() + ".init()");
            }
            return instance;
        }
    }

    public static void init(Context context) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ContinuousDataBaseManager(context);
            }
        }
    }

    public static void destroy() {
        synchronized (LOCK) {
            if (instance != null) {
                if (instance.database != null && instance.database.isOpen()) {
                    instance.database.close();
                }

                instance = null;
            }
        }
    }

    public long addHttpLogRequest(HttpLogRequest logRequest) {
        synchronized (LOCK) {
            return httpLogRepository.addWithAutoQueryId(mapToLogModel(logRequest));
        }
    }

    public void addHttpLogResponse(HttpLogResponse logResponse) {
        synchronized (LOCK) {
            httpLogRepository.add(mapToLogModel(logResponse));
        }
    }

    public void clearAllHttpLog() {
        synchronized (LOCK) {
            httpLogRepository.clearAll();
        }
    }

    public void addLog(LogModel model) {
        synchronized (LOCK) {
            logRepository.addLog(model);
        }
    }

    public List<LogModel> getLogByFilter(int offset, int limit, String level, String tag, String search) {
        synchronized (LOCK) {
            return logRepository.getLogsByFilter(offset, limit, level, tag, search);
        }
    }

    public void clearAllLog() {
        synchronized (LOCK) {
            logRepository.clearAllLogs();
        }
    }

    public List<HttpLogModel> getHttpLogs(int offset,
                                          int limit,
                                          String queryId,
                                          String statusCode,
                                          boolean isOnlyExceptions,
                                          String search) {
        return httpLogRepository.getHttpLogs(offset, limit, queryId, statusCode, isOnlyExceptions, search);
    }

    private HttpLogModel mapToLogModel(HttpLogResponse httpLogResponse) {
        HttpLogModel httpLogModel = new HttpLogModel();
        httpLogModel.queryId = httpLogResponse.queryId;
        httpLogModel.method = httpLogResponse.method;
        httpLogModel.responseTime= httpLogResponse.responseTime;
        httpLogModel.code = httpLogResponse.code;
        httpLogModel.message = httpLogResponse.message;
        httpLogModel.requestDuration = httpLogResponse.requestDuration;
        httpLogModel.responseBodySize = httpLogResponse.responseBodySize;
        httpLogModel.baseUrl = httpLogResponse.baseUrl;
        httpLogModel.port = httpLogResponse.port;
        httpLogModel.ip = httpLogResponse.ip;
        httpLogModel.fullUrl = httpLogResponse.fullUrl;
        httpLogModel.shortUrl = httpLogResponse.shortUrl;
        httpLogModel.errorMessage = httpLogResponse.errorMessage;
        httpLogModel.responseBody = httpLogResponse.responseBody;
        httpLogModel.responseHeaders = httpLogResponse.responseHeaders;
        httpLogModel.queryType = QueryType.RESPONSE;
        return httpLogModel;
    }

    private HttpLogModel mapToLogModel(HttpLogRequest httpLogRequest) {
        HttpLogModel httpLogModel = new HttpLogModel();
        httpLogModel.queryId = httpLogRequest.queryId;
        httpLogModel.method = httpLogRequest.method;
        httpLogModel.requestTime = httpLogRequest.requestTime;
        httpLogModel.requestContentType = httpLogRequest.requestContentType;
        httpLogModel.requestBodySize = httpLogRequest.requestBodySize;
        httpLogModel.baseUrl = httpLogRequest.baseUrl;
        httpLogModel.port = httpLogRequest.port;
        httpLogModel.ip = httpLogRequest.ip;
        httpLogModel.fullUrl = httpLogRequest.fullUrl;
        httpLogModel.shortUrl = httpLogRequest.shortUrl;
        httpLogModel.requestBody = httpLogRequest.requestBody;
        httpLogModel.requestHeaders = httpLogRequest.requestHeaders;
        httpLogModel.queryParams = httpLogRequest.queryParams;
        httpLogModel.queryType = QueryType.REQUEST;
        return httpLogModel;
    }
}
