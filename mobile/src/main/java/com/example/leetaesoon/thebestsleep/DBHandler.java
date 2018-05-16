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

    public static final String DATABASE_TABLE_PLUG = "plugs";

    public static final String DATABASE_TABLE_LIGHT = "lights";


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
        m_context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_SPEAKER);
        String CREATE_TABLE = "create table if not exists  " + DATABASE_TABLE_SPEAKER + "(" + SPEAKER_ADDRESS +
                " text primary key, " + SPEAKER_NAME + " text)";
        db.execSQL(CREATE_TABLE);// Speaker DB 생성.

        //Plug DB 생성


        //Light DB 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_SPEAKER);
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
}
