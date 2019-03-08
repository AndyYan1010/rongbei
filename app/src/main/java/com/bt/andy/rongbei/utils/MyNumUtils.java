package com.bt.andy.rongbei.utils;

/**
 * @创建者 AndyYan
 * @创建时间 2019/2/14 12:59
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MyNumUtils {
    static double num;

    public static double getDoubleNum(String str) {
        try {
            num = Double.parseDouble(str);
        } catch (Exception e) {
            num = 0.00;
        }
        return num;
    }
    //DecimalFormat df = new DecimalFormat(".00");  stockInfo.setFqty(df.format(MyNumUtils.getDoubleNum(map.get("fqty"))));
}
