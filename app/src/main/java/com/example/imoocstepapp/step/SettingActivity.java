package com.example.imoocstepapp.step;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.imoocstepapp.R;
import com.example.imoocstepapp.frame.BaseActivity;
import com.example.imoocstepapp.frame.LogWriter;
import com.example.imoocstepapp.utiles.Setting;
import com.example.imoocstepapp.utiles.Utiles;

public class SettingActivity extends BaseActivity {
    private String[] listTitle={"设置步长","设置体重","传感器灵敏度","传感器采样时间"};
    private ListView settingListView;
    private ImageView back;

    static  class ViewHolder{
        TextView title;
        TextView desc;
    }

    public class SettingListAdapter extends BaseAdapter {

        private Setting setting=null;
        private String[] listTitle={"设置步长","设置体重","传感器灵敏度","传感器采样时间"};
        public SettingListAdapter(){
            setting=new Setting(SettingActivity.this);
        }
        @Override
        public int getCount() {
            return listTitle.length;
        }

        @Override
        public Object getItem(int position) {
            if (listTitle!=null&&position<listTitle.length) {

                return listTitle[position];
            }
            return 0;

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder=null;
            if (convertView==null) {
                viewHolder=new ViewHolder();
                convertView=View.inflate(SettingActivity.this, R.layout.item_setting, null);
                viewHolder.title=convertView.findViewById(R.id.title);
                viewHolder.desc=convertView.findViewById(R.id.desc);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
                viewHolder.title.setText(listTitle[position]);
               switch (position){
                   case 0:
                   {
                       float stepLen=setting.getSetpLength();
                       viewHolder.desc.setText(String.format("计算距离和消耗的热量:%s CM", stepLen));
                       convertView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               //设置步长
                           }
                       });
                   }
                   break;
                   case 1:
                   {
                       float bodyWeight=setting.getBodyWeight();
                       viewHolder.desc.setText(String.format("通过体重计算消耗的热量:%s kg", bodyWeight));
                       convertView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               //设置体重
                           }
                       });
                   }
                   break;
                   case 2:
                   {
                       double sensitivity=  setting.getSensitivity();
                       viewHolder.desc.setText(String.format("传感器的敏感程度:%s ", Utiles.getFormatVal(sensitivity, "#.00")));
                       convertView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               //传感器的敏感程度
                           }
                       });
                   }
                   break;
                   case 3:
                   {
                       int interval=setting.getInterval();
                       viewHolder.desc.setText(String.format("每隔:%s毫秒进行一次数据采集 ", Utiles.getFormatVal(interval,"#.00")));
                       convertView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               //
                           }
                       });
                   }
                   break;
                   default:{
                       LogWriter.d("Position"+position);
                   }
               }
            return convertView;
        }
    }
    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
     setContentView(R.layout.act_setting);
     settingListView=findViewById(R.id.listView);
        back=findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           finish();
            }
        });
        settingListView.setAdapter(new SettingListAdapter());
    }

    @Override
    protected void onRequestData() {

    }
}
