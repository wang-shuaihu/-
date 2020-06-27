package com.example.imoocstepapp.utiles;

import android.app.ActivityManager;
import android.content.Context;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Utiles {

    public static String objToJson(Object obj){
        Gson gson=new Gson();
        return gson.toJson(obj);
    }

    public static long getTimestempDyDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        Date d = new Date();
        String dataStr = sdf.format(d);

        try {
            Date date = sdf.parse(dataStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static double getCalorrieBySteps(int stepCount) {

        //步长
        int stepLen = 50;
        //体重
        int bodyWeight = 70;
        //走路
        double METRIC_WALKING_FACTOR = 0.708;
        //跑步
        double METRIC_RUNNING_FACTOR = 1.02784823;
        //跑步热量(kcal)-体重(kg)*距离(公里)*METRIC_RUNNING_FACTOR
        //走路热量(kcal)-体重(kg)*距离(公里)*METRIC_WALKING_FACTOR
        double calories=(bodyWeight*METRIC_WALKING_FACTOR)*stepLen*stepCount/100000.0;
        return  calories;
    }

    public static double getDistanceVal(int stepCount){
        //步长
        int stepLen=50;
        double distance=(stepCount*(long)(stepLen))/100000.0f;
        return distance;
    }
    public static  String getFormatVal(double val,String formatStr){
        DecimalFormat decimalFormat =new DecimalFormat(formatStr);
        return decimalFormat.format(val);
    }


    public static  String getFormatVal(double val){

        return getFormatVal(val,"0.00");
    }

    /**
     * 判断服务是否被运行
     * @param ctx
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context ctx,String serviceName){
        boolean isRunning=false;
        if (ctx==null||serviceName==null) {
            return isRunning;
        }
        ActivityManager activityManager= (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List serviceList=activityManager.getRunningServices(Integer.MAX_VALUE);
        Iterator iterator=serviceList.iterator();
        while (iterator.hasNext()){
            ActivityManager.RunningServiceInfo runningServiceInfo= (ActivityManager.RunningServiceInfo) iterator.next();
            if (serviceName.trim().equals(runningServiceInfo.service.getClassName())) {
                isRunning=true;
                return isRunning;

            }
        }
        return isRunning;
    }
}
