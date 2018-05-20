package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity{

    Intent intent;
    public static DBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DBHandler(this,DBHandler.DATABASE_NAME,null,1);//DBHander 생성

        //여기부터 DB추가한 부분. 여기는 지우고 다른곳에서 사용하면 됨.
        //Acceleration은 Gyro와 같은 방법으로 하면 됨.
        Gyro acc1 = new Gyro("201311231",1.0,2.0,3.0);//Gyro 객체 만드는 방법.
        Gyro acc2 = new Gyro("20180520",6.0,8.0,10.0);
        dbHandler.addGyro(acc1);//db핸들러에 Gyro 객체 추가.(DB에 추가됨)

        ArrayList<Gyro> list = new ArrayList<>();//db에 있는 Gyro 정보를 모두 가져오기 위한 Gyro List

        if(dbHandler.getGyroDB() != null)//gyroDB에 저장된 것이 있을 때
        {
            list.addAll(dbHandler.getGyroDB());//GyroDB에 있는 정보를 모두 가져와 list에 저장

            for(Gyro ac : list)//list에 있는 정보를 하나씩 log로 볼 수 있음.
            {
                Log.d("Gyro", " ( TIME : " +ac.getGyroTime() + " X : "+ac.getGyroX()+" Y : "+ac.getGyroY() + " Z : "+ac.getGyroZ());
            }
        }
        dbHandler.deleteGyroDB();//현재 GyroDB에 있는 정보를 모두 삭제
        list = new ArrayList<>();//초기화 하지 않으면 위에 생성된 list에 추가되어 보이므로 delete된 것이 안보임.
        if(dbHandler.getGyroDB() != null)
        {
            list.addAll(dbHandler.getGyroDB());

            for(Gyro ac : list)
            {
                Log.d("Gyro", " ( TIME : " +ac.getGyroTime() + " X : "+ac.getGyroX()+" Y : "+ac.getGyroY() + " Z : "+ac.getGyroZ());
            }
        }
        else{//DB에 아무것도 없을 때.(delete가 정상적으로 된 경우 No db가 나오는게 정상.)
            Log.d("Gyro", "NO db");
        }

        dbHandler.addGyro(acc2);
        list = new ArrayList<>();
        if(dbHandler.getGyroDB() != null)
        {
            list.addAll(dbHandler.getGyroDB());

            for(Gyro ac : list)
            {
                Log.d("Gyro", " ( TIME : " +ac.getGyroTime() + " X : "+ac.getGyroX()+" Y : "+ac.getGyroY() + " Z : "+ac.getGyroZ());
            }
        }
        else{
            Log.d("Gyro", "NO db");
        }

        //심박수 객체 추가
        HeartRate hr1 = new HeartRate(64);//심박수 객체 생성(심박수 값만 넣어주면 된다.)
        ArrayList<HeartRate> list2;//DB에 저장된 심박수 객체를 불러와 저장할 list2
        list2 = new ArrayList<>();
        dbHandler.addHeartRate(hr1);//hr1이라는 심박수 객체를 db에 저장.

        if(dbHandler.getHeartRateDB() != null)
        {
            list2.addAll(dbHandler.getHeartRateDB());

            for(HeartRate ac : list2)
            {
                Log.d("Gyro", " ( HeartRate : " +ac.getHeartRateRate() );
            }
        }
        //심박수 db 삭제
        dbHandler.deleteHeartRateDB();
        list2 = new ArrayList<>();
        if(dbHandler.getHeartRateDB() != null)
        {
            list2.addAll(dbHandler.getHeartRateDB());

            for(HeartRate ac : list2)
            {
                Log.d("Gyro", " ( HeartRate : " +ac.getHeartRateRate() );
            }
        }
        else{
            Log.d("Gyro", " There is  No HeartRate DB" );
        }
        //심박수 객체 다시 추가
        HeartRate hr2 = new HeartRate(100);
        dbHandler.addHeartRate(hr2);
        list2 = new ArrayList<>();
        if(dbHandler.getHeartRateDB() != null)
        {
            list2.addAll(dbHandler.getHeartRateDB());

            for(HeartRate ac : list2)
            {
                Log.d("Gyro", " ( HeartRate : " +ac.getHeartRateRate() );
            }
        }

    //여기까지 DB.
    }

    public void logShow(View view) {
        intent = new Intent(MainActivity.this, sleepRecord.class);
        startActivity(intent);
    }

    public void plugSetting(View view) {
        intent = new Intent(MainActivity.this, plugSignIn.class);
        startActivity(intent);
    }

    public void speakerSetting(View view) {
        intent = new Intent(MainActivity.this, BluetoothSpeaker.class);
//        intent.putExtra("DB", dbHandler);
        startActivity(intent);
    }

    public void LampSetting(View view) {
        intent = new Intent(MainActivity.this, LampSelect.class);
        startActivity(intent);
    }
}
////
