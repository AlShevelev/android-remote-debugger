package com.sarproj.remotedebugger;

import android.content.Context;
import android.util.Log;

import com.sarproj.remotedebugger.utils.HttpUtils;

import fi.iki.elonen.NanoHTTPD;

final class ServerRunner {
    private static final String TAG = "RemoteDebugger";
    private static final int DEFAULT_PORT = 8080;
    private static volatile ServerRunner instance;
    private AndroidWebServer androidWebServer;
    private boolean isEnableLogging;

    private ServerRunner() { }

    static ServerRunner getInstance() {
        ServerRunner localInstance = instance;
        if (localInstance == null) {
            synchronized (ServerRunner.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ServerRunner();
                }
            }
        }
        return localInstance;
    }

    void init(Context context, boolean isEnableLogging) {
        if (isAlive()) {
            print(context.getString(R.string.debugger_already_running));
            return;
        }

        this.isEnableLogging = isEnableLogging;

        String ip = HttpUtils.getIpAccess(context);

        try {
            androidWebServer = new AndroidWebServer(context, ip, DEFAULT_PORT);
            androidWebServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);

            print(context.getString(R.string.debugger_started, ip));
        } catch (Exception ex) {
            printErr(context.getString(R.string.error_start_server), ex);
        }
    }

    private void print(String text) {
        if (isEnableLogging)
            Log.d(TAG, text);
    }

    private void printErr(String text, Throwable th) {
        if (isEnableLogging)
            Log.e(TAG, text, th);
    }

    boolean isAlive() {
        return androidWebServer != null && androidWebServer.isAlive();
    }

    void stop() {
        if (androidWebServer != null && androidWebServer.isAlive()) {
            androidWebServer.stop();
            print("Android Remote Debugger is stopped.");
        }

        androidWebServer = null;
        instance = null;
    }
}
