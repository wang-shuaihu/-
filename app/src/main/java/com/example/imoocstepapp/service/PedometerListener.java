package com.example.imoocstepapp.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import com.example.imoocstepapp.beans.PedometerBean;

public class PedometerListener implements SensorEventListener {


    //当前步数
    private int currentSteps = 0;
    public void setCurrentSteps(int step){

        currentSteps=step;
    }
    //灵敏度
    private float sensitivity = 30;
    //采样时间
    private long mLimit = 300;
    //最后保存的数值
    private float mLastValue;
    //放大值
    private float mScale = -4f;
    //偏移值
    private float offset = 240f;
    //开始时间
    private long start = 0;

    private long end = 0;
    //方向
    private float mLastDirection;
    //记录数值
    private float mLastExtremes[][] = new float[2][1];
    //最后一次变化量
    private float mLastDiff;
    //是否匹配
    private int mLastMatch = -1;
    private PedometerBean data;
    public PedometerListener(PedometerBean data){

        this.data=data;

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float sum = 0;
                for (int i = 0; i < 3; i++) {
                    float vector = offset + event.values[i] * mScale;
                    sum += vector;
                }
                //传感器取平均值
                float average=sum/3;
                float dir;
                //判断了方向
                if(average>mLastValue){
                    dir=1;

                }else if(average<mLastValue){
                    dir=-1;
                }
                else {

                    dir=0;
                }
                //如果和上次相反
                if(dir==-mLastDirection){
                    int extType=(dir>0?0:1);
                    //保存数值变化
                    mLastExtremes[extType][0]=mLastValue;
                    //变化的绝对值
                    float diff=Math.abs(mLastExtremes[extType][0]-mLastExtremes[1-extType][0]);
                    if(diff>sensitivity){
                        //数值以上次比是否足够大
                        boolean isLargeAsprevious=diff>(mLastDiff*2/3);
                        //数值是否小于上次的三倍
                        boolean isPreviousLargeEnough=diff<mLastDiff*3;
                        //方向判断
                        boolean isNotContra=(mLastMatch!=1-extType);
                        if(isLargeAsprevious&&isPreviousLargeEnough&&isNotContra){
                            //这是一次有效的记录
                            end=System.currentTimeMillis();
                            if(end-start>mLimit){
                                currentSteps++;
                                mLastMatch=extType;
                                start=end;
                                mLastDiff=diff;
                                if(data!=null){
                                    data.setStepCount(currentSteps);
                                    data.setLastStepTime(System.currentTimeMillis());
                                }
                            }else {
                                mLastDiff=sensitivity;

                            }
                        }else {//未匹配
                            mLastMatch=-1;
                            mLastDiff=sensitivity;
                        }
                    }
                }mLastDirection=dir;
                mLastValue=average;

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
