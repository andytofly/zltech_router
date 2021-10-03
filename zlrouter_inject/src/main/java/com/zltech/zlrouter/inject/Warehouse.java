package com.zltech.zlrouter.inject;


import com.zltech.zlrouter.annotation.RouteMeta;
import com.zltech.zlrouter.inject.template.IRouteGroup;
import com.zltech.zlrouter.inject.template.AbsComponent;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {

    // root 映射表 保存分组信息  key：GroupName value：处理该Group的Class
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();

    // group 映射表 保存组中的所有数据, 在生成的Name_Group_xx代码中去装在 路径-RouteMeta的 Map，装载跳转的或者处理业务功能的类，
    static Map<String, RouteMeta> routes = new HashMap<>();

    // group 映射表 保存组中的所有数据 装载
    static Map<Class, AbsComponent> services = new HashMap<>();
    // TestServiceImpl.class , TestServiceImpl 没有再反射
}
