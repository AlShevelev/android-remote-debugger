package com.sarproj.remotedebugger.api.base;

import android.content.Context;

import com.google.gson.Gson;
import com.sarproj.remotedebugger.source.local.Theme;
import com.sarproj.remotedebugger.utils.NumberUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public abstract class Api {
    protected static final String EMPTY = "";
    protected Context context;
    private Gson gson;
    protected static final int DEFAULT_FONT_SIZE = 12;
    protected static final Theme DEFAULT_THEME = Theme.DARK;

    public Api(Context context) {
        this.context = context;
        gson = new Gson();
    }

    public abstract String execute(Map<String, List<String>> params) throws NanoHTTPD.ResponseException;

    @SuppressWarnings("ConstantConditions")
    protected boolean containsValue(Map<String, List<String>> params, String key) {
        return params.containsKey(key) && params.get(key) != null && !params.get(key).isEmpty();
    }

    @SuppressWarnings("ConstantConditions")
    protected String getValue(Map<String, List<String>> params, String key) {
        return params.get(key).get(0);
    }

    @SuppressWarnings("ConstantConditions")
    protected String getStringValue(Map<String, List<String>> params, String key) {
        if (!containsValue(params, key)) {
            return null;
        }
        return params.get(key).get(0);
    }

    @SuppressWarnings("ConstantConditions")
    protected int getIntValue(Map<String, List<String>> params, String key) {
        if (!containsValue(params, key)) {
            return 0;
        }

        String rawValue = params.get(key).get(0);
        if (!NumberUtils.isInt(rawValue)) {
            return 0;
        }

        return Integer.parseInt(rawValue);
    }

    @SuppressWarnings("ConstantConditions")
    protected boolean getBooleanValue(Map<String, List<String>> params, String key) {
        if (!containsValue(params, key)) {
            return false;
        }

        return Boolean.parseBoolean(params.get(key).get(0));
    }

    protected String serialize(Object object) {
        return gson.toJson(object);
    }

    protected <T> T deserialize(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    protected <T> List<T> deserialize(String json, Type type) {
        return new Gson().fromJson(json, type);
    }

    protected void throwEmptyParameterException(String parameter) throws NanoHTTPD.ResponseException {
        throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST,
                "'" + parameter + "' parameter not found");
    }

    public void destroy() {
        context = null;
        gson = null;
    }
}
