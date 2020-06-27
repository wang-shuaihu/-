package com.example.imoocstepapp.step;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.imoocstepapp.R;
import com.example.imoocstepapp.beans.PedometerChartBean;
import com.example.imoocstepapp.frame.BaseActivity;
import com.example.imoocstepapp.frame.LogWriter;
import com.example.imoocstepapp.service.IPedometerService;
import com.example.imoocstepapp.service.PedometerService;
import com.example.imoocstepapp.utiles.Utiles;
import com.example.imoocstepapp.widgets.CircleProgressBar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {
    private CircleProgressBar progressBar;
    private TextView textCalorie;
    private TextView time;
    private TextView distance;
    private TextView stepCount;
    private Button reset;
    private Button btnStart;
    private BarChart dataChart;
    private IPedometerService remoteService;
    private int status = -1;
    private static final int STATUS_NOT_RUNNING = 0;
    public static final int STATUS_RUNNING = 1;
    private boolean isRunning = false;
    private boolean isChartUpdate = false;
    public static final int MESSAGE_UPDATE_STEP_COUNT = 1000;
    public static final int MESSAGE_UPDATE_CHART_DFATA = 2000;
    public static final int GET_DATA_TIME = 200;
    private static final long GET_CHART_DATA_TIME = 60000L;
    private PedometerChartBean chartBean;
    private boolean bindService=false;
    private ImageView setting;

    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.act_home);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(5000);
        progressBar.setMaxProgress(10000);
        textCalorie = findViewById(R.id.textCalorie);
        time = findViewById(R.id.time);
        setting=findViewById(R.id.imageView);
        distance = findViewById(R.id.distance);
        stepCount = findViewById(R.id.stepCount);
        reset = findViewById(R.id.reset);
        btnStart = findViewById(R.id.btnStart);
        dataChart = findViewById(R.id.dataChart);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("确认重置");
                builder.setMessage("您的记录将要被清除,确定吗?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (remoteService!=null) {
                            try {
                                remoteService.stopCount();
                                remoteService.resetCount();
                                chartBean=remoteService.getChartData();
                                updateChart(chartBean);
                                status=remoteService.getServiceStatus();
                               if (status == PedometerService.STATUS_RUNNING){
                                   btnStart.setText("停止");
                               }else if(status==PedometerService.STATUS_NOT_RUN){
                                   btnStart.setText("启动");
                               }


                            } catch (RemoteException e) {
                               LogWriter.d(e.toString());
                            }
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", null);
                AlertDialog resetDlg=builder.create();
                resetDlg.show();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    status = remoteService.getServiceStatus();
                } catch (RemoteException e) {
                    LogWriter.d(e.toString());
                }
                if (status == STATUS_RUNNING && remoteService != null) {
                    try {
                        remoteService.stopCount();
                        btnStart.setText("启动");
                        isRunning = false;
                        isChartUpdate = false;

                    } catch (RemoteException e) {
                        LogWriter.d(e.toString());
                    }
                } else if (status == STATUS_NOT_RUNNING && remoteService != null) {
                    try {
                        remoteService.startCount();
                        startStepCount();
                    } catch (RemoteException e) {
                        LogWriter.d(e.toString());
                    }


                }
            }
        });

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        //服务连接成功,回调方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IPedometerService.Stub.asInterface(service);
            try {
                status = remoteService.getServiceStatus();
                if (status == STATUS_RUNNING) {
                    startStepCount();
                } else {
                    btnStart.setText("启动");
                }

            } catch (RemoteException e) {
                LogWriter.d(e.toString());
            }
        }

        //服务连接失败,回调方法
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void startStepCount() throws RemoteException {
        btnStart.setText("停止");
        isChartUpdate = true;
        isRunning = true;
        chartBean = (PedometerChartBean) remoteService.getChartData();
        updateChart(chartBean);
        //启动两个线程刷新UI
        new Thread(new StepRunnable()).start();
        new Thread(new ChartRunnale()).start();
    }

    @Override
    protected void onRequestData() {
        //检查服务是否运行
        //服务没有运行,启动服务,如果服务已经运行,直接绑定服务
        Intent serviceIntent=new Intent(this,PedometerService.class);
        if(!Utiles.isServiceRunning(this, PedometerService.class.getName())){
            //服务没有运行,启动服务
            startService(serviceIntent);
        }else {
            //服务运行
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        }
        //绑定服务
        bindService=bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE);
        //初始化一些对应状态,按钮文字
        if (bindService&&remoteService!=null){
        try {
            status=remoteService.getServiceStatus();
            if (status== PedometerService.STATUS_RUNNING) {
                 btnStart.setText("启动");
            }else if (status== PedometerService.STATUS_RUNNING){
                btnStart.setText("停止");
                isRunning=true;
                isChartUpdate=true;
                //启动两个线程刷新UI
                new Thread(new StepRunnable()).start();
                new Thread(new ChartRunnale()).start();
            }
        } catch (RemoteException e) {
           LogWriter.e(e.toString());
        }

        }else {
            btnStart.setText("启动");
        }

    }

    private class StepRunnable implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                try {
                    status = remoteService.getServiceStatus();
                    if (status == STATUS_RUNNING) {
                        handler.removeMessages(MESSAGE_UPDATE_STEP_COUNT);
                        //发送消息,让Handler去更新数据
                        handler.sendEmptyMessage(MESSAGE_UPDATE_STEP_COUNT);
                        Thread.sleep(GET_DATA_TIME);
                    }
                } catch (RemoteException | InterruptedException e) {
                    LogWriter.d(e.toString());
                }
            }
        }
    }

    private class ChartRunnale implements Runnable {

        @Override
        public void run() {
            while (isChartUpdate) {
                try {
                    chartBean = (PedometerChartBean) remoteService.getChartData();
                    handler.removeMessages(MESSAGE_UPDATE_CHART_DFATA);
                    handler.sendEmptyMessage(MESSAGE_UPDATE_CHART_DFATA);
                    Thread.sleep(GET_CHART_DATA_TIME);
                } catch (RemoteException | InterruptedException e) {
                    LogWriter.d(e.toString());
                }
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_STEP_COUNT: {
                    //更新计步器
                    updateStepCount();
                }
                break;
                case MESSAGE_UPDATE_CHART_DFATA:
                    if (chartBean != null) {
                        updateChart(chartBean);
                    }
                    break;
                default:
                    LogWriter.d("Default=" + msg.what);
            }
            super.handleMessage(msg);
        }
    };

    public void updateChart(PedometerChartBean bean) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        if (chartBean != null) {
            for (int i = 0; i <= chartBean.getIndex(); i++) {
                xVals.add(String.valueOf(i) + "分");
                int valY = chartBean.getArrayData()[i];
                yVals.add(new BarEntry(valY, i));
            }
            time.setText(String.valueOf(chartBean.getIndex()) + "分");
            BarDataSet set1 = new BarDataSet(yVals, "所走步数");
            //set1.setBarSpacePercent(2f);
            ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
            dataSets.add(set1);
            BarData data = new BarData();
            data.setValueTextSize(10f);
            dataChart.setData(data);
            dataChart.invalidate();
        }
    }

    private void updateStepCount() {
        if (remoteService != null) {
            int stepCountVal = 0;
            double calorieVal = 0;
            double distanceVal = 0;

            try {
                stepCountVal = remoteService.getStepsCount();
                calorieVal = remoteService.getCalorie();
                distanceVal = remoteService.getDistance();
            } catch (RemoteException e) {
                LogWriter.d(e.toString());
            }
            stepCount.setText(String.valueOf(stepCountVal) + "步");
            textCalorie.setText(Utiles.getFormatVal(calorieVal) + "卡");
            distance.setText(Utiles.getFormatVal(distanceVal));
            progressBar.setProgress(stepCountVal);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bindService) {
            bindService=false;
            isChartUpdate=false;
            isRunning=false;
            unbindService(serviceConnection);
        }
    }
}
