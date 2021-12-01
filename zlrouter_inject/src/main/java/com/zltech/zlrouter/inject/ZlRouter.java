package com.zltech.zlrouter.inject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.zltech.zlrouter.inject.thread.ZltechPoolExecutor;
import com.zltech.zlrouter.inject.utils.ClassUtils;
import com.zltech.zlrouter.inject.utils.LogUtil;
import com.zltech.zlrouter.inject.utils.PackageUtils;
import com.zltech.zlrouter.inject.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.zltech.zlrouter.annotation.RouteMeta.Type.ACTIVITY;

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

    public static final String ZLROUTER_SP_CACHE_KEY = "ZLROUTER_SP_CACHE_KEY";
    public static final String ZLROUTER_SP_KEY_MAP = "ZLROUTER_SP_KEY_MAP";

    public static final String LAST_VERSION_NAME = "LAST_VERSION_NAME";
    public static final String LAST_VERSION_CODE = "LAST_VERSION_CODE";

    private static Application application;
    private Handler mHandler;

    private static final int InvokeType_Navigate = 1000;
    private static final int InvokeType_Call = 1001;


    /**
     * 在调用 init方法之前，编译文件已经生成完毕...
     *
     * @param _application
     */
    private static AtomicBoolean initDone = new AtomicBoolean(false);

    /**
     * 注意，如果设置isDebug = true，那么每次都会从dexFile加载，非常耗时间，仅用于调试；所以在调式模式，也要根据实际情况设置该参数;
     *
     * @param debuggable
     * @param _application
     */
    public static void init(boolean debuggable, Application _application) {
        setDebuggable(debuggable);
        init(_application);
    }

    private static void init(Application _application) {
        application = _application;
        ZltechPoolExecutor.getInstance().execute(() -> {
            try {
                LogUtil.d(Const.TAG, " zlrouter即将初始化");
                loadInfo();
                initDone.set(true);
                LogUtil.d(Const.TAG, " zlrouter初始化完成!!!");
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(Const.TAG, "zlrouter初始化失败!", e);
                initDone.set(false);
            }
        });
    }

    private static boolean debuggable = false;

    public static void setDebuggable(boolean _isDebug) {
        debuggable = _isDebug;
        LogUtil.setLog(debuggable);
        Log.d(Const.TAG, " setLog debuggable = " + debuggable);
    }

    public static boolean debuggable() {
        return debuggable;
    }

    /**
     * 分组表制作，key(组名)-value(组对应的class文件) 【组对应的class文件，会装载各种服务，以路径来导航...不在该方法处理...】
     */
    private static void loadInfo() throws PackageManager.NameNotFoundException, InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //获得所有 apt生成的路由类的全类名 (路由表)
        long startTime = System.currentTimeMillis();
        LogUtil.fd(Const.TAG, Thread.currentThread().toString() + " loadInfo start time—> " + Utils.formatTime1());

        Set<String> routerMap;
        boolean debuggable = ZlRouter.debuggable();
        boolean newVersion = PackageUtils.isNewVersion(application);
        LogUtil.fd(Const.TAG, "debuggable " + debuggable + " ,newVersion " + newVersion);
        if (debuggable || newVersion) {
            routerMap = ClassUtils.getFileNameByPackageName(application, ROUTE_ROOT_PAKCAGE);
            Log.d(Const.TAG, " debuggable || 首次运行 || 版本更新，解析dex，获取路由表......");
            PackageUtils.updateVersion(application);
        } else {
            SharedPreferences sp = application.getSharedPreferences(ZLROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE);
            routerMap = sp.getStringSet(ZLROUTER_SP_KEY_MAP, new HashSet<>());
            Log.d(Const.TAG, "从缓存中获取路由表,size = " + routerMap.size());
        }

        for (String className : routerMap) {
            if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                //root中注册的是分组信息 将分组信息加入仓库中
                ((IRouteRoot) Class.forName(className).getConstructor().newInstance()).loadInto(Warehouse.groupsIndex);
            } else {

            }
        }
        LogUtil.fd(Const.TAG, Thread.currentThread().toString() + " loadInfo end time—> " + (System.currentTimeMillis() - startTime) + "ms");
        for (Map.Entry<String, Class<? extends IRouteGroup>> stringClassEntry : Warehouse.groupsIndex.entrySet()) {
            Log.d(TAG, "Root映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry.getValue() + "]");
        }
    }


    public JumpCard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else if (!initDone.get()) {
            throw new RuntimeException("zlrouter没有初始化完成，无法调用!");
        } else {
            return build(path, extractGroup(path));
        }
    }

    public JumpCard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return new JumpCard(path, group);
        }
    }


    RtResult dispatch(final Context context, final JumpCard jumpCard, AbsComponent.InvokingType type, OnResultCallback callback) {
        try {
            /**
             *  对 jumper 进行赋值处理...
             */
            prepareCard(context, jumpCard, InvokeType_Navigate);
        } catch (NoRouteFoundException e) {
            e.printStackTrace();
            //没找到
            return null;
        }
        if (jumpCard.getType() == ACTIVITY) {
            //如果Activity的跳转，会忽视InvokingType
            Log.w(TAG, "Activity 级别的跳转，忽视InvokingType");
            navigate(context, jumpCard, callback);
            return null;
        } else {
            return callComponent(context, jumpCard, type, callback);
        }
    }


    /**
     * @param jumpCard
     * @param type
     * @return
     */
    RtResult callComponent(Context context, final JumpCard jumpCard, AbsComponent.InvokingType type, OnResultCallback callback) {
        if (type == AbsComponent.InvokingType.Async) {
            jumpCard.getComponent().callAsync(callback);
            return null;
        } else if (type == AbsComponent.InvokingType.MainThread) {
            jumpCard.getComponent().callOnMainThread(callback);
            return null;
        } else {
            return jumpCard.getComponent().call();
        }
    }


    /**
     * Activity回调的保存,如果是Component，是调用完直接回调，不需要保存；
     */
    private ConcurrentHashMap<String, OnResultCallback> activityCallbackMap = new ConcurrentHashMap<>();

    public void onActivityResult(Activity activity, RtResult result) {
        OnResultCallback resultCallback = null;
        String canonicalName = activity.getClass().getCanonicalName();
        if (canonicalName != null && activityCallbackMap.containsKey(canonicalName)) {
            resultCallback = activityCallbackMap.get(canonicalName);
        }
        if (resultCallback != null) {
            resultCallback.onResult(result);
        }
    }

    void navigate(final Context context, final JumpCard jumpCard, OnResultCallback callback) {

        Class<?> destination = jumpCard.getDestination();
        if (callback != null && destination != null) {
            String canonicalName = destination.getCanonicalName();
            if (canonicalName != null) {
                activityCallbackMap.put(canonicalName, callback);
            }
        }

        final Context currentContext = null == context ? application : context;
        final Intent intent = new Intent(currentContext, destination);
        intent.putExtras(jumpCard.getExtras());
        int flags = jumpCard.getFlags();
        if (-1 != flags) {
            intent.setFlags(flags);
        } else if (!(currentContext instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //可能需要返回码
//                if (requestCode > 0) {
//                    ActivityCompat.startActivityForResult((Activity) currentContext, intent,
//                            requestCode, null);
//                } else {
//                    ActivityCompat.startActivity(currentContext, intent, null);
//                }
                ActivityCompat.startActivity(currentContext, intent, null);

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
    private void prepareCard(Context context, JumpCard card, int invokeType) {
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
            prepareCard(context, card, invokeType);
        } else {
            //类 要跳转的activity 或Component实现类
            card.setDestination(routeMeta.getDestination());
            card.setType(routeMeta.getType());


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
                            final Context currentContext = null == context ? application : context;
                            component.setContext(currentContext);
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
        if (!initDone.get()) {
            Log.e(Const.TAG, "ZlRouter 静态方法没有初始化完成，还不能调用!!!");
        }
        return SingleTonHoler.INSTANCE;
    }
    /***end***/
}
