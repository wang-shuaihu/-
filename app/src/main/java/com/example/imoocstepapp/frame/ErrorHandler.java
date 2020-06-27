package com.example.imoocstepapp.frame;

import android.content.Context;
import androidx.annotation.NonNull;

public class ErrorHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        LogWriter.LogToFile(true,"Error","崩溃线程信息:"+e.getMessage());
        LogWriter.LogToFile(true,"Error","崩溃线程名称"+t.getName()+"线程ID:"+t.getId());
       final StackTraceElement[] trace=e.getStackTrace();
       for(final StackTraceElement element:trace){

           LogWriter.LogToFile(true,"Error","Lines"+element.getClassName()+":"+element.getMethodName());
       }
       e.printStackTrace();
       FrameApplication.exitApp();
    }

    private static ErrorHandler instance;

    public static ErrorHandler getInstance() {

        if (ErrorHandler.instance == null) {
            ErrorHandler.instance = new ErrorHandler();
        }
        return ErrorHandler.instance;
    }
    private ErrorHandler(){

    }
    public void setErrorHandler(final Context ctx){

        Thread.setDefaultUncaughtExceptionHandler(this);
    }

}
