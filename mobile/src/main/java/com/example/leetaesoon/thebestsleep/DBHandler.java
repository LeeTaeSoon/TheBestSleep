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

    public static final String DATABASE_TABLE_PLUG = "plugs";
    public static final String PLUG_ID = "Id";
    public static final String PLUG_URL = "url";
    public static final String PLUG_ALIAS = "alias";
    public static final String PLUG_USER = "user";


    public static final String DATABASE_TABLE_LIGHT = "lights";

    public static final String DATABASE_TABLE_ACCELERATION = "acceleration";
    public static final String ACCELERATION_ID="id";
    public static final String ACCELERATION_TIME="time";
    public static final String ACCELERATION_X = "x";
    public static final String ACCELERATION_Y = "y";
    public static final String ACCELERATION_Z = "z";
    public static final String ACCELERATION_SCALAR = "scalar";
    public static final String ACCELERATION_SCALAR_MIN = "scalar_min";
    public static final String ACCELERATION_SCALAR_MAX = "scalar_max";

    public static final String DATABASE_TABLE_GYRO = "gyro";
    public static final String GYRO_ID = "id";
    public static final String GYRO_TIME = "time";
    public static final String GYRO_X = "x";
    public static final String GYRO_Y = "y";
    public static final String GYRO_Z = "z";
    public static final String GYRO_SCALAR = "scalar";

    public static final String DATABASE_TABLE_HEARTRATE = "heartRate";
    public static final String HEARTRATE_ID = "id";
    public static final String HEARTRATE_RATE = "rate";
    public static final String HEARTRATE_TIME = "time";

    public static final String DATABASE_TABLE_LAMP_BRIDGE = "lampBridge";
    public static final String LAMP_BRIDGE_USERNAME = "username";
    public static final String LAMP_BRIDGE_IP = "ip";

    public static final String DATABASE_TABLE_LAMP = "lamps";
    public static final String LAMP_ID ="id";
    public static final String LAMP_NAME="name";
    public static final String LAMP_R="R";
    public static final String LAMP_G="G";
    public static final String LAMP_B="B";
    public static final String LAMP_A="A";

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

        //Plug DB 생성(Plug user)
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_PLUG_USER);
        String CREATE_PLUG_USER_TABLE = "create table if not exists  " + DATABASE_TABLE_PLUG_USER + "(" + PLUG_USER_ID +
                " text primary key, " + PLUG_USER_TOKEN + " text)";
        db.execSQL(CREATE_PLUG_USER_TABLE);

        //Plug DB 생성(Plug)
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_PLUG);
        String CREATE_PLUG_TABLE = "create table if not exists  " + DATABASE_TABLE_PLUG + "(" + PLUG_ID +
                " text primary key, " + PLUG_URL + " text, "+ PLUG_ALIAS+" text, "+ PLUG_USER + " text)";
        db.execSQL(CREATE_PLUG_TABLE);

        //Light DB 생성


        //watch 에서 받아오는 정보
        //가속도
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_ACCELERATION);
        String CREATE_ACCELERATION_TABLE = "create table if not exists  " + DATABASE_TABLE_ACCELERATION + "(" + ACCELERATION_ID+
                " integer primary key autoincrement, " + ACCELERATION_TIME +" integer, "+ ACCELERATION_X +" real, "+ ACCELERATION_Y + " real, "+ ACCELERATION_Z +" real, "+ ACCELERATION_SCALAR + " real)";
        db.execSQL(CREATE_ACCELERATION_TABLE);
        //자이로
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_GYRO);
        String CREATE_GYRO_TABLE = "create table if not exists  " + DATABASE_TABLE_GYRO + "(" + GYRO_ID+
                " integer primary key autoincrement, " + GYRO_TIME +" integer, "+ GYRO_X +" real, "+ GYRO_Y + " real, "+ GYRO_Z +" real, "+ GYRO_SCALAR +" real)";
        db.execSQL(CREATE_GYRO_TABLE);
        //심박수
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_HEARTRATE);
        String CREATE_HEARTRATE_TABLE = "create table if not exists  " + DATABASE_TABLE_HEARTRATE + "(" + HEARTRATE_ID+
                " integer primary key autoincrement, "+ HEARTRATE_TIME+" integer, " + HEARTRATE_RATE +" integer)";
        db.execSQL(CREATE_HEARTRATE_TABLE);

        //조명 Bridge.
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_LAMP_BRIDGE);
        String CREATE_LAMP_BRIDGE_TABLE = "create table if not exists  " + DATABASE_TABLE_LAMP_BRIDGE + "(" + LAMP_BRIDGE_USERNAME+
                " text primary key, " + LAMP_BRIDGE_IP +" text)";
        db.execSQL(CREATE_LAMP_BRIDGE_TABLE);

        //조명
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_LAMP);
        String CREATE_LAMP_TABLE = "create table if not exists  " + DATABASE_TABLE_LAMP + "(" + LAMP_ID+
                " text primary key, " + LAMP_NAME +" text, "+ LAMP_R +" integer, "+LAMP_G +" integer, "+LAMP_B +" integer, "+LAMP_A +" integer)";
        db.execSQL(CREATE_LAMP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_SPEAKER);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_PLUG_USER);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_PLUG);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_ACCELERATION);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_GYRO);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_HEARTRATE);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_LAMP_BRIDGE);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_LAMP);
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

    //Plug DB(USER)
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

    //Plug DB(Device)
    public void addPlug(PlugItem product){//설정 시 DB 추가

        ContentValues value = new ContentValues();
        value.put(PLUG_ID,product.getdeviceId());
        value.put(PLUG_URL,product.geturl());
        value.put(PLUG_ALIAS,product.getalias());
        value.put(PLUG_USER,product.getuserId());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE_PLUG,null,value);
        db.close();
    }

    public boolean deletePlug(String productid)//해제 시 delete.
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+DATABASE_TABLE_PLUG+" where "+PLUG_ID
                +"=\'"+productid+"\'";
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            db.delete(DATABASE_TABLE_PLUG,PLUG_ID+"=?",new String[]{cursor.getString(0)});
            //Toast.makeText(m_context,cursor.getString(0),Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    public ArrayList<PlugItem> selectPlugs(String productid)//
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_PLUG+" WHERE "
                +PLUG_USER+"=\'"+productid+"\'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        ArrayList<PlugItem> listData = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            String deviceId="";
            String url="";
            String alias="";
            String userId="";
            while(!cursor.isAfterLast())
            {
                for(int i=0;i<cursor.getColumnCount();i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case PLUG_ID:
                            deviceId= cursor.getString(i);
                            break;
                        case PLUG_URL:
                            url = cursor.getString(i);
                            break;
                        case PLUG_ALIAS:
                            alias = cursor.getString(i);
                            break;
                        case PLUG_USER:
                            userId = cursor.getString(i);
                            break;
                    }
                }
                PlugItem product = new PlugItem(deviceId,url,alias,userId);
                listData.add(product);
                cursor.moveToNext();
            }
        }
        else
            listData=null;
        cursor.close();
        db.close();
        return listData;
    }

    public boolean existPlug(String productid)//
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_PLUG+" WHERE "
                +PLUG_ID+"=\'"+productid+"\'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        PlugItem product = new PlugItem();

        if(cursor.getCount()<=0)
        {
            cursor.close();
            db.close();
            return false;
        }
        else{
            cursor.close();
            db.close();
            return true;
        }
    }

    public ArrayList<PlugItem> getPlugDB()
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_PLUG;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<PlugItem> listData = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            String deviceId="";
            String url="";
            String alias="";
            String userId="";

            while(!cursor.isAfterLast())
            {
                for(int i=0;i<cursor.getColumnCount();i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case PLUG_ID:
                            deviceId= cursor.getString(i);
                            break;
                        case PLUG_URL:
                            url = cursor.getString(i);
                            break;
                        case PLUG_ALIAS:
                            alias = cursor.getString(i);
                            break;
                        case PLUG_USER:
                            userId = cursor.getString(i);
                            break;
                    }
                }

                PlugItem product = new PlugItem(deviceId,url,alias,userId);
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

    //가속도 DB
    public void addAcceleration(Acceleration product){//가속도 값 DB에 추가.

        ContentValues value = new ContentValues();
        value.put(ACCELERATION_TIME,product.getAccelerationTime());
        value.put(ACCELERATION_X,product.getAccelerationX());
        value.put(ACCELERATION_Y,product.getAccelerationY());
        value.put(ACCELERATION_Z,product.getAccelerationZ());
        value.put(ACCELERATION_SCALAR, product.getAccelerationSCALAR());

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
                value.put(ACCELERATION_SCALAR, products.get(i).getAccelerationSCALAR());
                db.insert(DATABASE_TABLE_ACCELERATION, null, value);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Acceleration> getAccelerationDB()// 가속도 DB 전체 가져오기.
    {
        int index = 0;
        int MAX_COUNT = 10000;
        ArrayList<Acceleration> listData = new ArrayList<>();

        int readCount = 0;
        do {
            String query = "SELECT * FROM " + DATABASE_TABLE_ACCELERATION + " LIMIT " + MAX_COUNT + " OFFSET "  + index;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            readCount = cursor.getCount();
            index += readCount;

            if (cursor.moveToFirst()) {
                long time = 0;
                double x = 0;
                double y = 0;
                double z = 0;
                double scalar = 0;

                while (!cursor.isAfterLast()) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        switch (cursor.getColumnName(i)) {
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
                            case ACCELERATION_SCALAR:
                                scalar = cursor.getDouble(i);
                                break;
                        }
                    }

                    Acceleration product = new Acceleration(time, x, y, z, scalar);
                    listData.add(product);
                    cursor.moveToNext();
                }
            } else {
                listData = null;
            }
            cursor.close();
            db.close();
        } while (readCount == MAX_COUNT);

        return listData;
    }

    public ArrayList<Acceleration> getAccelerationDBMinMax()
    {
        int index = 0;
        int MAX_COUNT = 10000;
        ArrayList<Acceleration> listData = new ArrayList<>();

        int readCount = 0;
        do {
            String query = "SELECT " + ACCELERATION_TIME + ",MIN(" + ACCELERATION_SCALAR + ") as " + ACCELERATION_SCALAR_MIN + ", MAX(" +
                    ACCELERATION_SCALAR + ") as " + ACCELERATION_SCALAR_MAX + " FROM " + DATABASE_TABLE_ACCELERATION + " GROUP BY " + ACCELERATION_TIME +
                    " LIMIT " + MAX_COUNT + " OFFSET " + index;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            readCount = cursor.getCount();
            index += readCount;

            if (cursor.moveToFirst()) {
                long time = 0;
                double scalarMin = 0;
                double scalarMax = 0;

                while (!cursor.isAfterLast()) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        switch (cursor.getColumnName(i)) {
                            case ACCELERATION_TIME:
                                time = cursor.getLong(i);
                                break;
                            case ACCELERATION_SCALAR_MIN:
                                scalarMin = cursor.getDouble(i);
                                break;
                            case ACCELERATION_SCALAR_MAX:
                                scalarMax = cursor.getDouble(i);
                                break;
                        }
                    }

                    Acceleration product = new Acceleration(time, 0, 0, 0, scalarMin);
                    listData.add(product);
                    product = new Acceleration(time, 0, 0, 0, scalarMax);
                    listData.add(product);
                    cursor.moveToNext();
                }
            } else {
                listData = null;
            }
            cursor.close();
            db.close();
        } while (readCount == MAX_COUNT);

        return listData;
    }

    public double getAccelerationDBAvg(long start, long end)
    {
        String query = "SELECT " + ACCELERATION_SCALAR + " FROM " + DATABASE_TABLE_ACCELERATION +
                " WHERE " + ACCELERATION_TIME + " BETWEEN " + start + " and " + end;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        double scalar = 0;
        double pre = 0;

        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                for(int i = 0; i < cursor.getColumnCount(); i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case ACCELERATION_SCALAR:
                            if (pre == 0) pre = cursor.getDouble(i);
                            else {
                                double s = cursor.getDouble(i);
                                scalar += Math.abs(s - pre);
                                pre = s;
                            }
                            break;
                    }
                }
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();

        double avg = 0.0f;
        avg = scalar / ((double) cursor.getCount() - 1);

        return avg;
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
        value.put(GYRO_SCALAR,product.getGyroSCALAR());

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
            double scalar = 0;

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
                        case GYRO_SCALAR:
                            scalar = cursor.getDouble(i);
                            break;
                    }
                }

                Gyro product = new Gyro(time,x,y,z,scalar);
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

    public int getHeartRateDBAvg(long start, long end)
    {
        String query = "SELECT AVG(" + HEARTRATE_RATE + ") as " + HEARTRATE_RATE + " FROM " + DATABASE_TABLE_HEARTRATE +
                " WHERE " + HEARTRATE_TIME + " BETWEEN " + start + " and " + end;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        double rate = 0;

        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                for(int i = 0; i < cursor.getColumnCount(); i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case HEARTRATE_RATE:
                            rate = cursor.getDouble(i);
                            break;
                    }
                }
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();

        return (int) rate;
    }

    public void deleteHeartRateDB(){//Gyro Table 삭제
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_HEARTRATE,null,null);
    }



    //Lamp Bridge
    public void addLampBridge(LampBridgeItem product){//심박수 값 DB에 추가.

        ContentValues value = new ContentValues();
        value.put(LAMP_BRIDGE_USERNAME,product.getUserName());
        value.put(LAMP_BRIDGE_IP,product.getIp());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE_LAMP_BRIDGE,null,value);
        db.close();
    }
    public ArrayList<LampBridgeItem> getLampBridgeDB()//심박수 DB 전체 가져오기.
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_LAMP_BRIDGE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<LampBridgeItem> listData = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            String username="";
            String ip="";
            while(!cursor.isAfterLast())
            {
                for(int i=0;i<cursor.getColumnCount();i++)
                {
                    switch (cursor.getColumnName(i))
                    {
                        case LAMP_BRIDGE_USERNAME:
                            username = cursor.getString(i);
                            break;
                        case LAMP_BRIDGE_IP:
                            ip = cursor.getString(i);
                            break;
                    }
                }

                LampBridgeItem product = new LampBridgeItem(username,ip);
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

    public LampBridgeItem selectLampBridge(String productName)//id를 통해 검색
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_LAMP_BRIDGE+" WHERE "
                +LAMP_BRIDGE_USERNAME+"=\'"+productName+"\'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        LampBridgeItem product = new LampBridgeItem();

        if(cursor.moveToFirst())
        {
            product.setUserName(cursor.getString(0));
            product.setIp(cursor.getString(1));
        }
        else
            product=null;
        cursor.close();
        db.close();
        return product;
    }

    public void deleteLampBridgeDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_LAMP_BRIDGE,null,null);
    }

    // LAMP
    public void addLamp(LampItem product){//심박수 값 DB에 추가.

        ContentValues value = new ContentValues();
        value.put(LAMP_ID,product.getDeviceID());
        value.put(LAMP_NAME,product.getLampName());
        value.put(LAMP_R,product.getLampR());
        value.put(LAMP_G,product.getLampG());
        value.put(LAMP_B,product.getLampB());
        value.put(LAMP_A,product.getLampA());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE_LAMP,null,value);
        db.close();
    }

    public LampItem selectLamp(String productName)//id를 통해 검색
    {
        String query = "SELECT * FROM "+DATABASE_TABLE_LAMP+" WHERE "
                +LAMP_ID+"=\'"+productName+"\'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        LampItem product = new LampItem();

        if(cursor.moveToFirst())
        {
            product.setDeviceID(cursor.getString(0));
            product.setLampName(cursor.getString(1));
            product.setLampR(cursor.getInt(2));
            product.setLampG(cursor.getInt(3));
            product.setLampB(cursor.getInt(4));
            product.setLampA(cursor.getInt(5));
            product.setLampControl(true);
        }
        else
            product=null;
        cursor.close();
        db.close();
        return product;
    }

    public void deleteLampDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_LAMP,null,null);
    }

}
