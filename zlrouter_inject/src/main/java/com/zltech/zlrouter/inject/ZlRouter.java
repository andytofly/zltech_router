package com.zltech.zlrouter.inject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.zltech.zlrouter.annotation.RouteMeta;
import com.zltech.zlrouter.inject.callback.OnResultCallback;
import com.zltech.zlrouter.inject.exception.NoRouteFoundException;
import com.zltech.zlrouter.inject.template.IRouteGroup;
import com.zltech.zlrouter.inject.template.IRouteRoot;
import com.zltech.zlrouter.inject.template.AbsComponent;
import com.zltech.zlrouter.inject.template.RtResult;
import com.zltech.zlrouter.inject.utils.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import static com.zltech.zlrouter.annotation.RouteMeta.Type.ACTIVITY;
import static com.zltech.zlrouter.annotation.RouteMeta.Type.COMPONENT;

public class ZlRouter {

    /***
     *  路由地址格式
     * "/xxx/yyy...."
     *  /xxx 代表的是组名； /yyy...代表的是xxx组所在的某个被调用模块
     */

    private static final String TAG = "ZlRouter ";
    private static final String ROUTE_ROOT_PAKCAGE = "com.zltech.zlrouter.generators";
    private static final String SDK_NAME = "ZltechRouter";
    private static final String SEPARATOR = "_";
    private static final String SUFFIX_ROOT = "Root";

    private static Application mContext;
    private Handler mHandler;

    private static final int InvokeType_Navigate = 1000;
    private static final int InvokeType_Call = 1001;

    /**
     * 在调用 init方法之前，编译文件已经生成完毕...
     *
     * @param application
     */
    public static void init(Application application) {
        mContext = application;
        try {
            loadInfo();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "初始化失败!", e);
        }
    }


    /**
     * 分组表制作，key(组名)-value(组对应的class文件) 【组对应的class文件，会装载各种服务，以路径来导航...不在该方法处理...】
     */
    private static void loadInfo() throws PackageManager.NameNotFoundException, InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //获得所有 apt生成的路由类的全类名 (路由表)
        Set<String> routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
        for (String className : routerMap) {
            if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                //root中注册的是分组信息 将分组信息加入仓库中
                ((IRouteRoot) Class.forName(className).getConstructor().newInstance()).loadInto(Warehouse.groupsIndex);
            } else {

            }
        }
        for (Map.Entry<String, Class<? extends IRouteGroup>> stringClassEntry : Warehouse.groupsIndex.entrySet()) {
            Log.d(TAG, "Root映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry.getValue() + "]");
        }

    }


    public Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return build(path, extractGroup(path));
        }
    }

    public Postcard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return new Postcard(path, group);
        }
    }

    /**
     * @param postcard
     * @param type
     * @return
     */
    RtResult call(final Postcard postcard, AbsComponent.InvokingType type, OnResultCallback callback) {
        try {
            /**
             *  对 postcard 进行赋值处理...
             */
            prepareCard(postcard, InvokeType_Call);
        } catch (NoRouteFoundException e) {
            e.printStackTrace();
            //没找到
            return null;
        }

        if (type == AbsComponent.InvokingType.Async) {
            postcard.getComponent().callAsync(callback);
            return null;
        } else if (type == AbsComponent.InvokingType.MainThread) {
            postcard.getComponent().callOnMainThread(callback);
            return null;
        } else {
            return postcard.getComponent().call();
        }
    }


    void navigate(final Context context, final Postcard postcard, final int requestCode) {
        try {
            /**
             *  对 postcard 进行赋值处理...
             */
            prepareCard(postcard, InvokeType_Navigate);
        } catch (NoRouteFoundException e) {
            e.printStackTrace();
            //没找到
            return;
        }

        final Context currentContext = null == context ? mContext : context;
        final Intent intent = new Intent(currentContext, postcard.getDestination());
        intent.putExtras(postcard.getExtras());
        int flags = postcard.getFlags();
        if (-1 != flags) {
            intent.setFlags(flags);
        } else if (!(currentContext instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //可能需要返回码
                if (requestCode > 0) {
                    ActivityCompat.startActivityForResult((Activity) currentContext, intent,
                            requestCode, null);
                } else {
                    ActivityCompat.startActivity(currentContext, intent, null);
                }

                //跳转完成
            }
        });
    }


    /**
     * 准备卡片
     *
     * @param card
     * @param invokeType
     */
    private void prepareCard(Postcard card, int invokeType) {
        RouteMeta routeMeta = Warehouse.routes.get(card.getPath());
        if (null == routeMeta) {
            /**
             * 获取到Group class
             */
            Class<? extends IRouteGroup> groupMeta = Warehouse.groupsIndex.get(card.getGroup());
            if (null == groupMeta) {
                throw new NoRouteFoundException("没找到对应路由：分组=" + card.getGroup() + "   路径=" + card.getPath());
            }


            IRouteGroup iGroupInstance; //new一个处理group的类
            try {
                iGroupInstance = groupMeta.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("路由分组映射表记录失败.", e);
            }
            /**
             * Warehouse.routes 装载一个分组中所有的全路径 以及 全路径对应的 RouteMeta 信息
             */
            iGroupInstance.loadInto(Warehouse.routes);
            //已经准备过了就可以移除了 (不会一直存在内存中)
            Warehouse.groupsIndex.remove(card.getGroup());
            //再次进入 else
            prepareCard(card, invokeType);
        } else {
            //类 要跳转的activity 或Component实现类
            card.setDestination(routeMeta.getDestination());
            card.setType(routeMeta.getType());

            if (invokeType == InvokeType_Navigate && card.getType() != ACTIVITY) {
                throw new RuntimeException("服务请调用navigate方法");
            }

            if (invokeType == InvokeType_Call && card.getType() != COMPONENT) {
                throw new RuntimeException("服务请调用call方法");
            }

            switch (routeMeta.getType()) {
                case COMPONENT:
                    Class<?> destination = routeMeta.getDestination();
                    /**
                     * 把服务类的调用先初始化实例，并且装载好
                     */
                    AbsComponent component = Warehouse.services.get(destination);
                    if (null == component) {
                        try {
                            component = (AbsComponent) destination.getConstructor().newInstance();
                            component.setContext(mContext);
                            component.setParams(card.getExtras());
                            Warehouse.services.put(destination, component);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    card.setService(component);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 获得组别
     *
     * @param path
     * @return
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException(path + " : 不能提取group.");
        }
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new RuntimeException(path + " : 不能提取group.");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /***SingleTon start***/
    private ZlRouter() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    private static class SingleTonHoler {
        private static ZlRouter INSTANCE = new ZlRouter();
    }

    public static ZlRouter getInstance() {
        return SingleTonHoler.INSTANCE;
    }
    /***end***/
}
