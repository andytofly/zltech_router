package com.zltech.zlrouter.inject.utils;

import java.util.TreeMap;

/**
 * @desc: 主要用于拦截器优先级
 */
public class UniqueKeyTreeMap<K, V> extends TreeMap<K, V> {

    @Override
    public V put(K key, V value) {
        if (containsKey(key)){

            throw new RuntimeException("优先级为" + key + "的拦截器已经存在，不允许再次添加同级别的拦截器！");
        }else{

            return super.put(key, value);
        }
    }
}
