package com.bt.andy.rongbei;

import android.app.Activity;
import android.app.Application;

import com.bt.andy.rongbei.utils.ExceptionUtil;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.ArrayList;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/22 8:51
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MyAppliaction extends Application {
    public static boolean             isRelease    = false;
    public static ArrayList<Activity> listActivity = new ArrayList<Activity>();
    public static int                 flag         = -1;//判断是否被回收
    public static Long   userID;//用户id
    public static Long   fjianyanyuan;//操作员id
    public static String uerName;//
    public static String userType;//用户类别
    public static String userRight;//用户主管权限

    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
    }

    public static void exit() {
        try {
            for (Activity activity : listActivity) {
                activity.finish();
            }
            // 结束进程
            System.exit(0);
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }
}
