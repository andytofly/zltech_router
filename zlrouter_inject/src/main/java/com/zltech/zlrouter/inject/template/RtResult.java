package com.zltech.zlrouter.inject.template;

import java.util.HashMap;
import java.util.Map;

public class RtResult {
    public boolean success = true;
    /**
     * 如果成功，储存返回结果信息
     */
    public Map<String, Object> data = new HashMap<>();

    /**
     * 如果失败，返回错误信息
     */
    public String error;

    public <T> void putResult(String key, T value) {
        data.put(key, value);
    }

    public <T> T getResult(String key) {
        Object o = data.get(key);
        if (o == null) return null;
        return (T) o;
    }

    @Override
    public String toString() {
        return "RtResult{" +
                "success=" + success +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
