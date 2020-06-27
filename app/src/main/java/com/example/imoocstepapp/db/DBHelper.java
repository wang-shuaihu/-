package com.example.imoocstepapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.example.imoocstepapp.beans.PedometerBean;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    public static final String TABLE_NAME = "pedometer";
    public static final String[] COLUMNS = {
            "id",
            "stepCount",
            "calorie",
            "distance",
            "pace",
            "speed",
            "startTime",
            "lastStepTime",
            "day"
    };
    public DBHelper(Context context,String name){
        this(context,name,VERSION);
    }
    public DBHelper(Context context,String name,int version){

        this(context,name,null,version);
    }

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (id integer PRIMARY KEY AUTOINCREMENT DEFAULT NULL," +
                "stepCount integer, "
                + "calorie Double,"
                + "distance Double,"
                + "pace integer,"
                + "speed double,"
                + "startTime timestamp Default null,"
                + "lastStepTime timestamp Default null,"
                + "day timestamp Default null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 将数据写入数据库
     * @param data
     */

    public void writeToDatabase(PedometerBean data) {
   SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("id",data.getId());
        values.put("stepCount",data.getStepCount());
        values.put("calorie",data.getCalorie());
        values.put("distance",data.getDistance());
        values.put("pace",data.getPace());
        values.put("speed",data.getSpeed());
        values.put("startTime",data.getStartTime());
        values.put("lastStepTime",data.getLastStepTime());
        values.put("day",data.getDay());
        db.insert(DBHelper.TABLE_NAME,null,values);
        db.close();

    }

    /**
     * 根据天为单位获取基本数据
     * @param dayTime
     * @return
     */
    public PedometerBean getByDayTime(long dayTime){
        Cursor cursor=null;
        PedometerBean bean=new PedometerBean();
        SQLiteDatabase db=this.getWritableDatabase();
        cursor=db.rawQuery("select *from"+DBHelper.TABLE_NAME+
                " where day="+String.valueOf(dayTime),null);
        if(cursor!=null&&cursor.getCount()>0){
            if (cursor.moveToNext())
            {

                int id=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[0]));
                int stepCount=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[1]));
                int calorie=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[2]));
                int distance=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[3]));
                int pace=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[4]));
                int speed=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[5]));
                int startTime=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[6]));
                int lastStepTime=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[7]));
                int day=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[8]));
                bean.setId(id);
                bean.setStepCount(stepCount);
                bean.setCalorie(calorie);
                bean.setDistance(distance);
                bean.setPace(pace);
                bean.setSpeed(speed);
                bean.setStartTime(startTime);
                bean.setLastStepTime(lastStepTime);
                bean.setDay(day);
                return bean;
            }
        }
        cursor.close();
        db.close();
        return bean;
    }

    /**
     * 获取全部数据,分页
     * @param offVal
     * @return
     */
    public ArrayList<PedometerBean> getFromDatabase(int offVal){
        int pageSize=20;
        Cursor cursor=null;
        SQLiteDatabase db=this.getWritableDatabase();
        cursor=db.query(TABLE_NAME,null,null,null,
                null,null,
                "day desc limit"+String.valueOf(pageSize)+
                        "offset"+String.valueOf(offVal),null);
        ArrayList<PedometerBean> dataList=new ArrayList<PedometerBean>();
        if(cursor!=null&&cursor.getCount()>0){
            while (cursor.moveToNext()){
                PedometerBean bean=new PedometerBean();
                int id=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[0]));
                int stepCount=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[1]));
                int calorie=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[2]));
                int distance=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[3]));
                int pace=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[4]));
                int speed=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[5]));
                int startTime=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[6]));
                int lastStepTime=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[7]));
                int day=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[8]));
                bean.setId(id);
                bean.setStepCount(stepCount);
                bean.setCalorie(calorie);
                bean.setDistance(distance);
                bean.setPace(pace);
                bean.setSpeed(speed);
                bean.setStartTime(startTime);
                bean.setLastStepTime(lastStepTime);
                bean.setDay(day);
            dataList.add(bean);

            }
        }
        cursor.close();
        db.close();
        return  dataList;

}

    /**
     * 更新数据库
     * @param values
     * @param dayTime
     */
  public void updateDatabase(ContentValues values,long dayTime){

        SQLiteDatabase db=this.getWritableDatabase();
        db.update(TABLE_NAME,values, "day=?",new String[]{String.valueOf(dayTime)});
        db.close();
  }
}