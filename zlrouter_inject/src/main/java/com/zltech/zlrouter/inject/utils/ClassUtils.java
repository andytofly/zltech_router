package com.zltech.zlrouter.inject.utils;

import static com.zltech.zlrouter.inject.ZlRouter.LAST_VERSION_CODE;
import static com.zltech.zlrouter.inject.ZlRouter.LAST_VERSION_NAME;
import static com.zltech.zlrouter.inject.ZlRouter.ZLROUTER_SP_CACHE_KEY;
import static com.zltech.zlrouter.inject.ZlRouter.ZLROUTER_SP_KEY_MAP;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.zltech.zlrouter.inject.Const;
import com.zltech.zlrouter.inject.thread.ZltechPoolExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import dalvik.system.DexFile;

public class ClassUtils {

    /**
     * 获得程序所有的apk(instant run会产生很多split apk)
     *
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    private static List<String> getSourcePaths(Context context) throws PackageManager.NameNotFoundException {
        long startTime = System.currentTimeMillis();
        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        List<String> sourcePaths = new ArrayList<>();
        sourcePaths.add(applicationInfo.sourceDir);
        //instant run
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null != applicationInfo.splitSourceDirs) {
                sourcePaths.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
            }
        }
        LogUtil.fd(Const.TAG, " getSourcePaths time elapsed " + (System.currentTimeMillis() - startTime) + "ms");
        return sourcePaths;
    }

    /**
     * 得到路由表的类名
     *
     * @param context
     * @param packageName
     * @return
     * @throws PackageManager.NameNotFoundException
     * @throws InterruptedException
     */
    public static Set<String> getFileNameByPackageName(Application context, final String packageName)
            throws PackageManager.NameNotFoundException, InterruptedException {
        final Set<String> classNames = new HashSet<>();
        List<String> paths = getSourcePaths(context);
        LogUtil.fd(Const.TAG,"getSourcePaths size "+paths.size());
        //使用同步计数器判断均处理完成
        LogUtil.fd(Const.TAG, Thread.currentThread().toString() + " getFileNameByPackageName start time—> " + Utils.formatTime1());
        long startTime = System.currentTimeMillis();
        final CountDownLatch countDownLatch = new CountDownLatch(paths.size());

        for (final String path : paths) {
            ZltechPoolExecutor.getInstance().execute(() -> {
                DexFile dexFile = null;
                long startTimeSub = System.currentTimeMillis();
                int size = 0;
                int filterSize = 0;
                try {
                    //加载 apk中的dex 并遍历 获得所有包名为 {packageName} 的类
                    LogUtil.fd(Const.TAG, " ****************** dexFile path *********************** " + path);

                    DexFile dexfile;
                    if (!path.endsWith(".zip")) {
                        dexfile = new DexFile(path);
                        LogUtil.fd(Const.TAG,"~~use new DexFile ");
                    } else {
                        //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
                        dexfile = DexFile.loadDex(path, path + ".tmp", 0);
                        LogUtil.fd(Const.TAG,"~~use DexFile.loadDex ");
                    }

                    Enumeration<String> dexEntries = dexfile.entries();

                    while (dexEntries.hasMoreElements()) {
                        String className = dexEntries.nextElement();
                        if (!TextUtils.isEmpty(className) && className.startsWith(packageName)) {
                            LogUtil.fd(Const.TAG, "className " + className);
                            classNames.add(className);
                            filterSize ++;
                        }
                        size++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != dexFile) {
                        try {
                            dexFile.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //释放一个
                    LogUtil.fd(Const.TAG, " path:" + path + " dex elements size " + size+" filterSize "+filterSize);
                    LogUtil.fd(Const.TAG, " path:" + path + " time elapsed " + (System.currentTimeMillis() - startTimeSub) + "ms");
                    countDownLatch.countDown();
                }
            });
        }
        //等待执行完成
        countDownLatch.await();

        LogUtil.fd(Const.TAG,"will write router table to sp ");
        SharedPreferences sp = context.getSharedPreferences(ZLROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE);
        sp.edit().putStringSet(ZLROUTER_SP_KEY_MAP, classNames).apply();

        LogUtil.fd(Const.TAG, Thread.currentThread().toString() + " getFileNameByPackageName end time elapsed—> " + (System.currentTimeMillis() - startTime) + "ms");
        return classNames;
    }


}
