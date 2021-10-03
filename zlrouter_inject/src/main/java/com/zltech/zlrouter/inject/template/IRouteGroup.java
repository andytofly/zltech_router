package com.zltech.zlrouter.inject.template;


import com.zltech.zlrouter.annotation.RouteMeta;

import java.util.Map;


public interface IRouteGroup {
    void loadInto(Map<String, RouteMeta> atlas);
}
