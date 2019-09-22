package com.sarproj.remotedebugger.source.local;

import android.text.TextUtils;

public class StatusCodeFilter {
    public int minStatusCode = -1;
    public int maxStatusCode = -1;

    public StatusCodeFilter(String rawStatusCodeFilter) {
        if (TextUtils.isEmpty(rawStatusCodeFilter)) {
            return;
        }

        String[] splitted = rawStatusCodeFilter.split("[ ,\\-]");

        for (String item: splitted) {
            try {
                if (minStatusCode == -1) {
                    minStatusCode = Integer.parseInt(item);
                } else if (maxStatusCode == -1) {
                    maxStatusCode = Integer.parseInt(item);
                } else {
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }

        if (minStatusCode != -1 && maxStatusCode == -1) {
            maxStatusCode = minStatusCode;
        }
    }

    public boolean isExistCondition() {
        return minStatusCode != -1 && maxStatusCode != -1;
    }
}
