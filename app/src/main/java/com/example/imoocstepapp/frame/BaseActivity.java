package com.example.imoocstepapp.frame;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity {
    //是否显示App标题
 protected boolean isHideAppTitle=true;
 //是否显示系统标题
 protected boolean isHideSysTitle=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.onInitVariable();
        if(this.isHideAppTitle){
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        super.onCreate(savedInstanceState);
        if(this.isHideSysTitle){
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        //构造View,绑定事件
        this.onInitView(savedInstanceState);
        //请求数据
        this.onRequestData();
        FrameApplication.addToActivityList(this);
    }
    protected void onDestory(){
        FrameApplication.removeFromActivityList(this);
        super.onDestroy();

    }

    /**
     * 初始化变量,最先调用,用于初始化一些变量,创建一些对象
     */
    protected abstract void onInitVariable();

    /**
     * 初始化UI,布局载入操作
     * @param savedInstanceState
     */
    protected abstract void onInitView(final Bundle savedInstanceState);

    /**
     * 请求数据
     */
    protected abstract void onRequestData();
}
