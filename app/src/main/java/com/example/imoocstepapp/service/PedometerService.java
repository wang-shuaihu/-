package com.example.imoocstepapp.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;


import com.example.imoocstepapp.beans.PedometerBean;
import com.example.imoocstepapp.beans.PedometerChartBean;
import com.example.imoocstepapp.db.DBHelper;
import com.example.imoocstepapp.frame.FrameApplication;
import com.example.imoocstepapp.utiles.ACache;
import com.example.imoocstepapp.utiles.Setting;
import com.example.imoocstepapp.utiles.Utiles;

public class PedometerService extends Service {
    public static final String DB_NAME = "PedometerDB";
    private SensorManager sensorManager;
    private PedometerBean pedometerBean;
    private PedometerListener pedometerListener;
    public static final int STATUS_NOT_RUN = 0;
    public static final int STATUS_RUNNING = 0;
    private int runStatus = STATUS_NOT_RUN;
    private Setting settings;
    private PedometerChartBean pedometerChartBean;
    private static Handler handler = new Handler();
    public static final long SAVE_CHART_TIME = 60000L;
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (runStatus == STATUS_RUNNING) {
                if (handler != null && pedometerChartBean != null) {
                    handler.removeCallbacks(timeRunnable);
                    updateChartData();//更新数据
                    handler.postDelayed(timeRunnable, SAVE_CHART_TIME);
                }

            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pedometerBean = new PedometerBean();
        pedometerListener = new PedometerListener(pedometerBean);
        pedometerChartBean = new PedometerChartBean();
        settings = new Setting(this);
    }

    //更新图标数据
    private void updateChartData() {
        if (pedometerChartBean.getIndex() < 1440 - 1) {
            pedometerChartBean.setIndex(pedometerChartBean.getIndex() + 1);
            pedometerChartBean.getArrayData()[pedometerChartBean.getIndex()]
                    = pedometerBean.getStepCount();

        }
    }

    /**
     * 将对象保存
     */
    private void saveChartData() {
        String jsonStr = Utiles.objToJson(pedometerChartBean);
        ACache.get(FrameApplication.getInstance()).put("JsonChartData", jsonStr);
    }

    private IPedometerService.Stub iPedometerService = new IPedometerService.Stub() {

        @Override
        public void startCount() throws RemoteException {
            if (sensorManager != null && pedometerListener != null) {
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(pedometerListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                pedometerBean.setStartTime(System.currentTimeMillis());
                pedometerBean.setDay(Utiles.getTimestempDyDay());//记录是哪天的数据
                runStatus = STATUS_NOT_RUN;
                handler.postDelayed(timeRunnable, SAVE_CHART_TIME);//开始出发数据刷新
            }
        }

        @Override
        public void stopCount() throws RemoteException {
            if (sensorManager != null && pedometerListener != null) {
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.unregisterListener(pedometerListener, sensor);
                runStatus = STATUS_NOT_RUN;
                handler.removeCallbacks(timeRunnable);

            }

        }

        @Override
        public void resetCount() throws RemoteException {
            if (pedometerBean != null) {
                pedometerBean.reset();
                saveData();
            }
            if (pedometerChartBean!=null) {
                pedometerChartBean.reset();
                saveData();
            }
            if (pedometerListener != null) {
                pedometerListener.setCurrentSteps(0);
            }
        }

        @Override
        public int getStepsCount() throws RemoteException {
            if (pedometerBean != null) {
                return pedometerBean.getStepCount();
            }
            return 0;
        }

        @Override
        public double getCalorie() throws RemoteException {
            if (pedometerBean != null) {
                return Utiles.getCalorrieBySteps(pedometerBean.getStepCount());
            }
            return 0;
        }

        @Override
        public double getDistance() throws RemoteException {
            return getDistanceVal();
        }

        @Override
        public void saveData() throws RemoteException {
            if (pedometerBean != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DBHelper dbHelper = new DBHelper(PedometerService.this, DB_NAME);
                        //设置距离
                        pedometerBean.setDistance(getDistanceVal());
                        //设置热量消耗
                        pedometerBean.setCalorie(Utiles.getCalorrieBySteps(pedometerBean.getStepCount()));
                        //
                        long time = (pedometerBean.getLastStepTime() - pedometerBean.getStartTime()) / 1000;
                        if (time == 0) {
                            pedometerBean.setPace(0);//设置多少步/分钟
                            pedometerBean.setSpeed(0);
                        } else {
                            int pace = Math.round(60 * pedometerBean.getStepCount() / time);
                            pedometerBean.setPace(pace);
                            long speed = Math.round((pedometerBean.getDistance() / 1000) / (time / 60 * 60));
                            pedometerBean.setSpeed(speed);
                        }
                        dbHelper.writeToDatabase(pedometerBean);
                    }
                }) {

                }.start();
            }

        }

        @Override
        public void setSensitivity(double sensitivity) throws RemoteException {
            if (settings != null) {
                settings.setSensitivity((float) sensitivity);
            }
        }

        @Override
        public double getSensitivity() throws RemoteException {
            if (settings != null) {
                return settings.getSensitivity();
            }
            return 0;
        }

        @Override
        public void setInterval(int interval) throws RemoteException {
            if (settings != null) {
                settings.setInterval(interval);
            }
        }

        @Override
        public int getInterval() throws RemoteException {
            if (settings != null) {

                return settings.getInterval();
            }
            return 0;
        }

        @Override
        public long getStartTimeStamp() throws RemoteException {
            if (pedometerBean != null) {
                return pedometerBean.getStartTime();
            }
            return 0L;
        }

        @Override
        public int getServiceStatus() throws RemoteException {
            return runStatus;
        }
//有问题

        @Override
        public PedometerChartBean getChartData() throws RemoteException {
            return pedometerChartBean;
        }


    };

    private double getDistanceVal() {
        if (pedometerBean != null) {
            return Utiles.getDistanceVal(pedometerBean.getStepCount());
        }
        return 0;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return iPedometerService;
    }
}
