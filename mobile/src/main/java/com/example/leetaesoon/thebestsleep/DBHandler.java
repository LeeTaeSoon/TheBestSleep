package com.example.leetaesoon.thebestsleep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hyun on 2018-05-16.
 */

public class DBHandler extends SQLiteOpenHelper implements Serializable {
    Context m_context;
    public static final String DATABASE_NAME = "productDB.db";
    public static final String DATABASE_TABLE_SPEAKER = "speakers";
    public static final String SPEAKER_ADDRESS = "address";
    public static final String SPEAKER_NAME="name";

    public static final String DATABASE_TABLE_PLUG_USER = "userInfo";
    public static final String PLUG_USER_ID = "id";
    public static final String PLUG_USER_PASSWORD = "password";
    public static final String PLUG_USER_TOKEN = "token";

    public static final String DATABASE_TABLE_LIGHT = "lights";

    public static final String DATABASE_TABLE_ACCELERATION = "acceleration";
    public static final String ACCELERATION_ID="id";
    public static final String ACCELERATION_TIME="time";
    public static final String ACCELERATION_X = "x";
    public static final String ACCELERATION_Y = "y";
    public static final String ACCELERATION_Z = "z";

    public static final String DATABASE_TABLE_GYRO = "gyro";
    public static final String GYRO_ID = "id";
    public static final String GYRO_TIME = "time";
    public static final String GYRO_X = "x";
    public static final String GYRO_Y = "y";
    public static final String GYRO_Z = "z";

    public static final String DATABASE_TABLE_HEARTRATE = "heartRate";
    public static final String HEARTRATE_ID = "id";
    public static final String HEARTRATE_RATE = "rate";
    public static final String HEARTRATE_TIME = "time";




    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
        m_context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_SPEAKER);
        String CREATE_SPEAKER_TABLE = "create table if not exists  " + DATABASE_TABLE_SPEAKER + "(" + SPEAKER_ADDRESS +
                " text primary key, " + SPEAKER_NAME + " text)";
        db.execSQL(CREATE_SPEAKER_TABLE);// Speaker DB 생성.

        //Plug DB 생성
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_PLUG_USER);
        String CREATE_PLUG_TABLE = "create table if not exists  " + DATABASE_TABLE_PLUG_USER + "(" + PLUG_USER_ID +
                " text primary key, " + PLUG_USER_TOKEN + " text)";
        db.execSQL(CREATE_PLUG_TABLE);

        //Light DB 생성


        //watch 에서 받아오는 정보
        //가속도
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_ACCELERATION);
        String CREATE_ACCELERATION_TABLE = "create table if not exists  " + DATABASE_TABLE_ACCELERATION + "(" + ACCELERATION_ID+
                " integer primary key autoincrement, " + ACCELERATION_TIME +" integer, "+ ACCELERATION_X +" real, "+ ACCELERATION_Y + " real, "+ ACCELERATION_Z +" real)";
        db.execSQL(CREATE_ACCELERATION_TABLE);
        //자이로
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_GYRO);
        String CREATE_GYRO_TABLE = "create table if not exists  " + DATABASE_TABLE_GYRO + "(" + GYRO_ID+
                " integer primary key autoincrement, " + GYRO_TIME +" integer, "+ GYRO_X +" real, "+ GYRO_Y + " real, "+ GYRO_Z +" real)";
        db.execSQL(CREATE_GYRO_TABLE);
        //심박수
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_HEARTRATE);
        String CREATE_HEARTRATE_TABLE = "create table if not exists  " + DATABASE_TABLE_HEARTRATE + "(" + HEARTRATE_ID+
                " integer primary key autoincrement, "+ HEARTRATE_TIME+" integer, " + HEARTRATE_RATE +" integer)";
        db.execSQL(CREATE_HEARTRATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_SPEAKER);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_PLUG_USER);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_ACCELERATION);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_GYRO);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_HEARTRATE);
        onCreate(db);
    }

    public void addSpeaker(PairedDevice product){//DB에 새로운 스피커를 추가하는 부분.
        //Toast.makeText(m_context, product.getPName() + " " + String.valueOf(product.getPNum()),Toast.LENGTH_SHORT).show();

        ContentValues value = new ContentValues();
        value.put(SPEAKER_ADDRESS,product.getAddress());
        value.put(SPEAKER_NAME,product.getName());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE_SPEAKER,null,value);//null은 행이 비어있을 때 비어있는 것을 표현하기 위해 null사용
        db.close();
    }

    public boolean deleteSpeaker(String productAddress)//speaker에서 조작 할 것을 삭제 할 때 delete를 해주자. 파라메터로 Mac 주소를 넘겨준다.
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+DATABASE_TABLE_SPEAKER+" where "+SPEAKER_ADDRESS
                +"=\'"+productAddress+"\'";
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            db.delete(DATABASE_TABLE_SPEAKER,SPEAKER_ADDRESS+"=?",new String[]{cursor.getString(0)});
            //Toast.makeText(m_context,cursor.getString(0),Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return true;
        }
        db.close();
        return false;
    }

//    public boolean updateProduct(Product argproduct)
//    {
//        Product product = selectProduct(argproduct.getName());
//        if(product!=null)
//        {
//            ContentValues value = new ContentValues();
//            value.put(SPEAKER_ADDRESS,argproduct.getName());
//            value.put(SPEAKER_NAME,argproduct.getLat());
//            value.put(COLUMN_LNG,argproduct.getLng());
//            SQLiteDatabase db = this.getWritableDatabase();
//            db.update(DATABASE_TABLE_SPEAKER,value,SPEAKER_ADDRESS +"=\'"+product.getName()+"\'",null);
//            // db.update(DATABASE_TABLE_SPEAKER,value,SPEAKER_ADDRESS +"=?",new String[]{String.valueOf(product.getID())});
//            db.close();
//            return true;
//        }
//        else
//            return false;
//    }

    public PairedDevice selectSpeaker(String productName)//사용자가 잠에 들었을 때 현재 켜져있는 블루투스 장치가 DB에 있는지 확인, 있으면 블루투스 OFF, 없으면(Null이면) 끝
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_SPEAKER+" WHERE "
                +SPEAKER_ADDRESS+"=\'"+productName+"\'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        PairedDevice product = new PairedDevice();

        if(cursor.moveToFirst())
        {
            product.setAddress(cursor.getString(0));
            product.setName(cursor.getString(1));
        }
        else
            product=null;
        cursor.close();
        db.close();
        return product;
    }

//DB 전체를 가져오고 싶을 때 사용하자.
    public ArrayList<PairedDevice> getSpeakerDB()
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_SPEAKER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<PairedDevice> listData = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            String address="";
            String name="";

            while(!cursor.isAfterLast())
            {
                for(int i=0;i<cursor.getColumnCount();i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case SPEAKER_ADDRESS:
                            address= cursor.getString(i);
                            break;
                        case SPEAKER_NAME:
                            name = cursor.getString(i);
                            break;
                    }
                }

                PairedDevice product = new PairedDevice(address,name);
                listData.add(product);
                cursor.moveToNext();
            }
        }
        else
        {
            listData = null;
        }
        cursor.close();
        db.close();
        return listData;
    }

    public void addPlugUser(KasaInfo product){//plug DB에 새로운 user를 추가하는 부분.

        ContentValues value = new ContentValues();
        value.put(PLUG_USER_ID,product.getUserId());
        value.put(PLUG_USER_TOKEN,product.getUserToken());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE_PLUG_USER,null,value);
        db.close();
    }
    public boolean deletePlugUser(String productid)//Logout 시 저장되어 있던 유저 정보 삭제.
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+DATABASE_TABLE_PLUG_USER+" where "+PLUG_USER_ID
                +"=\'"+productid+"\'";
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            db.delete(DATABASE_TABLE_PLUG_USER,PLUG_USER_ID+"=?",new String[]{cursor.getString(0)});
            //Toast.makeText(m_context,cursor.getString(0),Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return true;
        }
        db.close();
        return false;
    }
    public KasaInfo selectPlugUser(String productid)//
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_PLUG_USER+" WHERE "
                +PLUG_USER_ID+"=\'"+productid+"\'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        KasaInfo product = new KasaInfo();

        if(cursor.moveToFirst())
        {
            product.setUserId(cursor.getString(0));
            product.setUserToken(cursor.getString(1));
        }
        else
            product=null;
        cursor.close();
        db.close();
        return product;
    }

    public ArrayList<KasaInfo> getPlugUserDB()
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_PLUG_USER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<KasaInfo> listData = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            String id="";
            String token="";

            while(!cursor.isAfterLast())
            {
                for(int i=0;i<cursor.getColumnCount();i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case PLUG_USER_ID:
                            id= cursor.getString(i);
                            break;
                        case PLUG_USER_TOKEN:
                            token = cursor.getString(i);
                            break;
                    }
                }

                KasaInfo product = new KasaInfo(id,token);
                listData.add(product);
                cursor.moveToNext();
            }
        }
        else
        {
            listData = null;
        }
        cursor.close();
        db.close();
        return listData;
    }

    public void addAcceleration(Acceleration product){//가속도 값 DB에 추가.

        ContentValues value = new ContentValues();
        value.put(ACCELERATION_TIME,product.getAccelerationTime());
        value.put(ACCELERATION_X,product.getAccelerationX());
        value.put(ACCELERATION_Y,product.getAccelerationY());
        value.put(ACCELERATION_Z,product.getAccelerationZ());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE_ACCELERATION,null,value);
        db.close();
    }

    public void addAllAcceleration(ArrayList<Acceleration> products) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < products.size(); i++) {
                ContentValues value = new ContentValues();
                value.put(ACCELERATION_TIME,products.get(i).getAccelerationTime());
                value.put(ACCELERATION_X,products.get(i).getAccelerationX());
                value.put(ACCELERATION_Y,products.get(i).getAccelerationY());
                value.put(ACCELERATION_Z,products.get(i).getAccelerationZ());
                db.insert(DATABASE_TABLE_ACCELERATION, null, value);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Acceleration> getAccelerationDB()// 가속도 DB 전체 가져오기.
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_ACCELERATION;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<Acceleration> listData = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            long time = 0;
            double x = 0;
            double y = 0;
            double z = 0;

            while(!cursor.isAfterLast())
            {
                for(int i=0;i<cursor.getColumnCount();i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case ACCELERATION_TIME:
                            time = cursor.getLong(i);
                            break;
                        case ACCELERATION_X:
                            x = cursor.getDouble(i);
                            break;
                        case ACCELERATION_Y:
                            y = cursor.getDouble(i);
                            break;
                        case ACCELERATION_Z:
                            z = cursor.getDouble(i);
                            break;
                    }
                }

                Acceleration product = new Acceleration(time,x,y,z);
                listData.add(product);
                cursor.moveToNext();
            }
        }
        else
        {
            listData = null;
        }
        cursor.close();
        db.close();
        return listData;
    }

    //Acceleration Table 삭제
    public void deleteAccelerationDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_ACCELERATION,null,null);
    }

    //Gyro.
    public void addGyro(Gyro product){//자이로 값 DB에 추가.

        ContentValues value = new ContentValues();
        value.put(GYRO_TIME,product.getGyroTime());
        value.put(GYRO_X,product.getGyroX());
        value.put(GYRO_Y,product.getGyroY());
        value.put(GYRO_Z,product.getGyroZ());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE_GYRO,null,value);
        db.close();
    }

    public ArrayList<Gyro> getGyroDB()// 자이로 DB 전체 가져오기.
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_GYRO;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<Gyro> listData = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            long time = 0;
            double x = 0;
            double y = 0;
            double z = 0;

            while(!cursor.isAfterLast())
            {
                for(int i=0;i<cursor.getColumnCount();i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case GYRO_TIME:
                            time = cursor.getLong(i);
                            break;
                        case GYRO_X:
                            x = cursor.getDouble(i);
                            break;
                        case GYRO_Y:
                            y = cursor.getDouble(i);
                            break;
                        case GYRO_Z:
                            z = cursor.getDouble(i);
                            break;
                    }
                }

                Gyro product = new Gyro(time,x,y,z);
                listData.add(product);
                cursor.moveToNext();
            }
        }
        else
        {
            listData = null;
        }
        cursor.close();
        db.close();
        return listData;
    }

    public void deleteGyroDB(){//Gyro Table 삭제
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_GYRO,null,null);
    }

    // 심박수
    public void addHeartRate(HeartRate product){//심박수 값 DB에 추가.

        ContentValues value = new ContentValues();
        value.put(HEARTRATE_TIME,product.getHeartRatetime());
        value.put(HEARTRATE_RATE,product.getHeartRateRate());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE_HEARTRATE,null,value);
        db.close();
    }

    public void addAllHeartRate(ArrayList<HeartRate> products) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < products.size(); i++) {
                ContentValues value = new ContentValues();
                value.put(HEARTRATE_TIME, products.get(i).getHeartRatetime());
                value.put(HEARTRATE_RATE, products.get(i).getHeartRateRate());
                db.insert(DATABASE_TABLE_HEARTRATE, null, value);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<HeartRate> getHeartRateDB()//심박수 DB 전체 가져오기.
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_HEARTRATE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<HeartRate> listData = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            int rate = 0;
            long time = 0;
            while(!cursor.isAfterLast())
            {
                for(int i=0;i<cursor.getColumnCount();i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case HEARTRATE_RATE:
                            rate = cursor.getInt(i);
                            break;
                        case HEARTRATE_TIME:
                            time = cursor.getLong(i);
                            break;
                    }
                }

                HeartRate product = new HeartRate(rate, time);
                listData.add(product);
                cursor.moveToNext();
            }
        }
        else
        {
            listData = null;
        }
        cursor.close();
        db.close();
        return listData;
    }
    public void deleteHeartRateDB(){//Gyro Table 삭제
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_HEARTRATE,null,null);
    }
}
