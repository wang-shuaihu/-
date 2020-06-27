package com.example.imoocstepapp.beans;

public class PedometerBean {
    //所走步数
    private int stepCount;
    //消耗卡路里
    private double calorie;
    //所走距离
    private double distance;
    //步/每分钟
    private int pace;
    //速度
    private double speed;
    //开始记录时间
    private long startTime;
    //最后一步的时间
    private long lastStepTime;
    //以天为单位的时间戳
    private long day;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getPace() {
        return pace;
    }

    public void setPace(int pace) {
        this.pace = pace;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getLastStepTime() {
        return lastStepTime;
    }

    public void setLastStepTime(long lastStepTime) {
        this.lastStepTime = lastStepTime;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }
    public void reset(){

        stepCount=0;
        calorie=0;
        distance=0;
    }

}
