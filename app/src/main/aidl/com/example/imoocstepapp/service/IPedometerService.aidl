// IPedometerService.aidl
package com.example.imoocstepapp.service;
import com.example.imoocstepapp.beans.IPedometerChartBean;
// Declare any non-default types here with import statements

interface IPedometerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
      //开始计步
     void startCount();
                       //结束计步
                          void stopCount();
                    //重置计步
                          void resetCount();
                    //获取步数
                          int getStepsCount();
                    //获取消耗的能量
                        double getCalorie();
                    //获取距离
                        double getDistance();
                    //保存数据
                        void saveData();
                    //设置传感器灵敏度
                        void setSensitivity(double sensitivity);
                    //设置采样时间间隔
                        double  getSensitivity();
                        //设置采样时间
                        void setInterval(int interval);
                    //获取采样时间
                        int getInterval();
                    //获取开始时间
                        long getStartTimeStamp();
                        //获取服务运行状态
                     int getServiceStatus();
                     //获取运动运动图标数据
                     PedometerChartBean getChartData();

}
